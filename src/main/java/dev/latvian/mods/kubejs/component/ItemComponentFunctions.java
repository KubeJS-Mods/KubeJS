package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface ItemComponentFunctions extends ComponentFunctions, AttributeModifierFunctions {
	@HideFromJS
	default <T> ItemComponentFunctions kjs$overrideItem(DataComponentType<T> type, @Nullable T value) {
		kjs$override(type, value);
		return this;
	}

	default ItemComponentFunctions kjs$setMaxStackSize(int size) {
		return kjs$overrideItem(DataComponents.MAX_STACK_SIZE, size);
	}

	default ItemComponentFunctions kjs$setMaxDamage(int maxDamage) {
		return kjs$overrideItem(DataComponents.MAX_DAMAGE, maxDamage);
	}

	default ItemComponentFunctions kjs$setDamage(int damage) {
		return kjs$overrideItem(DataComponents.DAMAGE, damage);
	}

	default ItemComponentFunctions kjs$setUnbreakable() {
		return kjs$overrideItem(DataComponents.UNBREAKABLE, new Unbreakable(false));
	}

	default ItemComponentFunctions kjs$setUnbreakableWithTooltip() {
		return kjs$overrideItem(DataComponents.UNBREAKABLE, new Unbreakable(true));
	}

	default ItemComponentFunctions kjs$setItemName(Component component) {
		return kjs$overrideItem(DataComponents.ITEM_NAME, component);
	}

	default ItemComponentFunctions kjs$setRepairCost(int repairCost) {
		return kjs$overrideItem(DataComponents.REPAIR_COST, repairCost);
	}

	default ItemComponentFunctions kjs$setFood(FoodProperties foodProperties) {
		return kjs$overrideItem(DataComponents.FOOD, foodProperties);
	}

	default ItemComponentFunctions kjs$setFood(int nutrition, float saturation) {
		return kjs$setFood(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build());
	}

	default ItemComponentFunctions kjs$setFireResistant() {
		kjs$setUnit(DataComponents.FIRE_RESISTANT);
		return this;
	}

	default ItemComponentFunctions kjs$setTool(Tool tool) {
		return kjs$overrideItem(DataComponents.TOOL, tool);
	}

	default ItemComponentFunctions kjs$setMapItemColor(KubeColor color) {
		return kjs$overrideItem(DataComponents.MAP_COLOR, new MapItemColor(color.kjs$getRGB()));
	}

	default ItemComponentFunctions kjs$setChargedProjectiles(List<ItemStack> items) {
		return kjs$overrideItem(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(items));
	}

	default ItemComponentFunctions kjs$setBundleContents(List<ItemStack> items) {
		return kjs$overrideItem(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
	}

	default ItemComponentFunctions kjs$setBucketEntityData(CompoundTag tag) {
		return kjs$overrideItem(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
	}

	default ItemComponentFunctions kjs$setBlockEntityData(CompoundTag tag) {
		return kjs$overrideItem(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
	}

	default ItemComponentFunctions kjs$setInstrument(Holder<Instrument> instrument) {
		return kjs$overrideItem(DataComponents.INSTRUMENT, instrument);
	}

	default ItemComponentFunctions kjs$setFireworkExplosion(FireworkExplosion explosion) {
		return kjs$overrideItem(DataComponents.FIREWORK_EXPLOSION, explosion);
	}

	default ItemComponentFunctions kjs$setFireworks(Fireworks fireworks) {
		return kjs$overrideItem(DataComponents.FIREWORKS, fireworks);
	}

	default ItemComponentFunctions kjs$setNoteBlockSound(ResourceLocation id) {
		return kjs$overrideItem(DataComponents.NOTE_BLOCK_SOUND, id);
	}

	@Override
	default ItemAttributeModifiers kjs$getAttributeModifiers() {
		var mods = kjs$get(DataComponents.ATTRIBUTE_MODIFIERS);
		return mods == null ? new ItemAttributeModifiers(List.of(), true) : mods;
	}

	@Override
	default ItemComponentFunctions kjs$setAttributeModifiers(ItemAttributeModifiers modifiers) {
		return kjs$overrideItem(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
	}
}
