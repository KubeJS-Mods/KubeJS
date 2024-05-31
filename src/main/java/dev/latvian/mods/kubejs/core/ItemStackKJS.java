package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJSComponents;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.JsonSerializable;
import dev.latvian.mods.kubejs.util.NBTSerializable;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.rhino.util.ToStringJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RemapPrefixForJS("kjs$")
public interface ItemStackKJS extends
	SpecialEquality,
	NBTSerializable,
	JsonSerializable,
	IngredientSupplierKJS,
	ToStringJS,
	RegistryObjectKJS<Item> {
	default ItemStack kjs$self() {
		return (ItemStack) this;
	}

	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		if (o instanceof CharSequence) {
			return kjs$getId().equals(ID.string(o.toString()));
		} else if (o instanceof ItemStack s) {
			return kjs$equalsIgnoringCount(s);
		}

		return kjs$equalsIgnoringCount(ItemStackJS.of(o));
	}

	default boolean kjs$equalsIgnoringCount(ItemStack stack) {
		var self = kjs$self();

		if (self == stack) {
			return true;
		} else if (self.isEmpty()) {
			return stack.isEmpty();
		}

		return ItemStack.isSameItemSameComponents(self, stack);
	}

	@Override
	default RegistryInfo<Item> kjs$getKubeRegistry() {
		return RegistryInfo.ITEM;
	}

	@Override
	default ResourceLocation kjs$getIdLocation() {
		return kjs$self().getItem().kjs$getIdLocation();
	}

	@Override
	default Holder<Item> kjs$asHolder() {
		return kjs$self().getItem().kjs$asHolder();
	}

	@Override
	default ResourceKey<Item> kjs$getRegistryKey() {
		return kjs$self().getItem().kjs$getRegistryKey();
	}

	@Override
	default String kjs$getId() {
		return kjs$self().getItem().kjs$getId();
	}

	default boolean kjs$isBlock() {
		return kjs$self().getItem() instanceof BlockItem;
	}

	default ItemStack kjs$withCount(int c) {
		if (c <= 0 || kjs$self().isEmpty()) {
			return ItemStack.EMPTY;
		}

		var is = kjs$self().copy();
		is.setCount(c);
		return is;
	}

	default void kjs$removeTag() {
		ItemStackJS.setTag(kjs$self(), null);
	}

	default String kjs$getComponentString(KubeJSContext cx) {
		return KubeJSComponents.patchToString(new StringBuilder(), cx.getRegistries(), kjs$self().getComponentsPatch()).toString();
	}

	default ItemStack kjs$withComponents(DataComponentMap components) {
		var is = kjs$self().copy();
		is.applyComponents(components);
		return is;
	}

	default ItemStack kjs$withComponentPatch(DataComponentPatch components) {
		var is = kjs$self().copy();
		is.applyComponents(components);
		return is;
	}

	default ItemStack kjs$withName(@Nullable Component displayName) {
		var is = kjs$self().copy();

		if (displayName != null) {
			is.set(DataComponents.CUSTOM_NAME, displayName);
		} else {
			is.remove(DataComponents.CUSTOM_NAME);
		}

		return is;
	}

	default ItemEnchantments kjs$getEnchantments() {
		return kjs$self().get(DataComponents.ENCHANTMENTS);
	}

	default boolean kjs$hasEnchantment(Enchantment enchantment, int level) {
		var e = kjs$getEnchantments();
		return e != null && e.getLevel(enchantment) >= level;
	}

	@RemapForJS("enchant")
	default ItemStack kjs$enchantCopy(Map<?, ?> enchantments) {
		var is = kjs$self();

		for (var entry : enchantments.entrySet()) {
			var enchantment = RegistryInfo.ENCHANTMENT.getValue(ID.mc(entry.getKey()));

			if (enchantment != null && entry.getValue() instanceof Number number) {
				is = is.kjs$enchantCopy(enchantment, number.intValue());
			}
		}

		return is;
	}

	@RemapForJS("enchant")
	default ItemStack kjs$enchantCopy(Enchantment enchantment, int level) {
		var is = kjs$self().copy();
		is.enchant(enchantment, level);
		return is;
	}

	@Override
	default String kjs$getMod() {
		return kjs$self().getItem().kjs$getMod();
	}

	@Deprecated
	default Ingredient kjs$ignoreNBT(Context cx) {
		var console = ConsoleJS.getCurrent(cx);
		console.warn("You don't need to call .ignoreNBT() anymore, all item ingredients ignore NBT by default!");
		return kjs$self().getItem().kjs$asIngredient();
	}

	default boolean kjs$areItemsEqual(ItemStack other) {
		return kjs$self().getItem() == other.getItem();
	}

	default boolean kjs$isNBTEqual(ItemStack other) {
		return ItemStack.isSameItemSameComponents(kjs$self(), other);
	}

	default float kjs$getHarvestSpeed(@Nullable BlockContainerJS block) {
		return kjs$self().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
	}

	default float kjs$getHarvestSpeed() {
		return kjs$getHarvestSpeed(null);
	}

	@Override
	@RemapForJS("toNBT")
	default CompoundTag toNBTJS(Context cx) {
		return (CompoundTag) kjs$self().save(((KubeJSContext) cx).getRegistries(), new CompoundTag());
	}

	/* default String kjs$getCreativeTab() {
		var cat = kjs$self().getItem().getItemCategory();
		return cat == null ? "" : cat.getRecipeFolderName();
	}*/

	default CompoundTag kjs$getTypeData() {
		return kjs$self().getItem().kjs$getTypeData();
	}

	@Override
	default String toStringJS(Context cx) {
		return kjs$toItemString0(((KubeJSContext) cx).getRegistries());
	}

	default String kjs$toItemString(KubeJSContext cx) {
		return kjs$toItemString0(cx.getRegistries());
	}

	default String kjs$toItemString0(HolderLookup.Provider registries) {
		var is = kjs$self();
		var count = is.getCount();

		if (count <= 0) {
			return "minecraft:air";
		}

		var builder = new StringBuilder();
		builder.append('\'');

		if (count > 1) {
			builder.append(count);
			builder.append("x ");
		}

		builder.append(kjs$getId());

		if (!is.isComponentsPatchEmpty()) {
			KubeJSComponents.patchToString(builder, registries, is.getComponentsPatch());
		}

		builder.append('\'');
		return builder.toString();
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self().getItem().kjs$asIngredient();
	}

	@Override
	default JsonElement toJsonJS() {
		return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, kjs$self()).result().orElse(JsonNull.INSTANCE);
	}

	default OutputItem kjs$withChance(double chance) {
		return OutputItem.create(kjs$self(), chance);
	}

	default ItemStack kjs$withLore(Component[] lines) {
		var is = kjs$self().copy();
		is.set(DataComponents.LORE, new ItemLore(List.of(lines)));
		return is;
	}

	default ItemStack kjs$withLore(Component[] lines, Component[] styledLines) {
		var is = kjs$self().copy();
		is.set(DataComponents.LORE, new ItemLore(List.of(lines), List.of(styledLines)));
		return is;
	}
}
