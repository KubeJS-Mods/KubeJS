package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public record BlockComponent(ComponentRole crole) implements RecipeComponent<Block> {
	public static final RecipeComponent<Block> INPUT = new BlockComponent(ComponentRole.INPUT);
	public static final RecipeComponent<Block> OUTPUT = new BlockComponent(ComponentRole.OUTPUT);
	public static final RecipeComponent<Block> BLOCK = new BlockComponent(ComponentRole.OTHER);

	@Override
	public String componentType() {
		return "block";
	}

	@Override
	public ComponentRole role() {
		return crole;
	}

	@Override
	public Class<?> componentClass() {
		return Block.class;
	}

	@Override
	public JsonPrimitive write(RecipeJS recipe, Block value) {
		return new JsonPrimitive(String.valueOf(KubeJSRegistries.blocks().getId(value)));
	}

	@Override
	public Block read(RecipeJS recipe, Object from) {
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
	public boolean isInput(RecipeJS recipe, Block value, ReplacementMatch match) {
		return crole.isInput() && match instanceof ItemMatch m && m.contains(value);
	}

	@Override
	public boolean isOutput(RecipeJS recipe, Block value, ReplacementMatch match) {
		return crole.isOutput() && match instanceof ItemMatch m && m.contains(value);
	}
}
