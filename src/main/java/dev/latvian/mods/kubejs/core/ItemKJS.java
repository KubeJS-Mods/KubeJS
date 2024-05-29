package dev.latvian.mods.kubejs.core;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
public interface ItemKJS extends IngredientSupplierKJS, WithRegistryKeyKJS<Item> {
	@RemapForJS("getItem")
	default Item kjs$self() {
		return (Item) this;
	}

	@Override
	default RegistryInfo<Item> kjs$getKubeRegistry() {
		return RegistryInfo.ITEM;
	}

	@Nullable
	default ItemBuilder kjs$getItemBuilder() {
		throw new NoMixinException();
	}

	default void kjs$setItemBuilder(ItemBuilder b) {
		throw new NoMixinException();
	}

	default CompoundTag kjs$getTypeData() {
		throw new NoMixinException();
	}

	default <T> void kjs$overrideComponent(DataComponentType<T> type, T value) {
		throw new NoMixinException();
	}

	default void kjs$setCraftingRemainder(Item i) {
		throw new NoMixinException();
	}

	default void kjs$setMaxStackSize(int i) {
		kjs$overrideComponent(DataComponents.MAX_STACK_SIZE, i);
	}

	default void kjs$setBurnTime(int i) {
		FuelRegistry.register(i, (Item) this);
	}

	default void kjs$setTier(Consumer<MutableToolTier> c) {
		if (this instanceof TieredItem tiered) {
			tiered.tier = Util.make(new MutableToolTier(tiered.tier), c);
		} else {
			throw new IllegalArgumentException("Item is not a tool/tiered item!");
		}
	}

	default void kjs$setNameKey(String key) {
		throw new NoMixinException();
	}

	default ItemStackKey kjs$getTypeItemStackKey() {
		throw new NoMixinException();
	}
}
