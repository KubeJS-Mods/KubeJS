package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateComponent(RecipeComponentType<?> type, boolean preferObjectForm, boolean allowEmpty) implements RecipeComponent<BlockState> {
	public static final TypeInfo TYPE_INFO = TypeInfo.of(BlockState.class);
	public static final RecipeComponentType<BlockState> BLOCK = RecipeComponentType.unit(KubeJS.id("block_state"), type -> new BlockStateComponent(type, true, false));
	public static final RecipeComponentType<BlockState> BLOCK_STRING = RecipeComponentType.unit(KubeJS.id("block_state_string"), type -> new BlockStateComponent(type, false, false));
	public static final RecipeComponentType<BlockState> OPTIONAL_BLOCK = RecipeComponentType.unit(KubeJS.id("optional_block_state"), type -> new BlockStateComponent(type, true, true));
	public static final RecipeComponentType<BlockState> OPTIONAL_BLOCK_STRING = RecipeComponentType.unit(KubeJS.id("optional_block_state_string"), type -> new BlockStateComponent(type, false, true));

	@Override
	public Codec<BlockState> codec() {
		return BlockState.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE_INFO;
	}

	@Override
	public BlockState wrap(RecipeScriptContext cx, Object from) {
		return switch (from) {
			case BlockState s -> s;
			case Block b -> b.defaultBlockState();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(cx.registries(), json.getAsString());
			case null, default -> {
				var map = cx.cx().optionalMapOf(from);

				if (map == null) {
					yield BlockWrapper.parseBlockState(cx.registries(), String.valueOf(from));
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
	public boolean matches(RecipeMatchContext cx, BlockState value, ReplacementMatchInfo match) {
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
		return type.toString();
	}

	@Override
	public String toString(OpsContainer ops, BlockState value) {
		return value.kjs$toString();
	}
}
