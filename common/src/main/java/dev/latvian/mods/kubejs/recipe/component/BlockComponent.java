package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
		} else if (from instanceof BlockState s) {
			return s.getBlock();
		} else if (from instanceof JsonPrimitive json) {
			return UtilsJS.parseBlockState(json.getAsString()).getBlock();
		} else {
			return UtilsJS.parseBlockState(String.valueOf(from)).getBlock();
		}
	}

	@Override
	public boolean isInput(RecipeJS recipe, Block value, ReplacementMatch match) {
		return crole.isInput() && match instanceof BlockStatePredicate m2 && m2.testBlock(value);
	}

	@Override
	public boolean isOutput(RecipeJS recipe, Block value, ReplacementMatch match) {
		return crole.isOutput() && match instanceof BlockStatePredicate m2 && m2.testBlock(value);
	}

	@Override
	public String checkEmpty(RecipeKey<Block> key, Block value) {
		if (value == Blocks.AIR) {
			return "Block '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	public String toString() {
		return componentType();
	}
}
