package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

import static dev.latvian.mods.kubejs.util.JsonIO.GSON;

public record BlockStateComponent(ComponentRole crole, boolean preferObjectForm) implements RecipeComponent<BlockState> {
	public static final RecipeComponent<BlockState> INPUT = new BlockStateComponent(ComponentRole.INPUT, true);
	public static final RecipeComponent<BlockState> OUTPUT = new BlockStateComponent(ComponentRole.OUTPUT, true);
	public static final RecipeComponent<BlockState> BLOCK = new BlockStateComponent(ComponentRole.OTHER, true);
	public static final RecipeComponent<BlockState> INPUT_STRING = new BlockStateComponent(ComponentRole.INPUT, false);
	public static final RecipeComponent<BlockState> OUTPUT_STRING = new BlockStateComponent(ComponentRole.OUTPUT, false);
	public static final RecipeComponent<BlockState> BLOCK_STRING = new BlockStateComponent(ComponentRole.OTHER, false);


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
	public JsonElement write(RecipeJS recipe, BlockState value) {
		if (preferObjectForm) {
			return BlockState.CODEC.encode(value, JsonOps.INSTANCE, new JsonObject()).getOrThrow(true, message -> {
				throw new RecipeExceptionJS("Failed to write blockstate to object form: " + message);
			});
		} else {
			return new JsonPrimitive(BlockStateParser.serialize(value));
		}

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
			Map<?, ?> map = MapJS.of(from);
			if (map == null) {
				return UtilsJS.parseBlockState(String.valueOf(from));
			} else
			// this is formatted like so:
			// { Name: "blockid", Properties: {Property: "value"}}
			{
				return BlockState.CODEC.parse(JsonOps.INSTANCE, GSON.toJsonTree(from)).getOrThrow(true, message -> {
					throw new RecipeExceptionJS("Failed to parse blockstate: " + message);
				});
			}
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
