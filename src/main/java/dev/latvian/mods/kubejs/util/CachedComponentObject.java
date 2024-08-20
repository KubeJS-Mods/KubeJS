package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import io.netty.buffer.Unpooled;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.UUID;

public record CachedComponentObject<T extends RegistryObjectKJS<T>, S>(UUID cacheKey, T value, S stack, DataComponentPatch components) {
	public static <T extends RegistryObjectKJS<T>> void writeCacheKey(FriendlyByteBuf buf, T value, DataComponentPatch components) {
		buf.writeUtf(value.kjs$getId());
		buf.writeVarInt(components.size());

		for (var entry : components.entrySet()) {
			var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey());
			buf.writeUtf(key.getNamespace());
			buf.writeUtf(key.getPath());
			buf.writeBoolean(entry.getValue().isPresent());

			if (entry.getValue().isPresent()) {
				buf.writeVarInt(entry.getValue().get().hashCode());
			}
		}
	}

	public static <T extends RegistryObjectKJS<T>, S> CachedComponentObject<T, S> of(T value, S stack, DataComponentPatch components) {
		var buf = new FriendlyByteBuf(Unpooled.buffer());
		writeCacheKey(buf, value, components);
		return new CachedComponentObject<>(UUID.nameUUIDFromBytes(buf.array()), value, stack, components);
	}

	public static CachedComponentObject<Item, ItemStack> ofItemStack(ItemStack stack, boolean visual) {
		return of(stack.getItem(), stack, visual ? DataComponentWrapper.visualPatch(stack.getComponentsPatch()) : stack.getComponentsPatch());
	}

	public static CachedComponentObject<Fluid, FluidStack> ofFluidStack(FluidStack stack, boolean visual) {
		return of(stack.getFluid(), stack, visual ? DataComponentWrapper.visualPatch(stack.getComponentsPatch()) : stack.getComponentsPatch());
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof CachedComponentObject<?, ?> c && value == c.value && components.equals(c.components);
	}
}
