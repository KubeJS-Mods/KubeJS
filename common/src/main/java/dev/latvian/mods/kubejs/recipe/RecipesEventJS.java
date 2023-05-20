package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipesEventJS extends EventJS {
	private static final Pattern SKIP_ERROR = Pattern.compile("at dev.latvian.mods.kubejs.recipe.RecipeEventJS.post");
	public static Map<UUID, IngredientWithCustomPredicate> customIngredientMap = null;
	public static Map<UUID, ModifyRecipeResultCallback> modifyResultCallbackMap = null;

	public static RecipesEventJS instance;

	private final List<RecipeJS> originalRecipes;
	final List<RecipeJS> addedRecipes;
	private final Map<ResourceLocation, RecipeJS> removedRecipes;
	private final Map<ResourceLocation, RecipeJS> modifiedRecipes;
	final Map<String, Object> recipeFunctions;
	private AtomicInteger modifiedRecipesCount;

	public final RecipeFunction shaped;
	public final RecipeFunction shapeless;
	public final RecipeFunction smelting;
	public final RecipeFunction blasting;
	public final RecipeFunction smoking;
	public final RecipeFunction campfireCooking;
	public final RecipeFunction stonecutting;
	public final RecipeFunction smithing;

	RecipeSerializer<?> stageSerializer;

	public RecipesEventJS() {
		originalRecipes = new ArrayList<>();

		ConsoleJS.SERVER.info("Scanning recipes...");

		addedRecipes = new ArrayList<>();
		removedRecipes = new ConcurrentHashMap<>();
		modifiedRecipes = new ConcurrentHashMap<>();

		recipeFunctions = new HashMap<>();

		var allNamespaces = RecipeNamespace.getAll();

		for (var namespace : allNamespaces.values()) {
			var nsMap = new HashMap<String, RecipeFunction>();
			recipeFunctions.put(namespace.name, new NamespaceFunction(namespace, nsMap));

			for (var entry : namespace.entrySet()) {
				nsMap.put(entry.getValue().id.toString(), new RecipeFunction(this, entry.getValue()));
			}

			recipeFunctions.putAll(nsMap);
		}

		shaped = (RecipeFunction) recipeFunctions.get(CommonProperties.get().serverOnly ? "minecraft:crafting_shaped" : "kubejs:shaped");
		shapeless = (RecipeFunction) recipeFunctions.get(CommonProperties.get().serverOnly ? "minecraft:crafting_shapeless" : "kubejs:shapeless");
		smelting = (RecipeFunction) recipeFunctions.get("minecraft:smelting");
		blasting = (RecipeFunction) recipeFunctions.get("minecraft:blasting");
		smoking = (RecipeFunction) recipeFunctions.get("minecraft:smoking");
		campfireCooking = (RecipeFunction) recipeFunctions.get("minecraft:campfire_cooking");
		stonecutting = (RecipeFunction) recipeFunctions.get("minecraft:stonecutting");
		smithing = (RecipeFunction) recipeFunctions.get("minecraft:smithing");

		for (var entry : new ArrayList<>(recipeFunctions.entrySet())) {
			if (entry.getValue() instanceof RecipeFunction && entry.getKey().indexOf(':') != -1) {
				var s = UtilsJS.convertSnakeCaseToCamelCase(entry.getKey());

				if (!s.equals(entry.getKey())) {
					recipeFunctions.put(s, entry.getValue());
				}
			}
		}

		for (var entry : RecipeNamespace.getMappedRecipes().entrySet()) {
			var type = recipeFunctions.get(entry.getValue().toString());

			if (type instanceof RecipeFunction) {
				recipeFunctions.put(entry.getKey(), type);
			}
		}

		stageSerializer = KubeJSRegistries.recipeSerializers().get(new ResourceLocation("recipestages:stage"));
	}

	@HideFromJS
	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> datapackRecipeMap) {
		RecipeJS.itemErrors = false;

		TagContext.INSTANCE.setValue(KubeJSReloadListener.resources.tagManager.getResult()
				.stream()
				.filter(result -> result.key() == Registry.ITEM_REGISTRY)
				.findFirst()
				.map(result -> TagContext.usingResult(UtilsJS.cast(result)))
				.orElseGet(() -> {
					ConsoleJS.SERVER.warn("Failed to load item tags during recipe event! Using replaceInput etc. will not work!");
					return TagContext.EMPTY;
				}));

		var timer = Stopwatch.createStarted();

		var allRecipeMap = new JsonObject();

		for (var entry : datapackRecipeMap.entrySet()) {
			var recipeId = entry.getKey();

			if (recipeId == null || entry.getValue() == null || entry.getValue().size() == 0) {
				continue;
			}

			if (Platform.isForge() && recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			var recipeIdAndType = recipeId + "[unknown:type]";
			JsonObject json;

			try {
				json = RecipePlatformHelper.get().checkConditions(entry.getValue());
			} catch (Exception ex) {
				if (DevProperties.get().logSkippedRecipes) {
					ConsoleJS.SERVER.info("Skipping recipe " + recipeId + ", failed to check conditions: " + ex);
				}

				continue;
			}

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

			if (DataExport.dataExport != null) {
				allRecipeMap.add(recipeId.toString(), JsonUtils.copy(json));
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
				recipe.afterLoaded(false);
				originalRecipes.add(recipe);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					if (SpecialRecipeSerializerManager.INSTANCE.isSpecial(recipe.getOriginalRecipe())) {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
					} else {
						ConsoleJS.SERVER.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
					}
				}
			} catch (Throwable ex) {
				if (!(ex instanceof RecipeExceptionJS rex) || rex.fallback) {
					if (DevProperties.get().logErroringRecipes) {
						ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex, SKIP_ERROR);
					}

					try {
						originalRecipes.add(JsonRecipeSchema.SCHEMA.deserialize(type, recipeId, json));
					} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
						if (DevProperties.get().logErroringRecipes) {
							ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType, ex2, SKIP_ERROR);
						}
					} catch (Exception ex3) {
						ConsoleJS.SERVER.warn("Failed to parse recipe " + recipeIdAndType + ":");
						ConsoleJS.SERVER.printStackTrace(false, ex3, SKIP_ERROR);
					}
				} else if (DevProperties.get().logErroringRecipes) {
					ConsoleJS.SERVER.warn("Failed to parse recipe '" + recipeIdAndType + "'", ex, SKIP_ERROR);
				}
			}
		}

		var removed = new AtomicInteger(0);
		var added = new AtomicInteger(0);
		var failed = new AtomicInteger(0);

		modifiedRecipesCount = new AtomicInteger(0);

		ConsoleJS.SERVER.info("Found " + originalRecipes.size() + " recipes in " + timer.stop());

		timer.reset().start();
		ServerEvents.RECIPES.post(ScriptType.SERVER, this);
		ConsoleJS.SERVER.info("Posted recipe events in " + timer.stop());

		timer.reset().start();
		Map<ResourceLocation, Recipe<?>> recipesByName = Stream.concat(originalRecipes.stream(), addedRecipes.stream())
				.filter(recipe -> {
					if (!recipe.newRecipe && removedRecipes.containsKey(recipe.getOrCreateId())) {
						removed.incrementAndGet();
						return false;
					}

					return true;
				})
				.map(recipe -> {
					try {
						return recipe.createRecipe();
					} catch (Throwable ex) {
						ConsoleJS.SERVER.warn("Error parsing recipe " + recipe + ": " + recipe.json, ex, SKIP_ERROR);
						failed.incrementAndGet();
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Recipe::getId, Function.identity()));

		ConsoleJS.SERVER.info("Added, modified & removed recipes in " + timer.stop());

		if (DataExport.dataExport != null) {
			for (var r : removedRecipes.values()) {
				if (allRecipeMap.get(r.getId()) instanceof JsonObject json) {
					json.addProperty("removed", true);
				}
			}

			DataExport.dataExport.add("recipes", allRecipeMap);
		}

		var newRecipeMap = new HashMap<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>();

		for (var entry : recipesByName.entrySet()) {
			var type = entry.getValue().getType();
			var recipes = newRecipeMap.computeIfAbsent(type, k -> new HashMap<>());
			recipes.put(entry.getKey(), entry.getValue());
		}

		RecipePlatformHelper.get().pingNewRecipes(newRecipeMap);
		recipeManager.byName = recipesByName;
		recipeManager.recipes = newRecipeMap;
		ConsoleJS.SERVER.info("Added " + added.get() + " recipes, removed " + removed.get() + " recipes, modified " + modifiedRecipesCount.get() + " recipes, with " + failed.get() + " failed recipes");
		RecipeJS.itemErrors = false;

		if (CommonProperties.get().debugInfo) {
			ConsoleJS.SERVER.info("======== Debug output of all added recipes ========");

			for (var r : addedRecipes) {
				ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json);
			}

			ConsoleJS.SERVER.info("======== Debug output of all modified recipes ========");

			for (var r : modifiedRecipes.values()) {
				ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json + " FROM " + r.originalJson);
			}

			ConsoleJS.SERVER.info("======== Debug output of all removed recipes ========");

			for (var r : removedRecipes.values()) {
				ConsoleJS.SERVER.info(r.getOrCreateId() + ": " + r.json);
			}
		}
	}

	public Map<String, Object> getRecipes() {
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, boolean json) {
		if (r.shouldAdd()) {
			addedRecipes.add(r);
		}

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

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}

	public void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		forEachRecipe(filter, consumer);
		/* Async currently breaks iterating stacks for some reason. It's easier to disable it than to fix it

		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.parallelStream().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.parallelStream().filter(filter).forEach(consumer);
		}
		*/
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return originalRecipes.size();
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return (int) originalRecipes.stream().filter(filter).count();
		}

		return 0;
	}

	public boolean containsRecipe(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return true;
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return originalRecipes.stream().anyMatch(filter);
		}

		return false;
	}

	public int remove(RecipeFilter filter) {
		var count = new MutableInt();
		forEachRecipeAsync(filter, r ->
		{
			if (removedRecipes.put(r.getOrCreateId(), r) != r) {
				if (DevProperties.get().logRemovedRecipes) {
					ConsoleJS.SERVER.info("- " + r + ": " + r.getFromToString());
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("- " + r + ": " + r.getFromToString());
				}

				count.increment();
			}
		});
		return count.getValue();
	}

	public int replaceInput(RecipeFilter filter, IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		var count = new AtomicInteger();
		var is = match.ingredient.toString();
		var ws = with.toString();

		forEachRecipeAsync(filter, r -> {
			if (r.replaceInput(match, with, transformer)) {
				count.incrementAndGet();
				modifiedRecipes.put(r.getOrCreateId(), r);

				if (DevProperties.get().logModifiedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + ": IN " + is + " -> " + ws);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + ": IN " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipesCount.addAndGet(count.get());
		return count.get();
	}

	public int replaceInput(RecipeFilter filter, IngredientMatch match, InputItem with) {
		return replaceInput(filter, match, with, InputItemTransformer.DEFAULT);
	}

	public int replaceOutput(RecipeFilter filter, IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		var count = new AtomicInteger();
		var is = match.ingredient.toString();
		var ws = with.toString();

		forEachRecipeAsync(filter, r ->
		{
			if (r.replaceOutput(match, with, transformer)) {
				count.incrementAndGet();
				modifiedRecipes.put(r.getOrCreateId(), r);

				if (DevProperties.get().logModifiedRecipes) {
					ConsoleJS.SERVER.info("~ " + r + ": OUT " + is + " -> " + ws);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("~ " + r + ": OUT " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipesCount.addAndGet(count.get());
		return count.get();
	}

	public int replaceOutput(RecipeFilter filter, IngredientMatch match, OutputItem with) {
		return replaceOutput(filter, match, with, OutputItemTransformer.DEFAULT);
	}

	public RecipeFunction getRecipeFunction(@Nullable String id) {
		if (id == null || id.isEmpty()) {
			return null;
		} else if (recipeFunctions.get(UtilsJS.getID(id)) instanceof RecipeFunction fn) {
			return fn;
		} else {
			return null;
		}
	}

	public RecipeJS custom(JsonObject json) {
		if (json == null || !json.has("type")) {
			throw new RecipeExceptionJS("JSON must contain 'type'!");
		}

		var type = getRecipeFunction(json.get("type").getAsString());

		if (type == null) {
			throw new RecipeExceptionJS("Unknown recipe type: " + json.get("type").getAsString());
		}

		return type.createRecipe(new Object[]{json});
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