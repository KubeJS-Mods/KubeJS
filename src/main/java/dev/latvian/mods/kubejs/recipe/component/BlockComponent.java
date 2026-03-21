package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record BlockComponent(boolean allowEmpty) implements RecipeComponent<Block> {
	private static final Codec<Block> CODEC = BuiltInRegistries.BLOCK.byNameCodec();

	private static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.key(KubeJS.id("block"));
	private static final ResourceKey<RecipeComponentType<?>> OPTIONAL_TYPE = RecipeComponentType.builtin("optional_block");

	public static final BlockComponent BLOCK = new BlockComponent(false);
	public static final BlockComponent OPTIONAL_BLOCK = new BlockComponent(true);

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return allowEmpty ? OPTIONAL_TYPE : TYPE;
	}

	@Override
	public Codec<Block> codec() {
		return CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(Block.class);
	}

	@Override
	public Block wrap(RecipeScriptContext cx, @Nullable Object from) {
		return switch (from) {
			case Block b -> b;
			case BlockState s -> s.getBlock();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(cx.cx(), json.getAsString()).getBlock();
			case null, default -> BlockWrapper.parseBlockState(cx.cx(), String.valueOf(from)).getBlock();
		};
	}

	@Override
	public boolean matches(RecipeMatchContext cx, Block value, ReplacementMatchInfo match) {
		return match.match() instanceof BlockStatePredicate m2 && m2.testBlock(value);
	}

	@Override
	public boolean isEmpty(Block value) {
		return value == Blocks.AIR;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Block value) {
		builder.append(value.kjs$getIdLocation());
	}

	@Override
	public String toString() {
		return allowEmpty ? "optional_block" : "block";
	}

	@Override
	public String toString(OpsContainer ops, Block value) {
		return value.kjs$getId();
	}
}
