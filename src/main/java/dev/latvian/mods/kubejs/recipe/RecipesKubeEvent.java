package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentException;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.error.UnknownRecipeTypeException;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.filter.RegexIDFilter;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.schema.UnknownRecipeSchema;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.server.ChangesForChat;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipesKubeEvent implements KubeEvent {
	public static final Pattern POST_SKIP_ERROR = Pattern.compile("dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipesKubeEvent\\.post");
	public static final Pattern CREATE_RECIPE_SKIP_ERROR = Pattern.compile("dev\\.latvian\\.mods\\.kubejs\\.recipe\\.RecipesKubeEvent\\.createRecipe");
	private static final Predicate<KubeRecipe> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
	private static final EventExceptionHandler RECIPE_EXCEPTION_HANDLER = (event, handler, ex) -> {
		// skip the current handler on a recipe or JSON exception, but let other handlers run
		if (ex instanceof KubeRuntimeException || ex instanceof JsonParseException) {
			ConsoleJS.SERVER.error("Error while processing recipe event handler: " + handler, ex);
			return null;
		} else {
			return ex; // rethrow
		}
	};

	private String recipeToString(Recipe<?> recipe) {
		var map = new LinkedHashMap<String, Object>();
		map.put("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()));

		try {
			var in = new ArrayList<>();

			for (var ingredient : recipe.getIngredients()) {
				var list = new ArrayList<String>();

				for (var item : ingredient.getItems()) {
					list.add(item.kjs$toItemString0(registries.nbt()));
				}

				in.add(list);
			}

			map.put("in", in);
		} catch (Exception ex) {
			map.put("in_error", ex.toString());
		}

		try {
			var result = recipe.getResultItem(registries.access());
			//noinspection ConstantValue
			map.put("out", (result == null ? ItemStack.EMPTY : result).kjs$toItemString0(registries.nbt()));
		} catch (Exception ex) {
			map.put("out_error", ex.toString());
		}

		return map.toString();
	}

	private static final Function<RecipeHolder<?>, ResourceLocation> RECIPE_ID = RecipeHolder::id;
	private static final Predicate<RecipeHolder<?>> RECIPE_NON_NULL = Objects::nonNull;
	private static final Function<RecipeHolder<?>, RecipeHolder<?>> RECIPE_IDENTITY = Function.identity();

	public final RecipeSchemaStorage recipeSchemaStorage;
	public final RegistryAccessContainer registries;
	public final ResourceManager resourceManager;
	public final RegistryOps<JsonElement> jsonOps;
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

	public RecipesKubeEvent(ServerScriptManager manager, ResourceManager resourceManager) {
		ConsoleJS.SERVER.info("Initializing recipe event...");
		this.recipeSchemaStorage = manager.recipeSchemaStorage;
		this.registries = manager.getRegistries();
		this.resourceManager = resourceManager;
		this.jsonOps = new ConditionalOps<>(registries.json(), registries);
		this.originalRecipes = new HashMap<>();
		this.addedRecipes = new ConcurrentLinkedQueue<>();
		this.recipeFunctions = new HashMap<>();
		this.takenIds = new ConcurrentHashMap<>();

		// var itemTags = manager.getLoadedTags(Registries.ITEM);
		// System.out.println(itemTags);

		this.mergeOriginal = (a, b) -> {
			ConsoleJS.SERVER.warn("Duplicate original recipe for id " + a.id() + "!\nRecipe A: " + recipeToString(a.value()) + "\nRecipe B: " + recipeToString(b.value()) + "\nUsing last one encountered.");
			return b;
		};

		this.mergeAdded = (a, b) -> {
			ConsoleJS.SERVER.error("Duplicate added recipe for id " + a.id() + "!\nRecipe A: " + recipeToString(a.value()) + "\nRecipe B: " + recipeToString(b.value()) + "\nUsing last one encountered.");
			return b;
		};

		this.failedCount = new AtomicInteger(0);

		for (var namespace : recipeSchemaStorage.namespaces.values()) {
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

		for (var entry : recipeSchemaStorage.mappings.entrySet()) {
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

		stageSerializer = BuiltInRegistries.RECIPE_SERIALIZER.get(ResourceLocation.parse("recipestages:stage"));
	}

	@HideFromJS
	public void post(RecipeManagerKJS recipeManager, Map<ResourceLocation, JsonElement> datapackRecipeMap) {
		ConsoleJS.SERVER.info("Processing recipes...");

		var timer = Stopwatch.createStarted();

		KubeJSPlugins.forEachPlugin(p -> p.beforeRecipeLoading(this, recipeManager, datapackRecipeMap));

		for (var entry : datapackRecipeMap.entrySet()) {
			var recipeId = entry.getKey();

			//Forge: filter anything beginning with "_" as it's used for metadata.
			if (recipeId == null || recipeId.getPath().startsWith("_")) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.info("Skipping recipe %s, filename starts with _".formatted(recipeId));
				}

				continue;
			}

			var originalJsonElement = entry.getValue();

			if (!originalJsonElement.isJsonObject()) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.warn("Skipping recipe %s, not a json object".formatted(recipeId));
				} else {
					RecipeManager.LOGGER.warn("Skipping recipe %s, not a json object".formatted(recipeId));
				}

				continue;
			}

			var originalJson = originalJsonElement.getAsJsonObject();

			if (!originalJson.has("type")) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.warn("Skipping recipe %s, not a json object".formatted(recipeId));
				} else {
					RecipeManager.LOGGER.warn("Skipping recipe %s, not a json object".formatted(recipeId));
				}

				continue;
			}

			var codec = ConditionalOps.createConditionalCodec(Codec.unit(originalJson));

			var jsonResult = codec.parse(jsonOps, originalJson);

			if (jsonResult.isError()) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.error("Skipping recipe %s, error parsing conditions: %s".formatted(recipeId, jsonResult.error().get().message()));
				} else {
					RecipeManager.LOGGER.error("Skipping recipe %s, error parsing conditions: %s".formatted(recipeId, jsonResult.error().get().message()));
				}

				continue;
			}

			var result = jsonResult.result().get();

			if (result.isEmpty()) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.info("Skipping recipe %s, conditions not met".formatted(recipeId));
				} else {
					RecipeManager.LOGGER.info("Skipping recipe %s, conditions not met".formatted(recipeId));
				}

				continue;
			}

			var json = result.get();
			var typeStr = GsonHelper.getAsString(json, "type");
			var recipeIdAndType = recipeId + "[" + typeStr + "]";
			var type = getRecipeFunction(typeStr);

			if (type == null) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.warn("Skipping recipe " + recipeId + ", unknown type: " + typeStr);
				} else {
					RecipeManager.LOGGER.warn("Skipping recipe " + recipeId + ", unknown type: " + typeStr);
				}

				continue;
			}

			try {
				var recipe = type.schemaType.schema.deserialize(SourceLine.UNKNOWN, type, recipeId, json);
				recipe.afterLoaded();
				originalRecipes.put(recipeId, recipe);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					var original = recipe.getOriginalRecipe();

					if (original == null || SpecialRecipeSerializerManager.INSTANCE.isSpecial(original)) {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
					} else {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
					}
				}
			} catch (InvalidRecipeComponentException ignore) {
			} catch (Throwable ex) {
				if (DevProperties.get().logErroringRecipes) {
					ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex, POST_SKIP_ERROR);
				}

				try {
					originalRecipes.put(recipeId, UnknownRecipeSchema.SCHEMA.deserialize(SourceLine.UNKNOWN, type, recipeId, json));
				} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
					if (DevProperties.get().logErroringRecipes) {
						ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2, POST_SKIP_ERROR);
					}
				} catch (Exception ex3) {
					ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex3, POST_SKIP_ERROR);
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

		ConsoleJS.SERVER.info("Posted recipe events in " + TimeJS.msToString(timer.stop().elapsed(TimeUnit.MILLISECONDS)));

		timer.reset().start();
		addedRecipes.removeIf(RecipesKubeEvent::addedRecipeRemoveCheck);

		var recipesByName = new HashMap<ResourceLocation, RecipeHolder<?>>(originalRecipes.size() + addedRecipes.size());

		try {
			recipesByName.putAll(originalRecipes.values().parallelStream()
				.filter(RECIPE_NOT_REMOVED)
				.map(this::createRecipe)
				.filter(RECIPE_NON_NULL)
				.collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, mergeOriginal)));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating datapack recipes", ex, POST_SKIP_ERROR);
		}

		try {
			recipesByName.putAll(addedRecipes.parallelStream()
				.map(this::createRecipe)
				.filter(RECIPE_NON_NULL)
				.collect(Collectors.toConcurrentMap(RECIPE_ID, RECIPE_IDENTITY, mergeAdded)));
		} catch (Throwable ex) {
			ConsoleJS.SERVER.error("Error creating script recipes", ex, POST_SKIP_ERROR);
		}

		KubeJSPlugins.forEachPlugin(p -> p.injectRuntimeRecipes(this, recipeManager, recipesByName));

		recipeManager.kjs$replaceRecipes(recipesByName);
		ChangesForChat.recipesAdded = addedRecipes.size();
		ChangesForChat.recipesModified = modifiedCount;
		ChangesForChat.recipesRemoved = removedRecipes.size();
		ChangesForChat.recipesMs = timer.stop().elapsed(TimeUnit.MILLISECONDS);

		ConsoleJS.SERVER.info("Added " + addedRecipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, modified " + modifiedCount + " recipes, with " + failedCount.get() + " failed recipes in " + TimeJS.msToString(ChangesForChat.recipesMs));

		if (DataExport.export != null) {
			for (var r : removedRecipes) {
				DataExport.export.addJson("removed_recipes/" + r.getId() + ".json", r.json);
			}
		}

		if (DevProperties.get().logRecipeDebug) {
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

		RegexIDFilter.clearInternCache();
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

			//noinspection ConstantValue
			if (rec == null || rec.value() == null) {
				return null;
			}

			return rec;
		} catch (Throwable ex) {
			ConsoleJS.SERVER.warn("Error parsing recipe " + r + ": " + r.json, ex, POST_SKIP_ERROR);
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
		addedRecipes.add(r);

		if (DevProperties.get().logAddedRecipes) {
			ConsoleJS.SERVER.info("+ " + r.getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.debug("+ " + r.getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		}

		return r;
	}

	private record RecipeStreamFilter(Context cx, RecipeFilter filter) implements Predicate<KubeRecipe> {
		@Override
		public boolean test(KubeRecipe r) {
			return r != null && !r.removed && filter.test(cx, r);
		}
	}

	public Stream<KubeRecipe> recipeStream(Context cx, RecipeFilter filter) {
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

		return originalRecipes.values().stream().filter(new RecipeStreamFilter(cx, filter));
	}

	private <T> T reduceRecipesAsync(Context cx, RecipeFilter filter, Function<Stream<KubeRecipe>, T> function) {
		return function.apply(recipeStream(cx, filter));
	}

	public void forEachRecipe(Context cx, RecipeFilter filter, Consumer<KubeRecipe> consumer) {
		if (filter instanceof IDFilter id) {
			var r = originalRecipes.get(id.id);

			if (r != null && !r.removed) {
				consumer.accept(r);
			}
		} else {
			recipeStream(cx, filter).forEach(consumer);
		}
	}

	public int countRecipes(Context cx, RecipeFilter filter) {
		return reduceRecipesAsync(cx, filter, s -> (int) s.count());
	}

	public boolean containsRecipe(Context cx, RecipeFilter filter) {
		return reduceRecipesAsync(cx, filter, s -> s.findAny().isPresent());
	}

	public Collection<KubeRecipe> findRecipes(Context cx, RecipeFilter filter) {
		return reduceRecipesAsync(cx, filter, Stream::toList);
	}

	public Collection<ResourceLocation> findRecipeIds(Context cx, RecipeFilter filter) {
		return reduceRecipesAsync(cx, filter, s -> s.map(KubeRecipe::getOrCreateId).toList());
	}

	public void remove(Context cx, RecipeFilter filter) {
		forEachRecipe(cx, filter, KubeRecipe::remove);
	}

	public void replaceInput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug()) ? (": IN " + match + " -> " + with) : "";

		forEachRecipe(cx, filter, r -> {
			if (r.replaceInput(cx, match, with)) {
				if (DevProperties.get().logModifiedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + dstring);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + dstring);
				}
			}
		});
	}

	public void replaceOutput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ConsoleJS.SERVER.shouldPrintDebug()) ? (": OUT " + match + " -> " + with) : "";

		forEachRecipe(cx, filter, r -> {
			if (r.replaceOutput(cx, match, with)) {
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

	public KubeRecipe custom(Context cx, JsonObject json) {
		if (json == null || !json.has("type")) {
			throw new KubeRuntimeException("JSON must contain 'type'!");
		}

		var type = getRecipeFunction(json.get("type").getAsString());

		if (type == null) {
			throw new UnknownRecipeTypeException(json.get("type").getAsString());
		}

		var sourceLine = SourceLine.of(cx);

		try {
			var r = type.schemaType.schema.deserialize(sourceLine, type, null, json);
			r.afterLoaded();
			return addRecipe(r, true);
		} catch (Throwable cause) {
			var r = type.schemaType.schema.recipeFactory.create(type, sourceLine, true);
			r.creationError = true;
			ConsoleJS.SERVER.error("Failed to create custom recipe from json " + JsonUtils.toString(json), sourceLine, cause, POST_SKIP_ERROR);
			r.json = json;
			r.newRecipe = true;
			return r;
		}
	}

	private void printTypes(Predicate<RecipeSchemaType> predicate) {
		int t = 0;
		var map = new Reference2ObjectOpenHashMap<RecipeSchema, Set<ResourceLocation>>();

		for (var ns : recipeSchemaStorage.namespaces.values()) {
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

	public void printTypes(Context cx) {
		ConsoleJS.SERVER.info("== All recipe types [used] ==");
		var set = reduceRecipesAsync(cx, ConstantFilter.TRUE, s -> s.map(r -> r.type.id).collect(Collectors.toSet()));
		printTypes(t -> set.contains(t.id));
	}

	public void printAllTypes() {
		ConsoleJS.SERVER.info("== All recipe types [available] ==");
		printTypes(t -> BuiltInRegistries.RECIPE_SERIALIZER.get(t.id) != null);
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
		var id = ResourceLocation.parse(prefix + ids);

		while (takenIds.containsKey(id)) {
			id = ResourceLocation.parse(prefix + ids + '_' + i);
			i++;
		}

		takenIds.put(id, recipe);
		return id;
	}

	public void stage(Context cx, RecipeFilter filter, String stage) {
		forEachRecipe(cx, filter, r -> r.stage(stage));
	}
}