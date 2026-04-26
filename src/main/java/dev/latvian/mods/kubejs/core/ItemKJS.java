package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@RemapPrefixForJS("kjs$")
public interface ItemKJS extends IngredientSupplierKJS, RegistryObjectKJS<Item> {
	@RemapForJS("getItem")
	default Item kjs$self() {
		return (Item) this;
	}

	default DataComponentMap kjs$getComponents() {
		return this.kjs$self().components();
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

	default void kjs$setCraftingRemainder(ItemStackTemplate i) {
		throw new NoMixinException();
	}

	default void kjs$setNameKey(String key) {
		throw new NoMixinException();
	}

	default void kjs$setCanRepair(boolean repairable) {
		throw new NoMixinException();
	}
}
