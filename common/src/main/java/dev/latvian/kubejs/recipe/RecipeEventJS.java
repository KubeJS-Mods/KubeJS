package dev.latvian.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceKey;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends EventJS {
	public static final String FORGE_CONDITIONAL = "forge:conditional";

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

		for (Map.Entry<ResourceKey<RecipeSerializer<?>>, RecipeSerializer<?>> entry : KubeJSRegistries.recipeSerializers().entrySet()) {
			serializers.computeIfAbsent(entry.getKey().location().getNamespace(), n -> new HashMap<>()).put(entry.getKey().location().getPath(), entry.getValue());
		}

		for (Map.Entry<String, Map<String, RecipeSerializer<?>>> entry : serializers.entrySet()) {
			Map<String, RecipeFunction> funcs = new HashMap<>();

			for (Map.Entry<String, RecipeSerializer<?>> entry1 : entry.getValue().entrySet()) {
				ResourceLocation location = new ResourceLocation(entry.getKey(), entry1.getKey());
				RecipeTypeJS typeJS = t.get(location);
				RecipeFunction func = new RecipeFunction(this, location, typeJS != null ? typeJS : new CustomRecipeTypeJS(entry1.getValue()));
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

		shaped = getRecipeFunction("minecraft:crafting_shaped");
		shapeless = getRecipeFunction("minecraft:crafting_shapeless");
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
		Stopwatch timer = Stopwatch.createStarted();

		JsonObject allRecipeMap = new JsonObject();

		for (Map.Entry<ResourceLocation, JsonObject> entry : jsonMap.entrySet()) {
			ResourceLocation recipeId = entry.getKey();

			if (Platform.isForge() && recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			String recipeIdAndType = recipeId + "[unknown:type]";

			try {
				JsonObject json = entry.getValue();

				String type = GsonHelper.getAsString(json, "type");

				recipeIdAndType = recipeId + "[" + type + "]";

				if (!processConditions(json, "conditions")) {
					if (ServerSettings.instance.logSkippedRecipes) {
						ConsoleJS.SERVER.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
					}

					continue;
				}

				if (type.equals(FORGE_CONDITIONAL)) {
					JsonArray items = GsonHelper.getAsJsonArray(json, "recipes");
					boolean skip = true;

					for (int idx = 0; idx < items.size(); idx++) {
						JsonElement e = items.get(idx);

						if (!e.isJsonObject()) {
							throw new RecipeExceptionJS("Invalid recipes entry at index " + idx + " Must be JsonObject");
						}

						JsonObject o = e.getAsJsonObject();

						if (processConditions(o, "conditions")) {
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

				RecipeFunction function = getRecipeFunction(type);

				if (function.type == null) {
					throw new MissingRecipeFunctionException("Unknown recipe type!").fallback();
				}

				RecipeJS recipe = function.type.factory.get();
				recipe.id = recipeId;
				recipe.type = function.type;
				recipe.json = json;
				recipe.originalRecipe = function.type.serializer.fromJson(recipeId, json);

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
					JsonObject exp = new JsonObject();
					exp.add("recipe", json);

					if (!recipe.inputItems.isEmpty()) {
						JsonArray array = new JsonArray();

						for (IngredientJS in : recipe.inputItems) {
							array.add(in.toJson());
						}

						exp.add("inputs", array);
					}

					if (!recipe.outputItems.isEmpty()) {
						JsonArray array = new JsonArray();

						for (ItemStackJS out : recipe.outputItems) {
							array.add(out.toResultJson());
						}

						exp.add("outputs", array);
					}

					allRecipeMap.add(recipe.getId(), exp);
				}
			} catch (Throwable ex) {
				if (!(ex instanceof RecipeExceptionJS) || ((RecipeExceptionJS) ex).fallback) {
					if (ServerSettings.instance.logErroringRecipes) {
						ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex);
					}

					try {
						fallbackedRecipes.add(Objects.requireNonNull(RecipeManager.fromJson(recipeId, entry.getValue())));
					} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
						if (ServerSettings.instance.logErroringRecipes) {
							ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2);
						}
					} catch (Exception ex3) {
						ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType + ":");
						ex3.printStackTrace();
					}
				} else if (ServerSettings.instance.logErroringRecipes) {
					ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'", ex);
				}
			}
		}

		MutableInt removed = new MutableInt(0), added = new MutableInt(0), failed = new MutableInt(0), fallbacked = new MutableInt(0);
		modifiedRecipesCount = new AtomicInteger(0);

		ConsoleJS.SERVER.info("Found " + originalRecipes.size() + " recipes and " + fallbackedRecipes.size() + " failed recipes in " + timer.stop());
		timer.reset().start();
		ConsoleJS.SERVER.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		ConsoleJS.SERVER.setLineNumber(false);
		ConsoleJS.SERVER.info("Posted recipe events in " + timer.stop());

		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipeMap = new HashMap<>();
		Map<ResourceLocation, RecipeType<?>> existingRecipes = new HashMap<>();

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
					if (recipe.originalRecipe != null) {
						existingRecipes.put(recipe.id, recipe.originalRecipe.getType());
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					//modified.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		fallbackedRecipes.stream()
				.filter(Objects::nonNull)
				.peek(recipe -> existingRecipes.put(recipe.getId(), recipe.getType()))
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					fallbacked.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		ConsoleJS.SERVER.info("Modified & removed recipes in " + timer.stop());

		timer.reset().start();
		addedRecipes.stream()
				.map(recipe -> {
					try {
						recipe.originalRecipe = recipe.createRecipe();
					} catch (Throwable ex) {
						ConsoleJS.SERVER.warn("Error creating recipe " + recipe + ": " + recipe.json, ex);
						failed.increment();
					}
					if (recipe.originalRecipe != null) {
						ResourceLocation id = recipe.getOrCreateId();
						RecipeType<?> t = existingRecipes.remove(id);
						if (t != null) {
							newRecipeMap.get(t).remove(id);
							if (ServerSettings.instance.logOverrides) {
								ConsoleJS.SERVER.info("Overriding existing recipe with ID " + id + "(" + t + " => " + recipe.getType() + ")");
							}
						}
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					added.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});

		if (ServerSettings.dataExport != null) {
			for (RecipeJS r : removedRecipes) {
				JsonElement e = allRecipeMap.get(r.getId());

				if (e instanceof JsonObject) {
					((JsonObject) e).addProperty("removed", true);
				}
			}

			ServerSettings.dataExport.add("recipes", allRecipeMap);
		}

		ConsoleJS.SERVER.info("Added recipes in " + timer.stop());
		pingNewRecipes(newRecipeMap);
		((RecipeManagerKJS) recipeManager).setRecipesKJS(newRecipeMap);
		ConsoleJS.SERVER.info("Added " + added.getValue() + " recipes, removed " + removed.getValue() + " recipes, modified " + modifiedRecipesCount.get() + " recipes, with " + failed.getValue() + " failed recipes and " + fallbacked.getValue() + " fall-backed recipes");
		RecipeJS.itemErrors = false;

		if (CommonProperties.get().debugInfo) {
			ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");

			for (RecipeJS r : addedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json);
			}

			ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");

			for (RecipeJS r : modifiedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json);
			}

			ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");

			for (RecipeJS r : removedRecipes) {
				ConsoleJS.SERVER.info(r.id + ": " + r.json);
			}
		}
	}

	@ExpectPlatform
	private static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
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
		MutableInt count = new MutableInt();
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
		AtomicInteger count = new AtomicInteger();
		String is = ingredient.toString();
		String ws = with.toString();

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
		AtomicInteger count = new AtomicInteger();
		String is = ingredient.toString();
		String ws = with.toString();

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

		String namespace = UtilsJS.getNamespace(id);
		String path = UtilsJS.getPath(id);

		Object func0 = recipeFunctions.get(namespace);

		if (func0 instanceof RecipeFunction) {
			return (RecipeFunction) func0;
		} else if (!(func0 instanceof Map)) {
			throw new NullPointerException("Unknown recipe type: " + id);
		}

		RecipeFunction func = ((Map<String, RecipeFunction>) func0).get(path);

		if (func == null) {
			throw new NullPointerException("Unknown recipe type: " + id);
		}

		return func;
	}

	public RecipeJS custom(Object o) {
		MapJS json = Objects.requireNonNull(MapJS.of(o));
		return getRecipeFunction(json.getOrDefault("type", "").toString()).createRecipe(new Object[]{json});
	}

	public void printTypes() {
		ConsoleJS.SERVER.info("== All recipe types [used] ==");
		HashSet<String> list = new HashSet<>();
		originalRecipes.forEach(r -> list.add(r.type.toString()));
		list.stream().sorted().forEach(ConsoleJS.SERVER::info);
		ConsoleJS.SERVER.info(list.size() + " types");
	}

	public void printAllTypes() {
		ConsoleJS.SERVER.info("== All recipe types [available] ==");
		List<String> list = KubeJSRegistries.recipeSerializers().entrySet().stream().map(e -> e.getKey().location().toString()).sorted().collect(Collectors.toList());
		list.forEach(ConsoleJS.SERVER::info);
		ConsoleJS.SERVER.info(list.size() + " types");
	}

	public void printExamples(String type) {
		List<RecipeJS> list = originalRecipes.stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ConsoleJS.SERVER.info("== Random examples of '" + type + "' ==");

		for (int i = 0; i < Math.min(list.size(), 5); i++) {
			RecipeJS r = list.get(i);
			ConsoleJS.SERVER.info("- " + r.getOrCreateId() + ":\n" + JsonUtilsJS.toPrettyString(r.json));
		}
	}

	public void setItemErrors(boolean b) {
		RecipeJS.itemErrors = b;
	}

	public void stage(RecipeFilter filter, String stage) {
		forEachRecipe(filter, r -> r.stage(stage));
	}

	@ExpectPlatform
	private static boolean processConditions(JsonObject json, String key) {
		throw new AssertionError();
	}
}