package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.kubejs.item.ChancedItem;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.rhino.util.ToStringJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RemapPrefixForJS("kjs$")
public interface ItemStackKJS extends
	SpecialEquality,
	WithCodec,
	IngredientSupplierKJS,
	ToStringJS,
	OutputReplacement,
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

	@Nullable
	default Block kjs$getBlock() {
		return kjs$self().getItem() instanceof BlockItem bi ? bi.getBlock() : null;
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
		return DataComponentWrapper.patchToString(new StringBuilder(), cx.getRegistries(), kjs$self().getComponentsPatch()).toString();
	}

	default ItemStack kjs$set(DataComponentType<?> component, Object value) {
		var is = kjs$self();

		if (value == null || Undefined.isUndefined(value)) {
			is.remove(component);
		} else {
			is.set((DataComponentType) component, value);
		}

		return is;
	}

	default ItemStack kjs$remove(DataComponentType<?> component) {
		var is = kjs$self();
		is.remove(component);
		return is;
	}

	default ItemStack kjs$set(DataComponentMap components) {
		var is = kjs$self();
		is.applyComponents(components);
		return is;
	}

	default ItemStack kjs$applyPatch(DataComponentPatch components) {
		var is = kjs$self();
		is.applyComponents(components);
		return is;
	}

	default ItemStack kjs$setCustomName(@Nullable Component displayName) {
		var is = kjs$self();

		if (displayName != null) {
			is.set(DataComponents.CUSTOM_NAME, displayName);
		} else {
			is.remove(DataComponents.CUSTOM_NAME);
		}

		return is;
	}

	@Nullable
	default Component kjs$getCustomName() {
		return kjs$self().get(DataComponents.CUSTOM_NAME);
	}

	default ItemEnchantments kjs$getEnchantments() {
		return kjs$self().get(DataComponents.ENCHANTMENTS);
	}

	default boolean kjs$hasEnchantment(Enchantment enchantment, int level) {
		var e = kjs$getEnchantments();
		return e != null && e.getLevel(enchantment) >= level;
	}

	default ItemStack kjs$enchant(Enchantment enchantment, int level) {
		var is = kjs$self();
		is.enchant(enchantment, level);
		return is;
	}

	default ItemStack kjs$enchant(Map<Enchantment, Integer> enchantments) {
		var is = kjs$self().copy();

		for (var entry : enchantments.entrySet()) {
			is.enchant(entry.getKey(), entry.getValue());
		}

		return is;
	}

	@Override
	default String kjs$getMod() {
		return kjs$self().getItem().kjs$getMod();
	}

	default boolean kjs$areItemsEqual(ItemStack other) {
		return kjs$self().getItem() == other.getItem();
	}

	default boolean kjs$areComponentsEqual(ItemStack other) {
		return ItemStack.isSameItemSameComponents(kjs$self(), other);
	}

	default float kjs$getHarvestSpeed(@Nullable BlockContainerJS block) {
		return kjs$self().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
	}

	default float kjs$getHarvestSpeed() {
		return kjs$getHarvestSpeed(null);
	}

	default Map<String, Object> kjs$getTypeData() {
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
			DataComponentWrapper.patchToString(builder, registries, is.getComponentsPatch());
		}

		builder.append('\'');
		return builder.toString();
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self().getItem().kjs$asIngredient();
	}

	@Override
	default Codec<ItemStack> getCodec(Context cx) {
		return ItemStack.CODEC;
	}

	default ChancedItem kjs$withChance(FloatProvider chance) {
		return new ChancedItem(kjs$self(), chance);
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

	@Override
	default Object replaceOutput(Context cx, KubeRecipe recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof ItemStack o) {
			var replacement = kjs$self().copy();
			replacement.setCount(o.getCount());
			return replacement;
		}

		return kjs$self().copy(); // return this?
	}
}
