package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record BlockStateComponent(ResourceKey<RecipeComponentType<?>> type, boolean preferObjectForm, boolean allowEmpty) implements RecipeComponent<BlockState> {
	public static final TypeInfo TYPE_INFO = TypeInfo.of(BlockState.class);

	public static final BlockStateComponent BLOCK_STATE = new BlockStateComponent(RecipeComponentType.builtin("block_state"), true, false);
	public static final BlockStateComponent BLOCK_STATE_STRING = new BlockStateComponent(RecipeComponentType.builtin("block_state_string"), false, false);
	public static final BlockStateComponent OPTIONAL_BLOCK_STATE = new BlockStateComponent(RecipeComponentType.builtin("optional_block_state"), true, true);
	public static final BlockStateComponent OPTIONAL_BLOCK_STATE_STRING = new BlockStateComponent(RecipeComponentType.builtin("optional_block_state_string"), false, true);

	@Override
	public Codec<BlockState> codec() {
		return BlockState.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE_INFO;
	}

	@Override
	public BlockState wrap(RecipeScriptContext cx, @Nullable Object from) {
		return switch (from) {
			case BlockState s -> s;
			case Block b -> b.defaultBlockState();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(cx.cx(), json.getAsString());
			case null, default -> {
				var map = cx.cx().optionalMapOf(from);

				if (map == null) {
					yield BlockWrapper.parseBlockState(cx.cx(), String.valueOf(from));
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
