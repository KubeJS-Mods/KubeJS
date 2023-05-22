package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockComponent implements RecipeComponent<Block> {
	public static final RecipeComponent<Block> BLOCK = new BlockComponent();

	@Override
	public String componentType() {
		return "block";
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
}
