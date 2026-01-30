package dev.latvian.mods.kubejs.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class KubeJSModelPropertyRegistry {
	public interface ConditionalCallback {
		boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext);
	}

	private static final Object2ObjectOpenHashMap<Identifier, ConditionalCallback> CONDITIONAL = new Object2ObjectOpenHashMap<>();

	public static void putConditional(Identifier id, ConditionalCallback cb) {
		CONDITIONAL.put(id, cb);
	}

	public static ConditionalCallback getConditional(Identifier id) {
		return CONDITIONAL.get(id);
	}
}
