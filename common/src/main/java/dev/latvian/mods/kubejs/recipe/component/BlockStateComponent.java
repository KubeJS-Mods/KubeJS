package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateComponent(ComponentRole crole) implements RecipeComponent<BlockState> {
	public static final RecipeComponent<BlockState> INPUT = new BlockStateComponent(ComponentRole.INPUT);
	public static final RecipeComponent<BlockState> OUTPUT = new BlockStateComponent(ComponentRole.OUTPUT);
	public static final RecipeComponent<BlockState> BLOCK = new BlockStateComponent(ComponentRole.OTHER);

	@Override
	public ComponentRole role() {
		return crole;
	}

	@Override
	public String componentType() {
		return "block_state";
	}

	@Override
	public Class<?> componentClass() {
		return BlockState.class;
	}

	@Override
	public JsonPrimitive write(RecipeJS recipe, BlockState value) {
		return new JsonPrimitive(BlockStateParser.serialize(value));
	}

	@Override
	public BlockState read(RecipeJS recipe, Object from) {
		if (from instanceof BlockState s) {
			return s;
		} else if (from instanceof Block b) {
			return b.defaultBlockState();
		} else if (from instanceof JsonPrimitive json) {
			return UtilsJS.parseBlockState(json.getAsString());
		} else {
			return UtilsJS.parseBlockState(String.valueOf(from));
		}
	}

	@Override
	public boolean isInput(RecipeJS recipe, BlockState value, ReplacementMatch match) {
		return crole.isInput() && match instanceof BlockStatePredicate m2 && m2.test(value);
	}

	@Override
	public boolean isOutput(RecipeJS recipe, BlockState value, ReplacementMatch match) {
		return crole.isOutput() && match instanceof BlockStatePredicate m2 && m2.test(value);
	}

	@Override
	public String checkEmpty(RecipeKey<BlockState> key, BlockState value) {
		if (value.getBlock() == Blocks.AIR) {
			return "Block '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	public String toString() {
		return componentType();
	}
}
