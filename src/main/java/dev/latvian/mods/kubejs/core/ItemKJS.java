package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RemapPrefixForJS("kjs$")
public interface ItemKJS extends IngredientSupplierKJS, RegistryObjectKJS<Item> {
	@RemapForJS("getItem")
	default Item kjs$self() {
		return (Item) this;
	}

	@Override
	default ResourceKey<Registry<Item>> kjs$getRegistryId() {
		return Registries.ITEM;
	}

	@Override
	default Registry<Item> kjs$getRegistry() {
		return BuiltInRegistries.ITEM;
	}

	@Nullable
	default ItemBuilder kjs$getItemBuilder() {
		throw new NoMixinException();
	}

	default void kjs$setItemBuilder(ItemBuilder b) {
		throw new NoMixinException();
	}

	default Map<String, Object> kjs$getTypeData() {
		throw new NoMixinException();
	}

	@HideFromJS
	default <T> void kjs$overrideComponent(DataComponentType<T> type, @Nullable T value) {
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

	default void kjs$setCanRepair(boolean repairable) {
		throw new NoMixinException();
	}
}
