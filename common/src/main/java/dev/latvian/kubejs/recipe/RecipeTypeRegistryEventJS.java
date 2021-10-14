package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeRegistryEventJS extends EventJS {
	private final Map<ResourceLocation, RecipeTypeJS> map;

	public RecipeTypeRegistryEventJS(Map<ResourceLocation, RecipeTypeJS> m) {
		map = m;
	}

	public void register(RecipeTypeJS type) {
		map.put(Registries.getId(type.serializer, Registry.RECIPE_SERIALIZER_REGISTRY), type);
		KubeJS.LOGGER.info("Registered custom recipe handler for type " + type);
	}

	public void register(ResourceLocation id, Supplier<RecipeJS> f) {
		register(new RecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id), "Cannot find recipe serializer: " + id), f));
	}

	public void ignore(ResourceLocation id) {
		register(new IgnoredRecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id), "Cannot find recipe serializer: " + id)));
	}

	public void registerShaped(ResourceLocation id) {
		register(id, ShapedRecipeJS::new);
	}

	public void registerShapeless(ResourceLocation id) {
		register(id, ShapelessRecipeJS::new);
	}
}