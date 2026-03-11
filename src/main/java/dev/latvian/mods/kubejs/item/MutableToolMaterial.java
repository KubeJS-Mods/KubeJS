package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class MutableToolMaterial {
	public final ToolMaterial parent;
	private TagKey<Block> incorrectBlocksForDrops;
	private int durability;
	private float speed;
	private float attackDamageBonus;
	private int enchantmentValue;
	private TagKey<Item> repairItems;

	public MutableToolMaterial(ToolMaterial p) {
		parent = p;
		incorrectBlocksForDrops = p.incorrectBlocksForDrops();
		durability = p.durability();
		speed = p.speed();
		attackDamageBonus = p.attackDamageBonus();
		enchantmentValue = p.enchantmentValue();
		repairItems = p.repairItems();
	}

	public ToolMaterial toToolMaterial() {
		return new ToolMaterial(incorrectBlocksForDrops, durability, speed, attackDamageBonus, enchantmentValue, repairItems);
	}

	@RemapForJS("getDurability")
	public int getDurability() {
		return durability;
	}

	public void setDurability(int v) {
		durability = v;
	}

	@RemapForJS("getSpeed")
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float v) {
		speed = v;
	}

	@RemapForJS("getAttackDamageBonus")
	public float getAttackDamageBonus() {
		return attackDamageBonus;
	}

	public void setAttackDamageBonus(float v) {
		attackDamageBonus = v;
	}

	@RemapForJS("getEnchantmentValue")
	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	public void setEnchantmentValue(int v) {
		enchantmentValue = v;
	}

	public void setIncorrectBlocksForDropsTag(Identifier tag) {
		incorrectBlocksForDrops = BlockTags.create(tag);
	}

	public Identifier getIncorrectBlocksForDropsTag() {
		return incorrectBlocksForDrops.location();
	}

	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlocksForDrops;
	}

	public void setRepairItemsTag(Identifier tag) {
		repairItems = ItemTags.create(tag);
	}

	public Identifier getRepairItemsTag() {
		return repairItems.location();
	}

	public TagKey<Item> getRepairItems() {
		return repairItems;
	}

	public static Tool createToolProperties(ToolMaterial material, TagKey<Block> minesEfficiently) {
		HolderGetter<Block> lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);

		return new Tool(
			List.of(
				Tool.Rule.deniesDrops(lookup.getOrThrow(material.incorrectBlocksForDrops())),
				Tool.Rule.minesAndDrops(lookup.getOrThrow(minesEfficiently), material.speed())
			),
			1.0F,
			1,
			true
		);
	}
}
