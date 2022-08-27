package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.filter.FilteredRecipe;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AfterRecipesLoadedEventJS extends EventJS {
	public static final class Container implements FilteredRecipe {
		public final ResourceLocation id;
		public final RecipeType<?> recipeType;
		public final Recipe<?> recipe;
		private final Map<ResourceLocation, Recipe<?>> mapRef;
		private JsonObject json;
		private String typeString;

		private Container(ResourceLocation id, RecipeType<?> recipeType, Recipe<?> recipe, Map<ResourceLocation, Recipe<?>> mapRef) {
			this.id = id;
			this.recipeType = recipeType;
			this.recipe = recipe;
			this.mapRef = mapRef;
		}

		public JsonObject getJson() {
			if (json == null) {
				json = new JsonObject();
			}

			return json;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || obj instanceof Container c && id.equals(c.id);
		}

		@Override
		public String getGroup() {
			return recipe.getGroup();
		}

		@Override
		public ResourceLocation getOrCreateId() {
			return id;
		}

		@Override
		public String getMod() {
			return id.getNamespace();
		}

		@Override
		public String getType() {
			if (typeString == null) {
				typeString = String.valueOf(KubeJSRegistries.recipeSerializers().getId(recipe.getSerializer()));
			}

			return typeString;
		}

		@Override
		public boolean hasInput(Ingredient in, boolean exact) {
			for (Ingredient ingredient : recipe.getIngredients()) {
				for (ItemStack stack : ingredient.getItems()) {
					if (in.test(stack)) {
						return true;
					}
				}
			}

			return false;
		}

		@Override
		public boolean hasOutput(Ingredient out, boolean exact) {
			return out.test(recipe.getResultItem());
		}
	}

	private final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipeMap;
	private final Map<ResourceLocation, Recipe<?>> recipeIdMap;

	private List<Container> originalRecipes;

	public AfterRecipesLoadedEventJS(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> r, Map<ResourceLocation, Recipe<?>> n) {
		recipeMap = r;
		recipeIdMap = n;
	}

	private List<Container> getOriginalRecipes() {
		if (originalRecipes == null) {
			originalRecipes = new ArrayList<>();

			for (var map : recipeMap.values()) {
				for (var entry : map.entrySet()) {
					originalRecipes.add(new Container(entry.getKey(), entry.getValue().getType(), entry.getValue(), map));
				}
			}
		}

		return originalRecipes;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<Container> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			getOriginalRecipes().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			getOriginalRecipes().stream().filter(filter).forEach(consumer);
		}
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return getOriginalRecipes().size();
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return (int) getOriginalRecipes().stream().filter(filter).count();
		}

		return 0;
	}

	public int remove(RecipeFilter filter) {
		int count = 0;
		var itr = getOriginalRecipes().iterator();

		while (itr.hasNext()) {
			Container r = itr.next();

			if (filter.test(r)) {
				recipeIdMap.remove(r.id);
				r.mapRef.remove(r.id);
				itr.remove();
				count++;

				if (ServerSettings.instance.logRemovedRecipes) {
					ConsoleJS.SERVER.info("- " + r);
				} else if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("- " + r);
				}
			}
		}

		return count;
	}

	@Override
	protected void afterPosted(boolean isCanceled) {
		recipeMap.values().removeIf(Map::isEmpty);
	}
}