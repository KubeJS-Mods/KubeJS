package dev.latvian.mods.kubejs.block.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * @author cofl
 */
public class LeavesBlockBuilder extends BlockBuilder {
	public LeavesBlockBuilder(ResourceLocation i){
		super(i);
		material = MaterialListJS.INSTANCE.get("leaves");
		hardness = 0.2f;
		resistance = 0.2f;
		notSolid = true;
		noValidSpawns = true;
		suffocating = false;
		viewBlocking = false;
		renderType = "cutout_mipped";
		redstoneConductor = false;
		lootTable = loot -> loot.addPool(pool -> {
			var condition = new JsonObject();
			condition.addProperty("condition", "minecraft:alternative");
			var alternatives = new JsonArray();
			{
				var shearsCondition = new JsonObject();
				{
					shearsCondition.addProperty("condition", "minecraft:match_tool");

					var shearsList = new JsonArray();
					shearsList.add("minecraft:shears");

					var shearsPredicate = new JsonObject();
					shearsPredicate.add("items", shearsList);
					shearsCondition.add("predicate", shearsPredicate);
				}
				alternatives.add(shearsCondition);
				var silkTouchCondition = new JsonObject();
				{
					silkTouchCondition.addProperty("condition", "minecraft:match_tool");

					var silkTouchLevels = new JsonObject();
					silkTouchLevels.addProperty("min", 1);

					var silkTouchEnchantment = new JsonObject();
					silkTouchEnchantment.addProperty("enchantment", "minecraft:silk_touch");
					silkTouchEnchantment.add("levels", silkTouchLevels);

					var silkTouchList = new JsonArray();
					silkTouchList.add(silkTouchEnchantment);

					var silkTouchPredicate = new JsonObject();
					silkTouchPredicate.add("enchantments", silkTouchList);
					silkTouchCondition.add("predicate", silkTouchPredicate);
				}
				alternatives.add(silkTouchCondition);
			}
			condition.add("terms", alternatives);

			pool.rolls = ConstantValue.exactly(1.0f);
			pool.bonusRolls = ConstantValue.exactly(0.0f);
			pool.addItem(new ItemStack(get()))
					.addCondition(condition);
		});
	}

	@Override
	public Block createObject(){ return new LeavesBlock(createProperties()); }
}
