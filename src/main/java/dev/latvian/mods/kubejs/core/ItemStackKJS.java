package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.kubejs.item.ChancedItem;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.rhino.util.ToStringJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
	Replaceable,
	MutableDataComponentHolderKJS,
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

		return kjs$equalsIgnoringCount(ItemStackJS.wrap(((KubeJSContext) cx).getRegistries(), o));
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

	@ReturnsSelf(copy = true)
	default ItemStack kjs$withCount(int c) {
		if (c <= 0 || kjs$self().isEmpty()) {
			return ItemStack.EMPTY;
		}

		var is = kjs$self().copy();
		is.setCount(c);
		return is;
	}

	@ReturnsSelf
	default ItemStack kjs$resetComponents() {
		var is = kjs$self();
		is.applyComponents(is.getPrototype());
		return is;
	}

	default String kjs$getComponentString(KubeJSContext cx) {
		return DataComponentWrapper.patchToString(new StringBuilder(), cx.getNbtOps(), kjs$self().getComponentsPatch()).toString();
	}

	@ReturnsSelf(copy = true)
	default ItemStack kjs$withCustomName(@Nullable Component name) {
		return (ItemStack) kjs$self().copy().kjs$setCustomName(name);
	}

	@ReturnsSelf
	default ItemStack kjs$setRepairCost(int repairCost) {
		var is = kjs$self();
		is.set(DataComponents.REPAIR_COST, repairCost);
		return is;
	}

	default ItemEnchantments kjs$getEnchantments() {
		return EnchantmentHelper.getEnchantmentsForCrafting(kjs$self());
	}

	default boolean kjs$hasEnchantment(Holder<Enchantment> enchantment, int level) {
		var e = kjs$getEnchantments();
		return e != null && e.getLevel(enchantment) >= level;
	}

	@ReturnsSelf
	default ItemStack kjs$enchant(Holder<Enchantment> enchantment, int level) {
		var is = kjs$self();
		is.enchant(enchantment, level);
		return is;
	}

	@ReturnsSelf(copy = true)
	default ItemStack kjs$enchant(ItemEnchantments enchantments) {
		var is = kjs$self().copy();

		EnchantmentHelper.updateEnchantments(is, mutable -> {
			for (var entry : enchantments.entrySet()) {
				mutable.upgrade(entry.getKey(), entry.getValue());
			}
		});

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
		return kjs$toItemString0(((KubeJSContext) cx).getNbtOps());
	}

	default String kjs$toItemString(KubeJSContext cx) {
		return kjs$toItemString0(cx.getNbtOps());
	}

	default String kjs$toItemString0(DynamicOps<Tag> dynamicOps) {
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
			DataComponentWrapper.patchToString(builder, dynamicOps, is.getComponentsPatch());
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

	@ReturnsSelf(copy = true)
	default ItemStack kjs$withLore(Component[] lines) {
		var is = kjs$self().copy();
		is.set(DataComponents.LORE, new ItemLore(List.of(lines)));
		return is;
	}

	@ReturnsSelf(copy = true)
	default ItemStack kjs$withLore(Component[] lines, Component[] styledLines) {
		var is = kjs$self().copy();
		is.set(DataComponents.LORE, new ItemLore(List.of(lines), List.of(styledLines)));
		return is;
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		var t = kjs$self();
		var r = ItemStackJS.wrap(((KubeJSContext) cx).getRegistries(), with);

		if (!ItemStack.isSameItemSameComponents(t, r)) {
			r.setCount(t.getCount());
			return r;
		}

		return this;
	}
}
