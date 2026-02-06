package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface ItemComponentFunctions extends ComponentFunctions, AttributeModifierFunctions {
	default void kjs$setMaxStackSize(int size) {
		kjs$override(DataComponents.MAX_STACK_SIZE, size);
	}

	default void kjs$setMaxDamage(int maxDamage) {
		kjs$override(DataComponents.MAX_DAMAGE, maxDamage);
	}

	default void kjs$setDamage(int damage) {
		kjs$override(DataComponents.DAMAGE, damage);
	}

	default void kjs$setUnbreakable() {
		kjs$setUnit(DataComponents.UNBREAKABLE);
	}

	default void kjs$setUnbreakableWithTooltip() {
		kjs$setUnit(DataComponents.UNBREAKABLE);

		var td = kjs$get(DataComponents.TOOLTIP_DISPLAY);
		if (td == null) {
			td = TooltipDisplay.DEFAULT;
		}
		kjs$override(DataComponents.TOOLTIP_DISPLAY, td.withHidden(DataComponents.UNBREAKABLE, false));
	}

	default void kjs$setItemName(Component component) {
		kjs$override(DataComponents.ITEM_NAME, component);
	}

	default void kjs$setRepairCost(int repairCost) {
		kjs$override(DataComponents.REPAIR_COST, repairCost);
	}

	default void kjs$setFood(FoodProperties foodProperties) {
		kjs$override(DataComponents.FOOD, foodProperties);
	}

	default void kjs$setFood(int nutrition, float saturation) {
		kjs$setFood(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build());
	}

	default void kjs$addDamageResistance(TagKey<DamageType> types) {
		kjs$override(DataComponents.DAMAGE_RESISTANT, new DamageResistant(types));
	}

	default void kjs$setFireResistant(TagKey<DamageType> fireTypes) {
		kjs$addDamageResistance(fireTypes);
	}

	default void kjs$setTool(Tool tool) {
		kjs$override(DataComponents.TOOL, tool);
	}

	default void kjs$setMapItemColor(KubeColor color) {
		kjs$override(DataComponents.MAP_COLOR, new MapItemColor(color.kjs$getRGB()));
	}

	default void kjs$setChargedProjectiles(List<ItemStack> items) {
		kjs$override(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(items));
	}

	default void kjs$setBundleContents(List<ItemStack> items) {
		kjs$override(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
	}

	default void kjs$setBucketEntityData(CompoundTag tag) {
		kjs$override(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
	}


	default void kjs$setBlockEntityData(BlockEntityType<?> type, CompoundTag tag) {
		kjs$override(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(type, tag));
	}

	default void kjs$setInstrument(Holder<Instrument> instrument) {
		kjs$override(DataComponents.INSTRUMENT, new InstrumentComponent(instrument));
	}

	default void kjs$setFireworkExplosion(FireworkExplosion explosion) {
		kjs$override(DataComponents.FIREWORK_EXPLOSION, explosion);
	}

	default void kjs$setFireworks(Fireworks fireworks) {
		kjs$override(DataComponents.FIREWORKS, fireworks);
	}

	default void kjs$setNoteBlockSound(Identifier id) {
		kjs$override(DataComponents.NOTE_BLOCK_SOUND, id);
	}

	@Override
	default ItemAttributeModifiers kjs$getAttributeModifiers() {
		var mods = kjs$get(DataComponents.ATTRIBUTE_MODIFIERS);
		return mods == null ? new ItemAttributeModifiers(List.of()) : mods;
	}

	@Override
	default void kjs$setAttributeModifiers(ItemAttributeModifiers modifiers) {
		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
	}
}
