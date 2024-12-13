package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateComponent(boolean preferObjectForm) implements RecipeComponent<BlockState> {
	public static final RecipeComponent<BlockState> BLOCK = new BlockStateComponent(true);
	public static final RecipeComponent<BlockState> BLOCK_STRING = new BlockStateComponent(false);

	@Override
	public Codec<BlockState> codec() {
		return BlockState.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(BlockState.class);
	}

	@Override
	public BlockState wrap(Context cx, KubeRecipe recipe, Object from) {
		return switch (from) {
			case BlockState s -> s;
			case Block b -> b.defaultBlockState();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(RegistryAccessContainer.of(cx), json.getAsString());
			case null, default -> {
				var map = cx.optionalMapOf(from);

				if (map == null) {
					yield BlockWrapper.parseBlockState(RegistryAccessContainer.of(cx), String.valueOf(from));
				} else {
					// this is formatted like so:
					// { Name: "blockid", Properties: {Property: "value"}}
					yield BlockState.CODEC.parse(JsonOps.INSTANCE, JsonUtils.GSON.toJsonTree(from)).getPartialOrThrow(message -> {
						throw new KubeRuntimeException("Failed to parse blockstate: " + message);
					});
				}
			}
		};
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, BlockState value, ReplacementMatchInfo match) {
		return match.match() instanceof BlockStatePredicate m2 && m2.test(value);
	}

	@Override
	public boolean isEmpty(BlockState value) {
		return value.getBlock() == Blocks.AIR;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, BlockState value) {
		builder.append(value.kjs$getIdLocation());
	}

	@Override
	public String toString() {
		return preferObjectForm ? "block_state" : "block_state_string";
	}
}
