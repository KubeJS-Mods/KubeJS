package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
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
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipesKubeEvent implements KubeEvent {
	public static final ScopedValue<RecipesKubeEvent> INSTANCE = ScopedValue.newInstance();

	public static final Pattern POST_SKIP_ERROR = ConsoleJS.methodPattern(RecipesKubeEvent.class, "post");
	public static final Pattern CREATE_RECIPE_SKIP_ERROR = ConsoleJS.methodPattern(RecipesKubeEvent.class, "createRecipe");
	private static final Predicate<KubeRecipe> RECIPE_NOT_REMOVED = r -> r != null && !r.removed;
	private static final Predicate<KubeRecipe> RECIPE_IS_SYNTHETIC = r -> !r.newRecipe;

	private final Stopwatch overallTimer;

	public final RecipeSchemaStorage recipeSchemaStorage;
	public final RegistryAccessContainer registries;
	public final RegistryOpsContainer ops;
	public final Map<Identifier, KubeRecipe> originalRecipes;
	public final Collection<KubeRecipe> addedRecipes;
	public final Collection<KubeRecipe> removedRecipes;

	int modifiedCount, failedCount;

	private final Map<Identifier, KubeRecipe> takenIds;

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

	public RecipesKubeEvent(ServerScriptManager manager) {
		ScriptType.SERVER.console.info("Initializing recipe event...");
		this.overallTimer = Stopwatch.createStarted();

		this.recipeSchemaStorage = manager.recipeSchemaStorage;
		this.registries = manager.getRegistries();
		this.ops = new RegistryOpsContainer(
			new KubeRecipeEventOps<>(this, registries.nbt()),
			new KubeRecipeEventOps<>(this, registries.json()),
			new KubeRecipeEventOps<>(this, registries.java())
		);
		this.originalRecipes = new HashMap<>();
		this.addedRecipes = new ConcurrentLinkedQueue<>();
		this.removedRecipes = new ConcurrentLinkedQueue<>();
		this.recipeFunctions = new HashMap<>();
		this.takenIds = new ConcurrentHashMap<>();

		// var itemTags = manager.getLoadedTags(Registries.ITEM);
		// System.out.println(itemTags);

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
				var s = StringUtilsWrapper.snakeCaseToCamelCase(entry.getKey());

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
	}

	@HideFromJS
	@ApiStatus.Internal
	public void post(Map<Identifier, JsonElement> datapackRecipeMap) {
		discoverRecipes(datapackRecipeMap);
		postEvent();
		applyChanges(datapackRecipeMap);
		finishEvent();
	}

	@HideFromJS
	public void discoverRecipes(Map<Identifier, JsonElement> recipeJsons) {
		var timer = Stopwatch.createStarted();

		KubeJSPlugins.forEachPlugin(p -> p.beforeRecipeLoading(this, recipeJsons));
		int skippedRecipes = 0;

		for (var entry : recipeJsons.entrySet()) {
			Identifier recipeId = entry.getKey();

			//Forge: filter anything beginning with "_" as it's used for metadata.
			//noinspection ConstantValue
			if (recipeId == null || recipeId.getPath().startsWith("_")) {
				infoSkip("Skipping recipe %s, filename starts with _".formatted(recipeId));
				skippedRecipes++;
				continue;
			}

			var originalJsonElement = entry.getValue();

			if (!(originalJsonElement instanceof JsonObject originalJson)) {
				warnSkip("Skipping recipe %s, not a json object".formatted(recipeId));
				continue;
			}

			if (!originalJson.has("type")) {
				warnSkip("Skipping recipe %s, missing 'type' field".formatted(recipeId));
				continue;
			}

			var codec = MapCodec.unit(originalJson).codec();

			switch (codec.parse(ops.json(), originalJson)) {
				case DataResult.Success(var jsonResult, var lifecycle) -> {
					if (jsonResult.isEmpty()) {
						infoSkip("Skipping recipe %s, conditions not met".formatted(recipeId));
						skippedRecipes++;
					} else {
						parseOriginalRecipe(jsonResult, recipeId);
					}
				}
				case DataResult.Error<?> error -> errorSkip("Skipping recipe %s, error parsing conditions: %s".formatted(recipeId, error.message()));
			}
		}

		takenIds.putAll(originalRecipes);
		ScriptType.SERVER.console.info("Found %,d recipes (skipped %,d) in %s".formatted(originalRecipes.size(), skippedRecipes, timer.stop()));
	}

	private void parseOriginalRecipe(JsonObject json, Identifier recipeId) {
		var typeStr = GsonHelper.getAsString(json, "type");
		var recipeIdAndType = recipeId + "[" + typeStr + "]";
		var type = getRecipeFunction(typeStr);

		if (type == null) {
			warnSkip("Skipping recipe %s, unknown type: %s".formatted(recipeId, typeStr));
			return;
		}

		var stack = new ErrorStack();

		try {
			var recipe = type.schemaType.schema.deserialize(SourceLine.UNKNOWN, type, recipeId, json);
			recipe.afterLoaded(stack);
			originalRecipes.put(recipeId, recipe);

			if (ScriptType.SERVER.console.shouldPrintDebug()) {
				var original = recipe.getOriginalRecipe();

				if (original == null || SpecialRecipeSerializerManager.INSTANCE.isSpecial(original)) {
					ScriptType.SERVER.console.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
				} else {
					ScriptType.SERVER.console.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
				}
			}
		} catch (Throwable ex) {
			var recipeStr = "'%s'%s".formatted(recipeIdAndType, stack.atString());

			if (ex instanceof RecipeComponentException || DevProperties.get().logErroringParsedRecipes) {
				ScriptType.SERVER.console.warn("Failed to parse recipe %s! Falling back to vanilla".formatted(recipeStr), ex, POST_SKIP_ERROR);
			}

			try {
				originalRecipes.put(recipeId, UnknownRecipeSchema.SCHEMA.deserialize(SourceLine.UNKNOWN, type, recipeId, json));
			} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
				if (DevProperties.get().logErroringParsedRecipes) {
					ScriptType.SERVER.console.error("Failed to parse recipe %s".formatted(recipeStr), ex2, POST_SKIP_ERROR);
				}
			} catch (Exception ex3) {
				ScriptType.SERVER.console.error("Failed to parse recipe %s".formatted(recipeStr), ex3, POST_SKIP_ERROR);
			}
		}
	}

	private void infoSkip(String s) {
		if (DevProperties.get().logSkippedRecipes) {
			ScriptType.SERVER.console.info(s);
		} else {
			RecipeManager.LOGGER.debug(s);
		}
	}

	private void warnSkip(String s) {
		if (DevProperties.get().logSkippedRecipes) {
			ScriptType.SERVER.console.warn(s);
		} else {
			RecipeManager.LOGGER.warn(s);
		}
	}

	private void errorSkip(String s) {
		if (DevProperties.get().logSkippedRecipes) {
			ScriptType.SERVER.console.error(s);
		} else {
			RecipeManager.LOGGER.error(s);
		}
	}

	@HideFromJS
	public void postEvent() {
		var timer = Stopwatch.createStarted();

		ServerEvents.RECIPES.post(ScriptType.SERVER, this);

		for (var r : originalRecipes.values()) {
			if (r.removed) {
				removedRecipes.add(r);
			} else if (r.hasChanged()) {
				modifiedCount++;
			}
		}

		ScriptType.SERVER.console.info("Posted recipe events in " + TimeJS.msToString(timer.stop().elapsed(TimeUnit.MILLISECONDS)));
	}

	@HideFromJS
	public void applyChanges(Map<Identifier, JsonElement> map) {
		var timer = Stopwatch.createStarted();
		addedRecipes.removeIf(RECIPE_IS_SYNTHETIC);

		map.clear();
		map.putAll(originalRecipes.values().parallelStream()
			.filter(RECIPE_NOT_REMOVED)
			.map(KubeRecipe::serializeChanges)
			.filter(RECIPE_NOT_REMOVED)
			.peek(this::addToExport)
			.collect(Collectors.toConcurrentMap(KubeRecipe::getOrCreateId, recipe -> recipe.json, (a, b) -> b)));

		map.putAll(addedRecipes.parallelStream()
			.filter(RECIPE_NOT_REMOVED)
			.map(KubeRecipe::serializeChanges)
			.filter(RECIPE_NOT_REMOVED)
			.peek(this::addToExport)
			.collect(Collectors.toConcurrentMap(KubeRecipe::getOrCreateId, recipe -> recipe.json, (a, b) -> {
				ScriptType.SERVER.console.warn("KubeJS has found two recipes with the same ID in your custom recipes! Picking the last one encountered!");
				ScriptType.SERVER.console.warn("Recipe A JSON: " + a);
				ScriptType.SERVER.console.warn("Recipe B JSON: " + b);
				return b;
			})));

		ScriptType.SERVER.console.info("KubeJS modifications to recipe manager finished in %s".formatted(timer.stop()));
	}

	@HideFromJS
	public void finishEvent() {
		var successfulAddedRecipes = addedRecipes.stream().filter(RECIPE_NOT_REMOVED).toList();
		ChangesForChat.recipesAdded = successfulAddedRecipes.size();
		ChangesForChat.recipesModified = modifiedCount;
		ChangesForChat.recipesRemoved = removedRecipes.size();
		ChangesForChat.recipesMs = overallTimer.stop().elapsed(TimeUnit.MILLISECONDS);

		ScriptType.SERVER.console.info("Added %d recipes, removed %d recipes, modified %d recipes, with %d failed recipes taking %s in total".formatted(successfulAddedRecipes.size(), removedRecipes.size(), modifiedCount, failedCount, TimeJS.msToString(ChangesForChat.recipesMs)));

		if (DataExport.export != null) {
			for (var r : removedRecipes) {
				DataExport.export.addJson("removed_recipes/" + r.getId() + ".json", r.json);
			}
		}

		if (DevProperties.get().logRecipeDebug) {
			ScriptType.SERVER.console.info("======== Debug output of all added recipes ========");

			for (var r : successfulAddedRecipes) {
				ScriptType.SERVER.console.info(r.getOrCreateId() + ": " + r.json);
			}

			ScriptType.SERVER.console.info("======== Debug output of all modified recipes ========");

			for (var r : originalRecipes.values()) {
				if (!r.removed && r.hasChanged()) {
					ScriptType.SERVER.console.info(r.getOrCreateId() + ": " + r.json + " FROM " + r.originalJson);
				}
			}

			ScriptType.SERVER.console.info("======== Debug output of all removed recipes ========");

			for (var r : removedRecipes) {
				ScriptType.SERVER.console.info(r.getOrCreateId() + ": " + r.json);
			}
		}

		RegexIDFilter.clearInternCache();
	}

	private void addToExport(KubeRecipe r) {
		var path = r.kjs$getMod() + "/" + r.getPath();
		if (DataExport.export != null) {
			DataExport.export.addJson("recipes/%s.json".formatted(path), r.json);

			if (r.newRecipe) {
				DataExport.export.addJson("added_recipes/%s.json".formatted(path), r.json);
			}
		}
	}

	@HideFromJS
	public void handleFailedRecipe(Identifier id, JsonElement json, Throwable ex) {
		// only handle recipes that failed because of kubejs interfering
		if (json instanceof JsonObject obj && obj.has(KubeRecipe.CHANGED_MARKER)) {
			var sourceLine = SourceLine.fromJson(obj.remove(KubeRecipe.CHANGED_MARKER).getAsJsonObject());
			if (DevProperties.get().logErroringRecipes) {
				ScriptType.SERVER.console.error("Error parsing recipe %s (details below this line)".formatted(id), sourceLine, ex, null);
				ScriptType.SERVER.console.stopCapturingErrors();
				ScriptType.SERVER.console.error("Recipe JSON for %s: %s".formatted(id, json), sourceLine, null, null);
				ScriptType.SERVER.console.startCapturingErrors();
			}
			failedCount++;
		}
	}

	public Map<String, Object> getRecipes() {
		return recipeFunctions;
	}

	public KubeRecipe addRecipe(KubeRecipe r, boolean json) {
		addedRecipes.add(r);

		if (DevProperties.get().logAddedRecipes) {
			ScriptType.SERVER.console.info("+ " + r.kjs$getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
			ScriptType.SERVER.console.debug("+ " + r.kjs$getType() + ": " + r.getFromToString() + (json ? " [json]" : ""));
		}

		return r;
	}

	private record RecipeStreamFilter(Context cx, RecipeFilter filter) implements Predicate<KubeRecipe> {
		@Override
		public boolean test(KubeRecipe r) {
			return r != null && !r.removed && filter.test(new RecipeMatchContext.Impl(cx, r));
		}
	}

	public Stream<KubeRecipe> recipeStream(Context cx, RecipeFilter filter) {
		if (filter == ConstantFilter.FALSE) {
			return Stream.empty();
		} else if (filter instanceof IDFilter(Identifier id1)) {
			var r = originalRecipes.get(id1);
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

			return or.list.stream().map(idf -> originalRecipes.get(((IDFilter) idf).id())).filter(RECIPE_NOT_REMOVED);
		}

		return originalRecipes.values().stream().filter(new RecipeStreamFilter(cx, filter));
	}

	private <T> T reduceRecipesAsync(Context cx, RecipeFilter filter, Function<Stream<KubeRecipe>, T> function) {
		return function.apply(recipeStream(cx, filter));
	}

	public void forEachRecipe(Context cx, RecipeFilter filter, Consumer<KubeRecipe> consumer) {
		if (filter instanceof IDFilter(Identifier id1)) {
			var r = originalRecipes.get(id1);

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

	public Collection<Identifier> findRecipeIds(Context cx, RecipeFilter filter) {
		return reduceRecipesAsync(cx, filter, s -> s.map(KubeRecipe::getOrCreateId).toList());
	}

	public void remove(Context cx, RecipeFilter filter) {
		forEachRecipe(cx, filter, KubeRecipe::remove);
	}

	public void replaceInput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ScriptType.SERVER.console.shouldPrintDebug()) ? (": IN " + match + " -> " + with) : "";

		forEachRecipe(cx, filter, r -> {
			if (r.replaceInput(new RecipeScriptContext.Impl(cx, r), match, with)) {
				if (DevProperties.get().logModifiedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + dstring);
				} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("~ " + r + dstring);
				}
			}
		});
	}

	public void replaceOutput(Context cx, RecipeFilter filter, ReplacementMatchInfo match, Object with) {
		var dstring = (DevProperties.get().logModifiedRecipes || ScriptType.SERVER.console.shouldPrintDebug()) ? (": OUT " + match + " -> " + with) : "";

		forEachRecipe(cx, filter, r -> {
			if (r.replaceOutput(new RecipeScriptContext.Impl(cx, r), match, with)) {
				if (DevProperties.get().logModifiedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + dstring);
				} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("~ " + r + dstring);
				}
			}
		});
	}

	@Nullable
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
		return parseJson(json, SourceLine.of(cx)).getPartialOrThrow(KubeRuntimeException::new);
	}

	@HideFromJS
	public DataResult<KubeRecipe> parseJson(JsonObject json, SourceLine sourceLine) {
		if (json == null || !json.has("type")) {
			return DataResult.error(() -> "JSON must contain 'type'!");
		}

		var type = getRecipeFunction(json.get("type").getAsString());

		if (type == null) {
			return DataResult.error(() -> "Unknown recipe type: " + json.get("type").getAsString());
		}

		var stack = new ErrorStack();

		try {
			var recipe = type.schemaType.schema.deserialize(sourceLine, type, null, json);
			recipe.afterLoaded(stack);
			return DataResult.success(addRecipe(recipe, true));
		} catch (Throwable cause) {
			var recipe = type.schemaType.schema.recipeFactory.create(type, sourceLine, true);
			recipe.creationError = true;
			var errorString = "Failed to create custom recipe" + stack.atString() + " from json " + JsonUtils.toString(json);
			ScriptType.SERVER.console.error(errorString, sourceLine, cause, POST_SKIP_ERROR);
			recipe.json = json;
			recipe.newRecipe = true;

			// importantly, we return a partial result here!
			return DataResult.error(() -> errorString, recipe);
		}
	}

	private void printTypes(Predicate<RecipeSchemaType> predicate, boolean all) {
		int t = 0;
		var map = new Reference2ObjectLinkedOpenHashMap<RecipeSchema, Set<Identifier>>();

		for (var ns : recipeSchemaStorage.namespaces.values()) {
			for (var type : ns.values()) {
				if (predicate.test(type)) {
					t++;
					map.computeIfAbsent(type.schema, s -> new LinkedHashSet<>()).add(type.id);
				}
			}
		}

		if (all) {
			ScriptType.SERVER.console.info("- All recipe types");
			ScriptType.SERVER.console.info("  - .id(id)");
			ScriptType.SERVER.console.info("  - .group(string)");
			ScriptType.SERVER.console.info("  - .set(key, value)");
			ScriptType.SERVER.console.info("  - .merge(json)");
			ScriptType.SERVER.console.info("- All crafting table recipe types");
			ScriptType.SERVER.console.info("  - .stage(string)");
			ScriptType.SERVER.console.info("  - .damageIngredient(filter, int?)");
			ScriptType.SERVER.console.info("  - .replaceIngredient(filter, item_stack)");
			ScriptType.SERVER.console.info("  - .customIngredientAction(filter, string)");
			ScriptType.SERVER.console.info("  - .keepIngredient(filter)");
			ScriptType.SERVER.console.info("  - .consumeIngredient(filter)");
			ScriptType.SERVER.console.info("  - .modifyResult(string)");
		}

		for (var entry : map.entrySet()) {
			ScriptType.SERVER.console.info("- " + entry.getValue().stream().map(Identifier::toString).collect(Collectors.joining(", ")));

			for (var c : entry.getKey().constructors().values()) {
				ScriptType.SERVER.console.info("  - " + c);
			}

			for (var key : entry.getKey().keys) {
				var name = key.getPrimaryFunctionName();

				if (RecipeFunction.isValidIdentifier(name.toCharArray())) {
					ScriptType.SERVER.console.info("  - ." + name + "(" + key.component + ")");
				}
			}

			for (var f : entry.getKey().functions.values()) {
				if (RecipeFunction.isValidIdentifier(f.name().toCharArray())) {
					ScriptType.SERVER.console.info("  - ." + f);
				}
			}
		}

		ScriptType.SERVER.console.info(t + " types");
	}

	public void printTypes(Context cx) {
		ScriptType.SERVER.console.info("== All recipe types [used] ==");
		var set = reduceRecipesAsync(cx, ConstantFilter.TRUE, s -> s.map(r -> r.type.id).collect(Collectors.toSet()));
		printTypes(t -> set.contains(t.id), false);
	}

	public void printAllTypes() {
		ScriptType.SERVER.console.info("== All recipe types [available] ==");
		printTypes(t -> BuiltInRegistries.RECIPE_SERIALIZER.containsKey(t.id), true);
	}

	public void printExamples(String type) {
		var list = originalRecipes.values().stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ScriptType.SERVER.console.info("== Random examples of '" + type + "' ==");

		for (var i = 0; i < Math.min(list.size(), 5); i++) {
			var r = list.get(i);
			ScriptType.SERVER.console.info("- " + r.getOrCreateId() + ":\n" + JsonIO.toPrettyString(r.json));
		}
	}

	public synchronized Identifier takeId(KubeRecipe recipe, String prefix, String ids) {
		int i = 2;
		var id = Identifier.parse(prefix + ids);

		while (takenIds.containsKey(id)) {
			id = Identifier.parse(prefix + ids + '_' + i);
			i++;
		}

		takenIds.put(id, recipe);
		return id;
	}

	public void stage(Context cx, RecipeFilter filter, String stage) {
		forEachRecipe(cx, filter, r -> r.stage(stage));
	}
}
