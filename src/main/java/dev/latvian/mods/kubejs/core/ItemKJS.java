package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemKJS extends IngredientSupplierKJS, RegistryObjectKJS<Item> {
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

	default void kjs$setNameKey(String key) {
		throw new NoMixinException();
	}

	default ItemStackKey kjs$getTypeItemStackKey() {
		throw new NoMixinException();
	}
}
