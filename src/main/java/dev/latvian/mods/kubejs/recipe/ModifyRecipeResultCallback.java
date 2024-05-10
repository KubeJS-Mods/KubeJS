package dev.latvian.mods.kubejs.recipe;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
	// FIXME: Implement properly
	Codec<ModifyRecipeResultCallback> CODEC = Codec.unit(null);

	StreamCodec<ByteBuf, ModifyRecipeResultCallback> STREAM_CODEC = StreamCodec.unit(null);

	ItemStack modify(ModifyRecipeCraftingGrid grid, ItemStack result);
}
