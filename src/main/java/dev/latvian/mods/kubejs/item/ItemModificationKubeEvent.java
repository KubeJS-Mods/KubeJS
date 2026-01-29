package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.ItemComponentFunctions;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;

@Info("""
	Invoked after all items are registered to modify them.
	""")
public class ItemModificationKubeEvent implements KubeEvent {
	@Info("""
		Modifies items matching the given ingredient.
		
		**NOTE**: tag ingredients are not supported at this time.
		""")
	public void modify(ItemPredicate in, Consumer<ItemModifications> c) {
		in.kjs$getItemTypes().stream().map(ItemModifications::new).forEach(c);
	}

	@RemapPrefixForJS("kjs$")
	public record ItemModifications(Item item) implements ItemComponentFunctions {
		@HideFromJS
		public static final Reference2IntOpenHashMap<Item> BURN_TIME_OVERRIDES = new Reference2IntOpenHashMap<>();

		@Override
		public DataComponentMap kjs$getComponentMap() {
			return item.components();
		}

		@Override
		@HideFromJS
		public <T> ItemComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
			item.kjs$overrideComponent(type, value);
			return this;
		}

		public void setBurnTime(TickDuration i) {
			BURN_TIME_OVERRIDES.put(item, i.intTicks());
		}

		public void setCraftingRemainder(Item item) {
			this.item.kjs$setCraftingRemainder(item);
		}

		public void setTier(Consumer<MutableToolMaterial> builder) {
			var material = Util.make(new MutableToolMaterial(ToolMaterial.IRON), builder).toToolMaterial();

			var attackDamageBaseline = 0F;
			var attackSpeedBaseline = 0F;

			var existing = kjs$getAttributeModifiers();

			if (existing != null) {
				for (var entry : existing.modifiers()) {
					if (entry.matches(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_ID)) {
						attackDamageBaseline = (float) entry.modifier().amount();
					} else if (entry.matches(Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID)) {
						attackSpeedBaseline = (float) entry.modifier().amount();
					}
				}
			}

			kjs$setMaxDamage(material.durability());
			kjs$override(DataComponents.ENCHANTABLE, new Enchantable(material.enchantmentValue()));
			kjs$override(DataComponents.REPAIRABLE, new Repairable(
				BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM).getOrThrow(material.repairItems())
			));

			var tool = kjs$getComponentMap().get(DataComponents.TOOL);

			if (tool != null) {
				var minesEfficiently = kjs$inferMineableTag(tool);
				kjs$override(DataComponents.TOOL, MutableToolMaterial.createToolProperties(material, minesEfficiently));
				kjs$setAttributeModifiers(ItemAttributeModifiers.builder()
					.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double) (attackDamageBaseline + material.attackDamageBonus()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double) attackSpeedBaseline, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.build()
				);
				kjs$override(DataComponents.WEAPON, new Weapon(2, 0F));
			} else {
				kjs$override(DataComponents.TOOL, new Tool(
					List.of(
						Tool.Rule.minesAndDrops(HolderSet.direct(new Holder[]{Blocks.COBWEB.builtInRegistryHolder()}), 15.0F),
						Tool.Rule.overrideSpeed(BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK).getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
						Tool.Rule.overrideSpeed(BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK).getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F)
					),
					1.0F,
					2,
					false
				));
				kjs$setAttributeModifiers(ItemAttributeModifiers.builder()
					.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double) (attackDamageBaseline + material.attackDamageBonus()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double) attackSpeedBaseline, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.build()
				);
				kjs$override(DataComponents.WEAPON, new Weapon(1));
			}
		}

		private TagKey<Block> kjs$inferMineableTag(Tool tool) {
			for (var rule : tool.rules()) {
				var set = rule.blocks();

				if (set instanceof HolderSet.Named<Block> named) {
					var key = named.key();
					return TagKey.create(Registries.BLOCK, key.location());
				}
			}

			return BlockTags.MINEABLE_WITH_PICKAXE;
		}


		public void setNameKey(String key) {
			item.kjs$setNameKey(key);
		}

		public void disableRepair() {
			item.kjs$setCanRepair(false);
		}
	}
}
