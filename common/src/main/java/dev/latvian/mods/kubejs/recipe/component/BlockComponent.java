package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public record BlockComponent(RecipeComponentType type) implements RecipeComponent<Block> {
	public static final RecipeComponent<Block> INPUT = new BlockComponent(RecipeComponentType.INPUT);
	public static final RecipeComponent<Block> OUTPUT = new BlockComponent(RecipeComponentType.OUTPUT);
	public static final RecipeComponent<Block> BLOCK = new BlockComponent(RecipeComponentType.OTHER);

	@Override
	public String componentType() {
		return "block";
	}

	@Override
	public RecipeComponentType getType() {
		return type;
	}

	@Override
	public JsonPrimitive write(Block value) {
		return new JsonPrimitive(String.valueOf(KubeJSRegistries.blocks().getId(value)));
	}

	@Override
	public Block read(Object from) {
		if (from instanceof Block b) {
			return b;
		} else if (from instanceof JsonPrimitive json) {
			return KubeJSRegistries.blocks().get(new ResourceLocation(json.getAsString()));
		} else {
			return KubeJSRegistries.blocks().get(new ResourceLocation(String.valueOf(from)));
		}
	}

	@Override
	public String toString() {
		return componentType();
	}

	@Override
	public boolean hasInput(RecipeKJS recipe, Block value, ReplacementMatch match) {
		return type == RecipeComponentType.INPUT && match instanceof IngredientMatch m && m.contains(value);
	}

	@Override
	public boolean hasOutput(RecipeKJS recipe, Block value, ReplacementMatch match) {
		return type == RecipeComponentType.OUTPUT && match instanceof IngredientMatch m && m.contains(value);
	}
}
