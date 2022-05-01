package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicateJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends EventJS {
	public static final String FORGE_CONDITIONAL = "forge:conditional";
	private static final Pattern SKIP_ERROR = Pattern.compile("at dev.latvian.mods.kubejs.recipe.RecipeEventJS.post");
	public static Map<UUID, IngredientWithCustomPredicateJS> customIngredientMap = null;

	public static RecipeEventJS instance;

	private final List<Recipe<?>> fallbackedRecipes = new ArrayList<>();
	private final List<RecipeJS> originalRecipes;
	final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	private final Set<RecipeJS> modifiedRecipes;
	private final Map<String, Object> recipeFunctions;
	private AtomicInteger modifiedRecipesCount;

	public final RecipeFunction shaped;
	public final RecipeFunction shapeless;
	public final RecipeFunction smelting;
	public final RecipeFunction blasting;
	public final RecipeFunction smoking;
	public final RecipeFunction campfireCooking;
	public final RecipeFunction stonecutting;
	public final RecipeFunction smithing;

	public RecipeEventJS(Map<ResourceLocation, RecipeTypeJS> t) {
		originalRecipes = new ArrayList<>();

		ConsoleJS.SERVER.info("Scanning recipes...");

		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		modifiedRecipes = new HashSet<>();

		recipeFunctions = new HashMap<>();

		Map<String, Map<String, RecipeSerializer<?>>> serializers = new HashMap<>();

		for (var entry : KubeJSRegistries.recipeSerializers().entrySet()) {
			serializers.computeIfAbsent(entry.getKey().location().getNamespace(), n -> new HashMap<>()).put(entry.getKey().location().getPath(), entry.getValue());
		}

		for (var entry : serializers.entrySet()) {
			Map<String, RecipeFunction> funcs = new HashMap<>();

			for (var entry1 : entry.getValue().entrySet()) {
				var location = new ResourceLocation(entry.getKey(), entry1.getKey());
				var typeJS = t.get(location);
				var func = new RecipeFunction(this, location, typeJS != null ? typeJS : new CustomRecipeTypeJS(entry1.getValue()));
				funcs.put(UtilsJS.convertSnakeCaseToCamelCase(entry1.getKey()), func);
				funcs.put(entry1.getKey(), func);

				recipeFunctions.put(UtilsJS.convertSnakeCaseToCamelCase(entry.getKey() + "_" + entry1.getKey()), func);
				recipeFunctions.put(entry.getKey() + "_" + entry1.getKey(), func);
			}

			recipeFunctions.put(UtilsJS.convertSnakeCaseToCamelCase(entry.getKey()), funcs);
			recipeFunctions.put(entry.getKey(), funcs);
		}

		SpecialRecipeSerializerManager.INSTANCE.reset();
		SpecialRecipeSerializerManager.INSTANCE.post(ScriptType.SERVER, KubeJSEvents.RECIPES_SERIALIZER_SPECIAL_FLAG);

		shaped = getRecipeFunction(CommonProperties.get().serverOnly ? "minecraft:crafting_shaped" : "kubejs:shaped");
		shapeless = getRecipeFunction(CommonProperties.get().serverOnly ? "minecraft:crafting_shapeless" : "kubejs:shapeless");
		smelting = getRecipeFunction("minecraft:smelting");
		blasting = getRecipeFunction("minecraft:blasting");
		smoking = getRecipeFunction("minecraft:smoking");
		campfireCooking = getRecipeFunction("minecraft:campfire_cooking");
		stonecutting = getRecipeFunction("minecraft:stonecutting");
		smithing = getRecipeFunction("minecraft:smithing");
	}

	@HideFromJS
	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap) {
		RecipeJS.itemErrors = false;
		ConsoleJS.SERVER.setLineNumber(true);
		var timer = Stopwatch.createStarted();

		var allRecipeMap = new JsonObject();

		for (var entry : jsonMap.entrySet()) {
			var recipeId = entry.getKey();

			if (Platform.isForge() && recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			var recipeIdAndType = recipeId + "[unknown:type]";

			try {
				var json = entry.getValue();

				var type = GsonHelper.getAsString(json, "type");

				recipeIdAndType = recipeId + "[" + type + "]";

				if (!RecipePlatformHelper.processConditions(recipeManager, json, "conditions")) {
					if (ServerSettings.instance.logSkippedRecipes) {
						ConsoleJS.SERVER.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
					}

					continue;
				}

				if (type.equals(FORGE_CONDITIONAL)) {
					var items = GsonHelper.getAsJsonArray(json, "recipes");
					var skip = true;

					for (var idx = 0; idx < items.size(); idx++) {
						var e = items.get(idx);

						if (!e.isJsonObject()) {
							throw new RecipeExceptionJS("Invalid recipes entry at index " + idx + " Must be JsonObject");
						}

						var o = e.getAsJsonObject();

						if (RecipePlatformHelper.processConditions(recipeManager, o, "conditions")) {
							json = o.get("recipe").getAsJsonObject();
							type = GsonHelper.getAsString(json, "type");
							recipeIdAndType = recipeId + "[" + type + "]";
							skip = false;
							break;
						}
					}

					if (skip) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ConsoleJS.SERVER.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
						}

						continue;
					}
				}

				var function = getRecipeFunction(type);

				if (function.type == null) {
					throw new MissingRecipeFunctionException("Unknown recipe type!").fallback();
				}

				var recipe = function.type.factory.get();
				recipe.id = recipeId;
				recipe.type = function.type;
				recipe.json = json;
				recipe.originalRecipe = RecipePlatformHelper.fromJson(recipe);

				if (recipe.originalRecipe == null) {
					if (ServerSettings.instance.logSkippedRecipes) {
						ConsoleJS.SERVER.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
					}

					continue;
				}

				recipe.deserializeJson();
				originalRecipes.add(recipe);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					if (SpecialRecipeSerializerManager.INSTANCE.isSpecial(recipe.originalRecipe)) {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
					} else {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
					}
				}

				if (ServerSettings.dataExport != null) {
					var exp = new JsonObject();
					exp.add("recipe", json);

					if (!recipe.inputItems.isEmpty()) {
						var array = new JsonArray();

						for (var in : recipe.inputItems) {
							array.add(in.toJson());
						}

						exp.add("inputs", array);
					}

					if (!recipe.outputItems.isEmpty()) {
						var array = new JsonArray();

						for (var out : recipe.outputItems) {
							array.add(out.toResultJson());
						}

						exp.add("outputs", array);
					}

					allRecipeMap.add(recipe.getId(), exp);
				}
			} catch (Throwable ex) {
				if (!(ex instanceof RecipeExceptionJS rex) || rex.fallback) {
					if (ServerSettings.instance.logErroringRecipes) {
						ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex, SKIP_ERROR);
					}

					try {
						fallbackedRecipes.add(Objects.requireNonNull(RecipeManager.fromJson(recipeId, entry.getValue())));
					} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
						if (ServerSettings.instance.logErroringRecipes) {
							ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2, SKIP_ERROR);
						}
					} catch (Exception ex3) {
						ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType + ":");
						ConsoleJS.SERVER.printStackTrace(ex3, SKIP_ERROR);
					}
				} else if (ServerSettings.instance.logErroringRecipes) {
					ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'", ex, SKIP_ERROR);
				}
			}
		}

		MutableInt removed = new MutableInt(0), added = new MutableInt(0), failed = new MutableInt(0), fallbacked = new MutableInt(0);
		modifiedRecipesCount = new AtomicInteger(0);

		ConsoleJS.SERVER.info("Found " + originalRecipes.size() + " recipes and " + fallbackedRecipes.size() + " failed recipes in " + timer.stop());

		timer.reset().start();

		TagIngredientJS.context = KubeJSReloadListener.resources.tagManager.getResult()
				.stream()
				.filter(result -> result.key().isFor(Registry.ITEM_REGISTRY))
				.findFirst()
				.map(result -> TagIngredientJS.Context.usingResult(UtilsJS.cast(result)))
				.orElseGet(() -> {
					ConsoleJS.SERVER.warn("Failed to load item tags during recipe event! Using replaceInput etc. will not work!");
					return TagIngredientJS.Context.EMPTY;
				});

		ConsoleJS.SERVER.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		ConsoleJS.SERVER.setLineNumber(false);

		ConsoleJS.SERVER.info("Posted recipe events in " + timer.stop());

		var recipesByName = new HashMap<ResourceLocation, Recipe<?>>();

		timer.reset().start();
		originalRecipes.stream()
				.filter(recipe -> {
					if (removedRecipes.contains(recipe)) {
						removed.increment();
						return false;
					}
					return true;
				})
				.map(recipe -> {
					try {
						recipe.originalRecipe = recipe.createRecipe();
					} catch (Throwable ex) {
						ConsoleJS.SERVER.warn("Error parsing recipe " + recipe + ": " + recipe.json, ex);
						failed.increment();
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.forEach(recipe -> {
					var id = recipe.getId();
					var ser = KubeJSRegistries.recipeSerializers().getId(recipe.getSerializer());
					var oldEntry = recipesByName.put(id, recipe);
					if (oldEntry != null) {
						var oldSer = KubeJSRegistries.recipeSerializers().getId(oldEntry.getSerializer());
						if (ServerSettings.instance.logOverrides) {
							ConsoleJS.SERVER.info("Overriding existing recipe with ID " + recipe.getId() + "[" + oldSer + " => " + ser + "] during phase PARSE!");
						}
					}
				});
		fallbackedRecipes.stream()
				.filter(Objects::nonNull)
				.forEach(recipe -> {
					var id = recipe.getId();
					var ser = KubeJSRegistries.recipeSerializers().getId(recipe.getSerializer());
					var oldEntry = recipesByName.put(id, recipe);
					if (oldEntry != null) {
						var oldSer = KubeJSRegistries.recipeSerializers().getId(oldEntry.getSerializer());
						if (ServerSettings.instance.logOverrides) {
							ConsoleJS.SERVER.info("Overriding existing recipe with ID " + recipe.getId() + "[" + oldSer + " => " + ser + "] during phase FALLBACK!");
						}
					}
				});
		ConsoleJS.SERVER.info("Modified & removed recipes in " + timer.stop());

		timer.reset().start();
		addedRecipes.stream()
				.map(recipe -> {
					try {
						recipe.originalRecipe = recipe.createRecipe();
					} catch (Throwable ex) {
						ConsoleJS.SERVER.warn("Error creating recipe " + recipe + ": " + recipe.json, ex, SKIP_ERROR);
						failed.increment();
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.forEach(recipe -> {
					added.increment();
					var id = recipe.getId();
					var ser = KubeJSRegistries.recipeSerializers().getId(recipe.getSerializer());
					var oldEntry = recipesByName.put(id, recipe);
					if (oldEntry != null) {
						var oldSer = KubeJSRegistries.recipeSerializers().getId(oldEntry.getSerializer());
						if (ServerSettings.instance.logOverrides) {
							ConsoleJS.SERVER.info("Overriding existing recipe with ID " + recipe.getId() + "[" + oldSer + " => " + ser + "] during phase ADD!");
						}
						removed.increment();
					}
				});

		if (ServerSettings.dataExport != null) {
			for (var r : removedRecipes) {
				if (allRecipeMap.get(r.getId()) instanceof JsonObject json) {
					json.addProperty("removed", true);
				}
			}

			ServerSettings.dataExport.add("recipes", allRecipeMap);
		}

		ConsoleJS.SERVER.info("Added recipes in " + timer.stop());

		HashMap<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipeMap = Util.make(new HashMap<>(), map -> {
			recipesByName.forEach((id, recipe) -> {
				var type = recipe.getType();
				var recipes = map.computeIfAbsent(type, t -> new HashMap<>());
				recipes.put(id, recipe);
			});
		});

		RecipePlatformHelper.pingNewRecipes(newRecipeMap);
		recipeManager.byName = recipesByName;
		recipeManager.recipes = newRecipeMap;
		ConsoleJS.SERVER.info("Added " + added.getValue() + " recipes, removed " + removed.getValue() + " recipes, modified " + modifiedRecipesCount.get() + " recipes, with " + failed.getValue() + " failed recipes and " + fallbacked.getValue() + " fall-backed recipes");
		RecipeJS.itemErrors = false;

		if (CommonProperties.get().debugInfo) {
			ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");

			for (var r : addedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json);
			}

			ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");

			for (var r : modifiedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json + " FROM " + r.originalJson);
			}

			ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");

			for (var r : removedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json);
			}
		}
	}

	public Map<String, Object> getRecipes() {
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1) {
		addedRecipes.add(r);

		if (ServerSettings.instance.logAddedRecipes) {
			ConsoleJS.SERVER.info("+ " + r.getType() + ": " + r.getFromToString());
		} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
			ConsoleJS.SERVER.debug("+ " + r.getType() + ": " + r.getFromToString());
		}

		return r;
	}

	public RecipeFilter customFilter(RecipeFilter filter) {
		return filter;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}

	public void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.parallelStream().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.parallelStream().filter(filter).forEach(consumer);
		}
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return originalRecipes.size();
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return (int) originalRecipes.stream().filter(filter).count();
		}

		return 0;
	}

	public int remove(RecipeFilter filter) {
		var count = new MutableInt();
		forEachRecipe(filter, r ->
		{
			if (removedRecipes.add(r)) {
				if (ServerSettings.instance.logRemovedRecipes) {
					ConsoleJS.SERVER.info("- " + r + ": " + r.getFromToString());
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("- " + r + ": " + r.getFromToString());
				}

				count.increment();
			}
		});
		return count.getValue();
	}

	public int replaceInput(RecipeFilter filter, IngredientJS ingredient, IngredientJS with, boolean exact) {
		var count = new AtomicInteger();
		var is = ingredient.toString();
		var ws = with.toString();

		forEachRecipeAsync(filter, r ->
		{
			if (r.replaceInput(ingredient, with, exact)) {
				count.incrementAndGet();
				modifiedRecipes.add(r);

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + ": IN " + is + " -> " + ws);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + ": IN " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipesCount.addAndGet(count.get());
		return count.get();
	}

	public int replaceInput(RecipeFilter filter, IngredientJS ingredient, IngredientJS with) {
		return replaceInput(filter, ingredient, with, false);
	}

	public int replaceInput(IngredientJS ingredient, IngredientJS with) {
		return replaceInput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public int replaceOutput(RecipeFilter filter, IngredientJS ingredient, ItemStackJS with, boolean exact) {
		var count = new AtomicInteger();
		var is = ingredient.toString();
		var ws = with.toString();

		forEachRecipeAsync(filter, r ->
		{
			if (r.replaceOutput(ingredient, with, exact)) {
				count.incrementAndGet();
				modifiedRecipes.add(r);

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + ": OUT " + is + " -> " + ws);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + ": OUT " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipesCount.addAndGet(count.get());
		return count.get();
	}

	public int replaceOutput(RecipeFilter filter, IngredientJS ingredient, ItemStackJS with) {
		return replaceOutput(filter, ingredient, with, false);
	}

	public int replaceOutput(IngredientJS ingredient, ItemStackJS with) {
		return replaceOutput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public RecipeFunction getRecipeFunction(@Nullable String id) {
		if (id == null || id.isEmpty()) {
			throw new NullPointerException("Recipe type is null!");
		}

		var namespace = UtilsJS.getNamespace(id);
		var path = UtilsJS.getPath(id);

		var func0 = recipeFunctions.get(namespace);

		if (func0 instanceof RecipeFunction fn) {
			return fn;
		} else if (!(func0 instanceof Map)) {
			throw new NullPointerException("Unknown recipe type: " + id);
		}

		var func = ((Map<String, RecipeFunction>) func0).get(path);

		if (func == null) {
			throw new NullPointerException("Unknown recipe type: " + id);
		}

		return func;
	}

	public RecipeJS custom(Object o) {
		var json = Objects.requireNonNull(MapJS.of(o));
		return getRecipeFunction(json.getOrDefault("type", "").toString()).createRecipe(new Object[]{json});
	}

	public void printTypes() {
		ConsoleJS.SERVER.info("== All recipe types [used] ==");
		var list = new HashSet<String>();
		originalRecipes.forEach(r -> list.add(r.type.toString()));
		list.stream().sorted().forEach(ConsoleJS.SERVER::info);
		ConsoleJS.SERVER.info(list.size() + " types");
	}

	public void printAllTypes() {
		ConsoleJS.SERVER.info("== All recipe types [available] ==");
		var list = KubeJSRegistries.recipeSerializers().entrySet().stream().map(e -> e.getKey().location().toString()).sorted().toList();
		list.forEach(ConsoleJS.SERVER::info);
		ConsoleJS.SERVER.info(list.size() + " types");
	}

	public void printExamples(String type) {
		var list = originalRecipes.stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ConsoleJS.SERVER.info("== Random examples of '" + type + "' ==");

		for (var i = 0; i < Math.min(list.size(), 5); i++) {
			var r = list.get(i);
			ConsoleJS.SERVER.info("- " + r.getOrCreateId() + ":\n" + JsonIO.toPrettyString(r.json));
		}
	}

	public void setItemErrors(boolean b) {
		RecipeJS.itemErrors = b;
	}

	public void stage(RecipeFilter filter, String stage) {
		forEachRecipe(filter, r -> r.stage(stage));
	}
}