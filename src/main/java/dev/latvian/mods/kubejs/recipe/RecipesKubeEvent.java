package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.helpers.RecipeHelper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.WrappedException;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipesKubeEvent implements KubeEvent {
	public static final Pattern SKIP_ERROR = Pattern.compile("dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipesEventJS\\.post");
	private static final Predicate<KubeRecipe> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
	private static final EventExceptionHandler RECIPE_EXCEPTION_HANDLER = (event, handler, ex) -> {
		// skip the current handler on a recipe or JSON exception, but let other handlers run
		if (ex instanceof RecipeExceptionJS || ex instanceof JsonParseException) {
			ConsoleJS.SERVER.error("Error while processing recipe event handler: " + handler, ex);
			return null;
		} else {
			return ex; // rethrow
		}
	};

	private String recipeToString(Recipe<?> recipe) {
		var map = new LinkedHashMap<String, Object>();
		map.put("type", RegistryInfo.RECIPE_SERIALIZER.getId(recipe.getSerializer()));

		try {
			var in = new ArrayList<>();

			for (var ingredient : recipe.getIngredients()) {
				var list = new ArrayList<String>();

				for (var item : ingredient.getItems()) {
					list.add(item.kjs$toItemString0(registries));
				}

				in.add(list);
			}

			map.put("in", in);
		} catch (Exception ex) {
			map.put("in_error", ex.toString());
		}

		try {
			var result = recipe.getResultItem(registries);
			//noinspection ConstantValue
			map.put("out", (result == null ? ItemStack.EMPTY : result).kjs$toItemString0(registries));
		} catch (Exception ex) {
			map.put("out_error", ex.toString());
		}

		return map.toString();
	}

	private static final Function<RecipeHolder<?>, ResourceLocation> RECIPE_ID = RecipeHolder::id;
	private static final Predicate<RecipeHolder<?>> RECIPE_NON_NULL = Objects::nonNull;
	private static final Function<RecipeHolder<?>, RecipeHolder<?>> RECIPE_IDENTITY = Function.identity();

	@HideFromJS
	public static final Map<ResourceLocation, ModifyRecipeResultCallback> MODIFY_RESULT_CALLBACKS = new ConcurrentHashMap<>();

	// hacky workaround for parallel streams, which are executed on the common fork/join pool by default
	// and forge / event bus REALLY does not like that (plus it's generally just safer to use our own pool)
	private static final ForkJoinPool PARALLEL_THREAD_POOL = new ForkJoinPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1),
		forkJoinPool -> {
			final ForkJoinWorkerThread thread = new ForkJoinWorkerThread(forkJoinPool) {
			};
			thread.setContextClassLoader(RecipesKubeEvent.class.getClassLoader()); // better safe than sorry
			thread.setName(String.format("KubeJS Recipe Event Worker %d", thread.getPoolIndex()));
			return thread;
		},
		(thread, ex) -> {
			while ((ex instanceof CompletionException | ex instanceof InvocationTargetException | ex instanceof WrappedException) && ex.getCause() != null) {
				ex = ex.getCause();
			}

			if (ex instanceof ReportedException crashed) {
				// crash the same way Minecraft would
				Bootstrap.realStdoutPrintln(crashed.getReport().getFriendlyReport());
				System.exit(-1);
			}

			ConsoleJS.SERVER.error("Error in thread %s while performing bulk recipe operation!".formatted(thread), ex);

			RecipeExceptionJS rex = ex instanceof RecipeExceptionJS rex1 ? rex1 : new RecipeExceptionJS(null, ex).error();

			if (rex.error) {
				throw rex;
			}
		}, true);

	@HideFromJS
	public static Map<UUID, IngredientWithCustomPredicate> customIngredientMap = null;

	@HideFromJS
	public static RecipesKubeEvent instance;

	public final HolderLookup.Provider registries;
	public final Map<ResourceLocation, KubeRecipe> originalRecipes;
	public final Collection<KubeRecipe> addedRecipes;
	private final BinaryOperator<RecipeHolder<?>> mergeOriginal, mergeAdded;


	public final AtomicInteger failedCount;
	public final Map<ResourceLocation, KubeRecipe> takenIds;

	private final Map<String, Object> recipeFunctions;
	public final transient RecipeTypeFunction vanillaShaped;
	public final transient RecipeTypeFunction vanillaShapeless;
	public final RecipeTypeFunction shaped;
	public final RecipeTypeFunction shapeless;
	public final RecipeTypeFunction smelting;
	public final RecipeTypeFunction blasting;
	public final RecipeTypeFunction smoking;
	public final RecipeTypeFunction campfireCooking;
	public final RecipeTypeFunction stonecutting;
	public final RecipeTypeFunction smithing;
	public final RecipeTypeFunction smithingTrim;

	final RecipeSerializer<?> stageSerializer;

	public RecipesKubeEvent(HolderLookup.Provider registries) {
		ConsoleJS.SERVER.info("Initializing recipe event...");
		this.registries = registries;
		this.originalRecipes = new HashMap<>();
		this.addedRecipes = new ConcurrentLinkedQueue<>();
		this.recipeFunctions = new HashMap<>();
		this.takenIds = new ConcurrentHashMap<>();

		this.mergeOriginal = (a, b) -> {
			ConsoleJS.SERVER.warn("Duplicate original recipe for id " + a.id() + "!\nRecipe A: " + recipeToString(a.value()) + "\nRecipe B: " + recipeToString(b.value()) + "\nUsing last one encountered.");
			return b;
		};

		this.mergeAdded = (a, b) -> {
			ConsoleJS.SERVER.error("Duplicate added recipe for id " + a.id() + "!\nRecipe A: " + recipeToString(a.value()) + "\nRecipe B: " + recipeToString(b.value()) + "\nUsing last one encountered.");
			return b;
		};

		this.failedCount = new AtomicInteger(0);

		var allNamespaces = RecipeNamespace.getAll();

		for (var namespace : allNamespaces.values()) {
			var nsMap = new HashMap<String, RecipeTypeFunction>();
			recipeFunctions.put(namespace.name, new NamespaceFunction(namespace, nsMap));

			for (var entry : namespace.entrySet()) {
				var func = new RecipeTypeFunction(this, entry.getValue());
				nsMap.put(entry.getValue().id.getPath(), func);
				recipeFunctions.put(entry.getValue().id.toString(), func);
			}
		}

		vanillaShaped = (RecipeTypeFunction) recipeFunctions.get("minecraft:crafting_shaped");
		vanillaShapeless = (RecipeTypeFunction) recipeFunctions.get("minecraft:crafting_shapeless");
		shaped = CommonProperties.get().serverOnly ? vanillaShaped : (RecipeTypeFunction) recipeFunctions.get("kubejs:shaped");
		shapeless = CommonProperties.get().serverOnly ? vanillaShapeless : (RecipeTypeFunction) recipeFunctions.get("kubejs:shapeless");
		smelting = (RecipeTypeFunction) recipeFunctions.get("minecraft:smelting");
		blasting = (RecipeTypeFunction) recipeFunctions.get("minecraft:blasting");
		smoking = (RecipeTypeFunction) recipeFunctions.get("minecraft:smoking");
		campfireCooking = (RecipeTypeFunction) recipeFunctions.get("minecraft:campfire_cooking");
		stonecutting = (RecipeTypeFunction) recipeFunctions.get("minecraft:stonecutting");
		smithing = (RecipeTypeFunction) recipeFunctions.get("minecraft:smithing_transform");
		smithingTrim = (RecipeTypeFunction) recipeFunctions.get("minecraft:smithing_trim");

		for (var entry : new ArrayList<>(recipeFunctions.entrySet())) {
			if (entry.getValue() instanceof RecipeTypeFunction && entry.getKey().indexOf(':') != -1) {
				var s = UtilsJS.snakeCaseToCamelCase(entry.getKey());

				if (!s.equals(entry.getKey())) {
					recipeFunctions.put(s, entry.getValue());
				}
			}
		}

		for (var entry : RecipeNamespace.getMappedRecipes().entrySet()) {
			var type = recipeFunctions.get(entry.getValue().toString());

			if (type instanceof RecipeTypeFunction) {
				recipeFunctions.put(entry.getKey(), type);
			}
		}

		recipeFunctions.put("shaped", shaped);
		recipeFunctions.put("shapeless", shapeless);
		recipeFunctions.put("smelting", smelting);
		recipeFunctions.put("blasting", blasting);
		recipeFunctions.put("smoking", smoking);
		recipeFunctions.put("campfireCooking", campfireCooking);
		recipeFunctions.put("stonecutting", stonecutting);
		recipeFunctions.put("smithing", smithing);
		recipeFunctions.put("smithingTrim", smithingTrim);

		stageSerializer = RegistryInfo.RECIPE_SERIALIZER.getValue(new ResourceLocation("recipestages:stage"));
	}

	@HideFromJS
	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
		ConsoleJS.SERVER.info("Processing recipes...");
		KubeRecipe.itemErrors = false;
		var resources = ((RecipeManagerKJS) recipeManager).kjs$getResources();
		var cx = resources.kjs$getServerScriptManager().contextFactory.enter();

		TagContext.INSTANCE.setValue(TagContext.fromLoadResult(((ReloadableServerResources) resources).tagManager.getResult()));

		// clear recipe event specific maps
		RecipesKubeEvent.MODIFY_RESULT_CALLBACKS.clear();

		var timer = Stopwatch.createStarted();

		var exportedRecipes = new JsonObject();

		for (var entry : datapackRecipeMap.entrySet()) {
			var recipeId = entry.getKey();

			if (recipeId == null || recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			var jsonResult = RecipeHelper.get().validate(recipeManager.registries, entry.getValue());
			if (jsonResult.error().isPresent()) {
				var error = jsonResult.error().get();
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.info("Skipping recipe %s, %s".formatted(recipeId, error.message()));
				}
				continue;
			}

			var json = jsonResult.getOrThrow(JsonParseException::new);
			var typeStr = GsonHelper.getAsString(json, "type");
			var recipeIdAndType = recipeId + "[" + typeStr + "]";
			var type = getRecipeFunction(typeStr);

			if (type == null) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.info("Skipping recipe " + recipeId + ", unknown type: " + typeStr);
				}

				continue;
			}

			try {
				var recipe = type.schemaType.schema.deserialize(type, recipeId, json);
				recipe.afterLoaded();
				originalRecipes.put(recipeId, recipe);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					var originalRecipe = recipe.getOriginalRecipe();
					if (originalRecipe == null || SpecialRecipeSerializerManager.INSTANCE.isSpecial(originalRecipe)) {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
					} else {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
					}
				}
			} catch (Throwable ex) {
				if (DevProperties.get().logErroringRecipes || DevProperties.get().debugInfo) {
					ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex, SKIP_ERROR);
				}

				try {
					originalRecipes.put(recipeId, JsonRecipeSchema.SCHEMA.deserialize(type, recipeId, json));
				} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
					if (DevProperties.get().logErroringRecipes || DevProperties.get().debugInfo) {
						ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2, SKIP_ERROR);
					}
				} catch (Exception ex3) {
					ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex3, SKIP_ERROR);
				}
			}
		}

		takenIds.putAll(originalRecipes);
		ConsoleJS.SERVER.info("Found " + originalRecipes.size() + " recipes in " + timer.stop());

		timer.reset().start();
		ServerEvents.RECIPES.post(ScriptType.SERVER, this);

		int modifiedCount = 0;
		var removedRecipes = new ConcurrentLinkedQueue<KubeRecipe>();

		for (var r : originalRecipes.values()) {
			if (r.removed) {
				removedRecipes.add(r);
			} else if (r.hasChanged()) {
				modifiedCount++;
			}
		}

		ConsoleJS.SERVER.info("Posted recipe events in " + timer.stop());

		timer.reset().start();
		addedRecipes.removeIf(RecipesKubeEvent::addedRecipeRemoveCheck);

		var recipesByName = new HashMap<ResourceLocation, RecipeHolder<?>>(originalRecipes.size() + addedRecipes.size());

		try {
			recipesByName.putAll(runInParallel(() -> originalRecipes.values().parallelStream()
				.filter(RECIPE_NOT_REMOVED)
				.map(this::createRecipe)
				.filter(RECIPE_NON_NULL)
				.collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, mergeOriginal))));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating datapack recipes", ex, SKIP_ERROR);
		}

		try {
			recipesByName.putAll(runInParallel(() -> addedRecipes.parallelStream()
				.map(this::createRecipe)
				.filter(RECIPE_NON_NULL)
				.collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, mergeAdded))));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating script recipes", ex, SKIP_ERROR);
		}

		KubeJSPlugins.forEachPlugin(p -> p.injectRuntimeRecipes(this, recipeManager, recipesByName));

		var recipesByType = ImmutableMultimap.<RecipeType<?>, RecipeHolder<?>>builder();

		for (var entry : recipesByName.entrySet()) {
			recipesByType.put(entry.getValue().value().getType(), entry.getValue());
		}

		recipeManager.byName = recipesByName;
		recipeManager.byType = recipesByType.build();
		ConsoleJS.SERVER.info("Added " + addedRecipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, modified " + modifiedCount + " recipes, with " + failedCount.get() + " failed recipes in " + timer.stop());
		KubeRecipe.itemErrors = false;

		if (DataExport.export != null) {
			for (var r : removedRecipes) {
				DataExport.export.addJson("removed_recipes/" + r.getId() + ".json", r.json);
			}
		}

		if (DevProperties.get().debugInfo) {
			ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");

			for (var r : addedRecipes) {
				ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json);
			}

			ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");

			for (var r : originalRecipes.values()) {
				if (!r.removed && r.hasChanged()) {
					ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json + " FROM " + r.originalJson);
				}
			}

			ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");

			for (var r : removedRecipes) {
				ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json);
			}
		}
	}

	@Nullable
	private RecipeHolder<?> createRecipe(KubeRecipe r) {
		try {
			var rec = r.createRecipe();
			var path = r.kjs$getMod() + "/" + r.getPath();

			if (!r.removed && DataExport.export != null) {
				DataExport.export.addJson("recipes/%s.json".formatted(path), r.json);

				if (r.newRecipe) {
					DataExport.export.addJson("added_recipes/%s.json".formatted(path), r.json);
				}
			}

			return rec;
		} catch (Throwable ex) {
			ConsoleJS.SERVER.warn("Error parsing recipe " + r + ": " + r.json, ex, SKIP_ERROR);
			failedCount.incrementAndGet();
			return null;
		}
	}

	private static boolean addedRecipeRemoveCheck(KubeRecipe r) {
		// r.getOrCreateId(); // Generate ID synchronously?
		return !r.newRecipe;
	}

	public Map<String, Object> getRecipes() {
		return recipeFunctions;
	}

	public KubeRecipe addRecipe(KubeRecipe r, boolean json) {
		if (r instanceof ErroredKubeRecipe) {
			ConsoleJS.SERVER.warn("Tried to add errored recipe %s!".formatted(r));
			return r;
		}

		addedRecipes.add(r);

		if (DevProperties.get().logAddedRecipes) {
			ConsoleJS.SERVER.info("+ " + r.getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.debug("+ " + r.getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		}

		return r;
	}

	public RecipeFilter customFilter(RecipeFilter filter) {
		return filter;
	}

	private record RecipeStreamFilter(HolderLookup.Provider registries, RecipeFilter filter) implements Predicate<KubeRecipe> {
		@Override
		public boolean test(KubeRecipe r) {
			return r != null && !r.removed && filter.test(registries, r);
		}
	}

	/**
	 * Creates a <i>non-parallel</i> stream of all recipes matching the given filter.
	 * <p>
	 * You may call .parallel() on the stream yourself after construction, but note
	 * that due to a bug in Forge (which will be resolved in Minecraft 1.20), you
	 * will *have* to execute the Stream on a separate fork/join pool then!
	 */
	public Stream<KubeRecipe> recipeStream(RecipeFilter filter) {
		if (filter == ConstantFilter.FALSE) {
			return Stream.empty();
		} else if (filter instanceof IDFilter id) {
			var r = originalRecipes.get(id.id);
			return r == null || r.removed ? Stream.empty() : Stream.of(r);
		}

		exit:
		if (filter instanceof OrFilter or) {
			if (or.list.isEmpty()) {
				return Stream.empty();
			}

			for (var recipeFilter : or.list) {
				if (!(recipeFilter instanceof IDFilter)) {
					break exit;
				}
			}

			return or.list.stream().map(idf -> originalRecipes.get(((IDFilter) idf).id)).filter(RECIPE_NOT_REMOVED);
		}

		return originalRecipes.values().stream().filter(new RecipeStreamFilter(registries, filter));
	}

	/**
	 * Creates a <i>possibly parallel</i> stream of all recipes matching the given filter.
	 * Note that this should <b>only</b> be used with a terminal operation that is
	 * executed on our own fork/join pool!
	 * <p>
	 * See {@link #recipeStream(RecipeFilter)} for more information on why this needs to exist.
	 */
	@ApiStatus.Internal
	private Stream<KubeRecipe> recipeStreamAsync(RecipeFilter filter) {
		var stream = recipeStream(filter);
		return CommonProperties.get().allowAsyncStreams ? stream.parallel() : stream;
	}

	private void forEachRecipeAsync(RecipeFilter filter, Consumer<KubeRecipe> consumer) {
		runInParallel(() -> recipeStreamAsync(filter).forEach(consumer));
	}

	private <T> T reduceRecipesAsync(RecipeFilter filter, Function<Stream<KubeRecipe>, T> function) {
		return runInParallel(() -> function.apply(recipeStreamAsync(filter)));
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<KubeRecipe> consumer) {
		recipeStream(filter).forEach(consumer);
	}

	public int countRecipes(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> (int) s.count());
	}

	public boolean containsRecipe(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> s.findAny().isPresent());
	}

	public Collection<KubeRecipe> findRecipes(RecipeFilter filter) {
		return reduceRecipesAsync(filter, Stream::toList);
	}

	public Collection<ResourceLocation> findRecipeIds(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> s.map(KubeRecipe::getOrCreateId).toList());
	}

	public void remove(RecipeFilter filter) {
		if (filter instanceof IDFilter id) {
			var r = originalRecipes.get(id.id);

			if (r != null) {
				r.remove();
			}
		} else {
			forEachRecipeAsync(filter, KubeRecipe::remove);
		}
	}

	public void replaceInput(RecipeFilter filter, ReplacementMatch match, InputReplacement with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug()) ? (": IN " + match + " -> " + with) : "";

		forEachRecipeAsync(filter, r -> {
			if (r.replaceInput(match, with)) {
				if (DevProperties.get().logModifiedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + dstring);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + dstring);
				}
			}
		});
	}

	public void replaceOutput(RecipeFilter filter, ReplacementMatch match, OutputReplacement with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug()) ? (": OUT " + match + " -> " + with) : "";

		forEachRecipeAsync(filter, r -> {
			if (r.replaceOutput(match, with)) {
				if (DevProperties.get().logModifiedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + dstring);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + dstring);
				}
			}
		});
	}

	public RecipeTypeFunction getRecipeFunction(@Nullable String id) {
		if (id == null || id.isEmpty()) {
			return null;
		} else if (recipeFunctions.get(ID.string(id)) instanceof RecipeTypeFunction fn) {
			return fn;
		} else {
			return null;
		}
	}

	public KubeRecipe custom(JsonObject json) {
		try {
			if (json == null || !json.has("type")) {
				throw new RecipeExceptionJS("JSON must contain 'type'!");
			}

			var type = getRecipeFunction(json.get("type").getAsString());

			if (type == null) {
				throw new RecipeExceptionJS("Unknown recipe type: " + json.get("type").getAsString());
			}

			var recipe = type.schemaType.schema.deserialize(type, null, json);
			recipe.afterLoaded();
			return addRecipe(recipe, true);
		} catch (RecipeExceptionJS rex) {
			if (rex.error) {
				throw rex;
			} else {
				return new ErroredKubeRecipe(this, "Failed to create custom JSON recipe from '%s'".formatted(json), rex, SKIP_ERROR);
			}
		}
	}

	private void printTypes(Predicate<RecipeSchemaType> predicate) {
		int t = 0;
		var map = new IdentityHashMap<RecipeSchema, Set<ResourceLocation>>();

		for (var ns : RecipeNamespace.getAll().values()) {
			for (var type : ns.values()) {
				if (predicate.test(type)) {
					t++;
					map.computeIfAbsent(type.schema, s -> new HashSet<>()).add(type.id);
				}
			}
		}

		for (var entry : map.entrySet()) {
			ConsoleJS.SERVER.info("- " + entry.getValue().stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));

			for (var c : entry.getKey().constructors().values()) {
				ConsoleJS.SERVER.info("  - " + c);
			}
		}

		ConsoleJS.SERVER.info(t + " types");
	}

	public void printTypes() {
		ConsoleJS.SERVER.info("== All recipe types [used] ==");
		var set = reduceRecipesAsync(ConstantFilter.TRUE, s -> s.map(r -> r.type.id).collect(Collectors.toSet()));
		printTypes(t -> set.contains(t.id));
	}

	public void printAllTypes() {
		ConsoleJS.SERVER.info("== All recipe types [available] ==");
		printTypes(t -> RegistryInfo.RECIPE_SERIALIZER.getValue(t.id) != null);
	}

	public void printExamples(String type) {
		var list = originalRecipes.values().stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ConsoleJS.SERVER.info("== Random examples of '" + type + "' ==");

		for (var i = 0; i < Math.min(list.size(), 5); i++) {
			var r = list.get(i);
			ConsoleJS.SERVER.info("- " + r.getOrCreateId() + ":\n" + JsonIO.toPrettyString(r.json));
		}
	}

	public synchronized ResourceLocation takeId(KubeRecipe recipe, String prefix, String ids) {
		int i = 2;
		var id = new ResourceLocation(prefix + ids);

		while (takenIds.containsKey(id)) {
			id = new ResourceLocation(prefix + ids + '_' + i);
			i++;
		}

		takenIds.put(id, recipe);
		return id;
	}

	public void setItemErrors(boolean b) {
		KubeRecipe.itemErrors = b;
	}

	public void stage(RecipeFilter filter, String stage) {
		forEachRecipeAsync(filter, r -> r.stage(stage));
	}

	public static void runInParallel(final Runnable runnable) {
		try {
			PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(runnable));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error running a recipe task", ex, SKIP_ERROR);
		}
	}

	public static <T> T runInParallel(final Callable<T> callable) {
		return PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(callable));
	}
}