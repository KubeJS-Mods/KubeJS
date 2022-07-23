package dev.latvian.mods.kubejs.core;

import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IgnoreNBTIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.WeakNBTIngredientJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RemapPrefixForJS("kjs$")
public interface ItemStackKJS extends AsKJS<ItemStackJS>, SpecialEquality, NBTSerializable {
	@Override
	default ItemStackJS asKJS() {
		return ItemStackJS.of(this);
	}

	ItemStack kjs$getSelf();

	@Override
	default boolean specialEquals(Object o, boolean shallow) {
		return SpecialEquality.super.specialEquals(o, shallow);
	}

	default String kjs$getId() {
		return String.valueOf(Registries.getId(kjs$getSelf().getItem(), Registry.ITEM_REGISTRY));
	}

	default Collection<ResourceLocation> kjs$getTags() {
		return Tags.byItem(kjs$getSelf().getItem()).map(TagKey::location).collect(Collectors.toSet());
	}

	default boolean kjs$hasTag(ResourceLocation tag) {
		return kjs$getSelf().is(Tags.item(tag));
	}

	default boolean kjs$isBlock() {
		return kjs$getSelf().getItem() instanceof BlockItem;
	}

	default ItemStack kjs$withCount(int c) {
		if (c <= 0) {
			return ItemStack.EMPTY;
		}

		var is = kjs$getSelf().copy();
		is.setCount(c);
		return is;
	}

	default void kjs$removeTag() {
		kjs$getSelf().setTag(null);
	}

	default String kjs$getNbtString() {
		return String.valueOf(kjs$getSelf().getTag());
	}

	default ItemStack kjs$withNBT(CompoundTag nbt) {
		var is = kjs$getSelf().copy();

		if (is.getTag() == null) {
			is.setTag(nbt);
		} else {
			if (nbt != null && !nbt.isEmpty()) {
				for (var key : nbt.getAllKeys()) {
					is.getTag().put(key, nbt.get(key));
				}
			}
		}

		return is;
	}

	default ItemStackJS kjs$withName(@Nullable Component displayName) {
		var is = kjs$getSelf().copy();

		if (displayName != null) {
			is.setHoverName(displayName);
		} else {
			is.resetHoverName();
		}

		return new ItemStackJS(is);
	}

	default Map<String, Integer> kjs$getEnchantments() {
		var map = new HashMap<String, Integer>();

		for (var entry : EnchantmentHelper.getEnchantments(kjs$getSelf()).entrySet()) {
			var id = KubeJSRegistries.enchantments().getId(entry.getKey());

			if (id != null) {
				map.put(id.toString(), entry.getValue());
			}
		}

		return map;
	}

	default boolean kjs$hasEnchantment(Enchantment enchantment, int level) {
		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, kjs$getSelf()) >= level;
	}

	@RemapForJS("enchant")
	default ItemStack kjs$enchantCopy(Map<?, ?> enchantments) {
		var is = kjs$getSelf();

		for (var entry : enchantments.entrySet()) {
			var enchantment = KubeJSRegistries.enchantments().get(UtilsJS.getMCID(entry.getKey()));

			if (enchantment != null && entry.getValue() instanceof Number number) {
				is = is.kjs$enchantCopy(enchantment, number.intValue());
			}
		}

		return is;
	}

	@RemapForJS("enchant")
	default ItemStack kjs$enchantCopy(Enchantment enchantment, int level) {
		var is = kjs$getSelf().copy();

		if (is.getItem() == Items.ENCHANTED_BOOK) {
			EnchantedBookItem.addEnchantment(is, new EnchantmentInstance(enchantment, level));
		} else {
			is.enchant(enchantment, level);
		}

		return is;
	}

	default String kjs$getMod() {
		return Registries.getId(kjs$getSelf().getItem(), Registry.ITEM_REGISTRY).getNamespace();
	}

	default IngredientJS kjs$ignoreNBT() {
		return new IgnoreNBTIngredientJS(ItemStackJS.of(this));
	}

	default IngredientJS kjs$weakNBT() {
		return new WeakNBTIngredientJS(ItemStackJS.of(this));
	}

	default boolean kjs$areItemsEqual(ItemStack other) {
		return kjs$getSelf().getItem() == other.getItem();
	}

	default boolean kjs$isNBTEqual(ItemStack other) {
		if (kjs$getSelf().hasTag() == other.hasTag()) {
			var nbt = kjs$getSelf().getTag();
			var nbt2 = other.getTag();
			return Objects.equals(nbt, nbt2);
		}

		return false;
	}

	default float kjs$getHarvestSpeed(@Nullable BlockContainerJS block) {
		return kjs$getSelf().getDestroySpeed(block == null ? Blocks.AIR.defaultBlockState() : block.getBlockState());
	}

	default float kjs$getHarvestSpeed() {
		return kjs$getHarvestSpeed(null);
	}

	@Override
	default CompoundTag toNBT() {
		return kjs$getSelf().save(new CompoundTag());
	}

	default String kjs$getItemGroup() {
		var cat = kjs$getSelf().getItem().getItemCategory();
		return cat == null ? "" : cat.getRecipeFolderName();
	}

	default CompoundTag kjs$getTypeData() {
		return kjs$getSelf().getItem().getTypeDataKJS();
	}
}
