package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockComponent() implements RecipeComponent<Block> {
	public static final RecipeComponentType<Block> BLOCK = RecipeComponentType.unit(KubeJS.id("block"), new BlockComponent());

	@Override
	public RecipeComponentType<?> type() {
		return BLOCK;
	}

	@Override
	public Codec<Block> codec() {
		return BuiltInRegistries.BLOCK.byNameCodec();
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(Block.class);
	}

	@Override
	public Block wrap(Context cx, KubeRecipe recipe, Object from) {
		return switch (from) {
			case Block b -> b;
			case BlockState s -> s.getBlock();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(RegistryAccessContainer.of(cx), json.getAsString()).getBlock();
			case null, default -> BlockWrapper.parseBlockState(RegistryAccessContainer.of(cx), String.valueOf(from)).getBlock();
		};
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Block value, ReplacementMatchInfo match) {
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
		return "block";
	}
}
