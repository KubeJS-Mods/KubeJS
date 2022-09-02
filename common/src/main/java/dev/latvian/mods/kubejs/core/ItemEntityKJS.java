package dev.latvian.mods.kubejs.core;

import dev.architectury.hooks.level.entity.ItemEntityHooks;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemEntityKJS extends EntityKJS {
	@Override
	default ItemEntity kjs$self() {
		return (ItemEntity) this;
	}

	@Override
	@Nullable
	default ItemStack kjs$getItem() {
		var stack = kjs$self().getItem();
		return stack.isEmpty() ? null : stack;
	}

	default int kjs$getLifespan() {
		return ItemEntityHooks.lifespan(kjs$self()).getAsInt();
	}

	default void kjs$setLifespan(int lifespan) {
		ItemEntityHooks.lifespan(kjs$self()).accept(lifespan);
	}

	default void kjs$setDefaultPickUpDelay() {
		kjs$self().setPickUpDelay(10);
	}

	default void kjs$setNoPickUpDelay() {
		kjs$self().setPickUpDelay(0);
	}

	default void kjs$setInfinitePickUpDelay() {
		kjs$self().setPickUpDelay(Short.MAX_VALUE);
	}

	default void kjs$setNoDespawn() {
		kjs$self().setUnlimitedLifetime();
	}

	default int kjs$getTicksUntilDespawn() {
		return ItemEntity.LIFETIME - kjs$self().age;
	}

	default void kjs$setTicksUntilDespawn(int ticks) {
		kjs$self().age = ItemEntity.LIFETIME - ticks;
	}
}
