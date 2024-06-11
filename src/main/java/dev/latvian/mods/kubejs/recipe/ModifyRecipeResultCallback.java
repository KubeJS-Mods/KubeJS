package dev.latvian.mods.kubejs.recipe;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
	record Holder(UUID id, ModifyRecipeResultCallback callback) {
		public static final Map<UUID, Holder> SERVER = new HashMap<>();
		public static final Codec<Holder> CODEC = UUIDUtil.STRING_CODEC.xmap(SERVER::get, Holder::id);
		public static final StreamCodec<ByteBuf, Holder> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(uuid -> new Holder(uuid, null), Holder::id);
	}

	ItemStack modify(ModifyRecipeCraftingGrid grid, ItemStack result);
}
