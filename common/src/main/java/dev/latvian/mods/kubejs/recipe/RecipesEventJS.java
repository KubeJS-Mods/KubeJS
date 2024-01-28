package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.WrappedException;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipesEventJS extends EventJS {
	public static final Pattern SKIP_ERROR = Pattern.compile("at.*dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipesEventJS\\.post");
	private static final Predicate<RecipeJS> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
	private static final EventExceptionHandler RECIPE_EXCEPTION_HANDLER = (event, handler, ex) -> {
		// skip the current handler on a recipe or JSON exception, but let other handlers run
		if (ex instanceof RecipeExceptionJS || ex instanceof JsonParseException) {
			ConsoleJS.SERVER.error("Error while processing recipe event handler: " + handler, ex);
			return null;
		} else {
			return ex; // rethrow
		}
	};

	@HideFromJS
	public static final Map<ResourceLocation, ModifyRecipeResultCallback> MODIFY_RESULT_CALLBACKS = new ConcurrentHashMap<>();

	// hacky workaround for parallel streams, which are executed on the common fork/join pool by default
	// and forge / event bus REALLY does not like that (plus it's generally just safer to use our own pool)
	private static final ForkJoinPool PARALLEL_THREAD_POOL = new ForkJoinPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1),
		forkJoinPool -> {
			final ForkJoinWorkerThread thread = new ForkJoinWorkerThread(forkJoinPool) {
			};
			thread.setContextClassLoader(RecipesEventJS.class.getClassLoader()); // better safe than sorry
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
	public static RecipesEventJS instance;

	public final Map<ResourceLocation, RecipeJS> originalRecipes;
	public final Collection<RecipeJS> addedRecipes;

	public final AtomicInteger failedCount;
	public final Map<ResourceLocation, RecipeJS> takenIds;

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

	final RecipeSerializer<?> stageSerializer;

	public RecipesEventJS() {
		ConsoleJS.SERVER.info("Initializing recipe event...");
		originalRecipes = new HashMap<>();
		addedRecipes = new ConcurrentLinkedQueue<>();
		recipeFunctions = new HashMap<>();
		takenIds = new ConcurrentHashMap<>();

		failedCount = new AtomicInteger(0);

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
		smithing = (RecipeTypeFunction) recipeFunctions.get("minecraft:smithing");

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

		stageSerializer = KubeJSRegistries.recipeSerializers().get(new ResourceLocation("recipestages:stage"));
	}

	@HideFromJS
	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
		ConsoleJS.SERVER.info("Processing recipes...");
		RecipeJS.itemErrors = false;

		TagContext.INSTANCE.setValue(TagContext.fromLoadResult(KubeJSReloadListener.resources.tagManager.getResult()));

		// clear recipe event specific maps
		RecipesEventJS.MODIFY_RESULT_CALLBACKS.clear();

		var timer = Stopwatch.createStarted();

		var exportedRecipes = new JsonObject();

		for (var entry : datapackRecipeMap.entrySet()) {
			var recipeId = entry.getKey();

			var recipeIdAndType = recipeId + "[unknown:type]";
			JsonObject json;

			try {
				if (recipeId == null || (Platform.isForge() && recipeId.getPath().startsWith("_"))) {
					continue; //Forge: filter anything beginning with "_" as it's used for metadata.
				}

				json = RecipePlatformHelper.get().checkConditions(GsonHelper.convertToJsonObject(entry.getValue(), "top element"));

				if (json == null) {
					if (DevProperties.get().logSkippedRecipes) {
						ConsoleJS.SERVER.info("Skipping recipe " + recipeId + ", conditions not met");
					}

					continue;
				} else if (!json.has("type")) {
					if (DevProperties.get().logSkippedRecipes) {
						ConsoleJS.SERVER.info("Skipping recipe " + recipeId + ", missing type");
					}

					continue;
				}

				if (DataExport.export != null) {
					exportedRecipes.add(recipeId.toString(), JsonUtils.copy(json));
				}
			} catch (Exception ex) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.warn("Skipping recipe %s, failed to load: ".formatted(recipeId), ex);
				}
				continue;
			}

			var typeStr = GsonHelper.getAsString(json, "type");
			recipeIdAndType = recipeId + "[" + typeStr + "]";
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
					ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType + ":");
					ConsoleJS.SERVER.printStackTrace(false, ex3, SKIP_ERROR);
				}
			}
		}

		takenIds.putAll(originalRecipes);
		ConsoleJS.SERVER.info("Found " + originalRecipes.size() + " recipes in " + timer.stop());

		timer.reset().start();
		ServerEvents.RECIPES.post(ScriptType.SERVER, this);

		int modifiedCount = 0;
		var removedRecipes = new ConcurrentLinkedQueue<RecipeJS>();

		for (var r : originalRecipes.values()) {
			if (r.removed) {
				removedRecipes.add(r);
			} else if (r.hasChanged()) {
				modifiedCount++;
			}
		}

		ConsoleJS.SERVER.info("Posted recipe events in " + timer.stop());

		timer.reset().start();
		addedRecipes.removeIf(RecipesEventJS::addedRecipeRemoveCheck);

		var recipesByName = new HashMap<ResourceLocation, Recipe<?>>(originalRecipes.size() + addedRecipes.size());

		try {
			recipesByName.putAll(runInParallel(() -> originalRecipes.values().parallelStream()
				.filter(RECIPE_NOT_REMOVED)
				.map(this::createRecipe)
				.filter(Objects::nonNull)
				.collect(Collectors.toConcurrentMap(Recipe::getId, Function.identity(), (a, b) -> {
					ConsoleJS.SERVER.warn("Duplicate recipe for id " + a.getId() + "! Using last one encountered.");
					return b;
				}))));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating datapack recipes", ex, SKIP_ERROR);
		}

		try {
			recipesByName.putAll(runInParallel(() -> addedRecipes.parallelStream()
				.map(this::createRecipe)
				.filter(Objects::nonNull)
				.collect(Collectors.toConcurrentMap(Recipe::getId, Function.identity(), (a, b) -> {
					ConsoleJS.SERVER.warn("Duplicate recipe for id " + a.getId() + "! Using last one encountered.");
					ConsoleJS.SERVER.warn("You might want to check your scripts for errors.");
					return b;
				}))));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating script recipes", ex, SKIP_ERROR);
		}

		KubeJSPlugins.forEachPlugin(p -> p.injectRuntimeRecipes(this, recipeManager, recipesByName));

		var newRecipeMap = new HashMap<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>();

		for (var entry : recipesByName.entrySet()) {
			var type = entry.getValue().getType();
			var recipes = newRecipeMap.computeIfAbsent(type, k -> new HashMap<>());
			recipes.put(entry.getKey(), entry.getValue());
		}

		RecipePlatformHelper.get().pingNewRecipes(newRecipeMap);
		recipeManager.byName = recipesByName;
		recipeManager.recipes = newRecipeMap;
		ConsoleJS.SERVER.info("Added " + addedRecipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, modified " + modifiedCount + " recipes, with " + failedCount.get() + " failed recipes in " + timer.stop());
		RecipeJS.itemErrors = false;

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
	private Recipe<?> createRecipe(RecipeJS r) {
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

	private static boolean addedRecipeRemoveCheck(RecipeJS r) {
		if (r.newRecipe) {
			// r.getOrCreateId(); // Generate ID synchronously?
			return false;
		}

		return true;
	}

	public Map<String, Object> getRecipes() {
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, boolean json) {
		if (r instanceof ErroredRecipeJS) {
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

	public RecipeFilter customFilter(Predicate<RecipeKJS> filter) {
		return filter::test;
	}

	private record RecipeStreamFilter(RecipeFilter filter) implements Predicate<RecipeJS> {
		@Override
		public boolean test(RecipeJS r) {
			return r != null && !r.removed && filter.test(r);
		}
	}

	/**
	 * Creates a <i>non-parallel</i> stream of all recipes matching the given filter.
	 * <p>
	 * You may call .parallel() on the stream yourself after construction, but note
	 * that due to a bug in Forge (which will be resolved in Minecraft 1.20), you
	 * will *have* to execute the Stream on a separate fork/join pool then!
	 */
	public Stream<RecipeJS> recipeStream(RecipeFilter filter) {
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

		return originalRecipes.values().stream().filter(new RecipeStreamFilter(filter));
	}

	/**
	 * Creates a <i>possibly parallel</i> stream of all recipes matching the given filter.
	 * Note that this should <b>only</b> be used with a terminal operation that is
	 * executed on our own fork/join pool!
	 * <p>
	 * See {@link #recipeStream(RecipeFilter)} for more information on why this needs to exist.
	 */
	@ApiStatus.Internal
	private Stream<RecipeJS> recipeStreamAsync(RecipeFilter filter) {
		var stream = recipeStream(filter);
		return CommonProperties.get().allowAsyncStreams ? stream.parallel() : stream;
	}

	private void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		runInParallel(() -> recipeStreamAsync(filter).forEach(consumer));
	}

	private <T> T reduceRecipesAsync(RecipeFilter filter, Function<Stream<RecipeJS>, T> function) {
		return runInParallel(() -> function.apply(recipeStreamAsync(filter)));
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		recipeStream(filter).forEach(consumer);
	}

	public int countRecipes(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> (int) s.count());
	}

	public boolean containsRecipe(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> s.findAny().isPresent());
	}

	public Collection<RecipeJS> findRecipes(RecipeFilter filter) {
		return reduceRecipesAsync(filter, Stream::toList);
	}

	public Collection<ResourceLocation> findRecipeIds(RecipeFilter filter) {
		return reduceRecipesAsync(filter, s -> s.map(RecipeJS::getOrCreateId).toList());
	}

	public void remove(RecipeFilter filter) {
		if (filter instanceof IDFilter id) {
			var r = originalRecipes.get(id.id);

			if (r != null) {
				r.remove();
			}
		} else {
			forEachRecipeAsync(filter, RecipeJS::remove);
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
		} else if (recipeFunctions.get(UtilsJS.getID(id)) instanceof RecipeTypeFunction fn) {
			return fn;
		} else {
			return null;
		}
	}

	public RecipeJS custom(JsonObject json) {
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
				return new ErroredRecipeJS(this, "Failed to create custom JSON recipe from '%s'".formatted(json), rex, SKIP_ERROR);
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
		printTypes(t -> KubeJSRegistries.recipeSerializers().get(t.id) != null);
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

	public synchronized ResourceLocation takeId(RecipeJS recipe, String prefix, String ids) {
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
		RecipeJS.itemErrors = b;
	}

	public void stage(RecipeFilter filter, String stage) {
		forEachRecipeAsync(filter, r -> r.stage(stage));
	}

	public static void runInParallel(final Runnable runnable) {
		PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(runnable));
	}

	public static <T> T runInParallel(final Callable<T> callable) {
		return PARALLEL_THREAD_POOL.invoke(ForkJoinTask.adapt(callable));
	}
}