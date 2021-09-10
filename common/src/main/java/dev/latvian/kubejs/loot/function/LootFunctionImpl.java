package dev.latvian.kubejs.loot.function;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.loot.LootTableUtils;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public interface LootFunctionImpl extends JsonSerializable {
	@HideFromJS
	LootFunction handleNewFunctionImpl(LootFunction lootFunction);

	default LootFunction copyState(Block block, String[] properties) {
		LootFunction lootFunction = new LootFunction("minecraft:copy_state");
		lootFunction.put("block", KubeJSRegistries.blocks().getId(block));
		lootFunction.put("properties", ListJS.of(properties));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction copyNbt(String source, ListJS ops) {
		LootFunction lootFunction = new LootFunction("minecraft:copy_nbt");
		lootFunction.put("source", source);
		lootFunction.put("ops", ops.copy());
		return handleNewFunctionImpl(lootFunction);
	}

	@SuppressWarnings("UnusedReturnValue")
	default LootFunction setNbt(MapJS nbt) {
		if (nbt.isEmpty()) {
			return null;
		}

		LootFunction lootFunction = new LootFunction("minecraft:set_nbt");
		lootFunction.put("tag", nbt.copy());
		return handleNewFunctionImpl(lootFunction);
	}

	@SuppressWarnings("UnusedReturnValue")
	default LootFunction setCount(int value) {
		LootFunction lootFunction = new LootFunction("minecraft:set_count");
		lootFunction.put("count", value);
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction setUniformCount(float min, float max) {
		LootFunction lootFunction = new LootFunction("minecraft:set_count");
		lootFunction.put("count", LootTableUtils.createUniformNumberProvider(min, max, true));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction setBinomialCount(int n, float probability) {
		LootFunction lootFunction = new LootFunction("minecraft:set_count");
		lootFunction.put("count", LootTableUtils.createBinomialNumberProvider(n, probability));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction explosionDecay() {
		LootFunction lootFunction = new LootFunction("minecraft:explosion_decay");
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction enchantRandomly() {
		LootFunction lootFunction = new LootFunction("minecraft:enchant_randomly");
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction furnaceSmelt() {
		LootFunction lootFunction = new LootFunction("minecraft:furnace_smelt");
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction enchantWithLevels(boolean treasure, int level) {
		LootFunction lootFunction = new LootFunction("minecraft:enchant_with_levels");
		lootFunction.put("treasure", treasure);
		lootFunction.put("levels", level);
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction enchantWithLevels(boolean treasure, float min, float max) {
		LootFunction lootFunction = new LootFunction("minecraft:enchant_with_levels");
		lootFunction.put("treasure", treasure);
		lootFunction.put("levels", LootTableUtils.createUniformNumberProvider(min, max, true));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction damage(float min, float max) {
		LootFunction lootFunction = new LootFunction("minecraft:set_damage");
		MapJS values = new MapJS();
		values.put("min", min);
		values.put("max", max);
		lootFunction.put("damage", values);
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction lootingEnchant(float min, float max) {
		LootFunction lootFunction = new LootFunction("minecraft:looting_enchant");
		lootFunction.put("count", LootTableUtils.createUniformNumberProvider(min, max, false));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction limitCount(Float min, Float max) {
		LootFunction lootFunction = new LootFunction("minecraft:limit_count");
		lootFunction.put("limit", LootTableUtils.createUniformNumberProvider(min, max, false));
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction applyBonus(Enchantment enchantment, String formula) {
		return applyBonus(enchantment, formula, null);
	}

	default LootFunction applyBonus(Enchantment enchantment, String formula, MapJS parameters) {
		LootFunction lootFunction = new LootFunction("minecraft:apply_bonus");
		lootFunction.put("enchantment", KubeJSRegistries.enchantments().getId(enchantment));
		lootFunction.put("formula", formula);
		if (parameters != null && !parameters.isEmpty()) {
			lootFunction.put("parameters", parameters);
		}

		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction applyOreDropsBonus(Enchantment enchantment) {
		return applyBonus(enchantment, "minecraft:ore_drops");
	}

	default LootFunction applyBinomialBonus(Enchantment enchantment, int extra, float probability) {
		MapJS parameters = new MapJS();
		parameters.put("extra", extra);
		parameters.put("probability", probability);
		return applyBonus(enchantment, "minecraft:binomial_with_bonus_count", parameters);
	}

	default LootFunction applyMultiplierBonus(Enchantment enchantment, int bonusMultiplier) {
		MapJS parameters = new MapJS();
		parameters.put("bonusMultiplier", bonusMultiplier);
		return applyBonus(enchantment, "minecraft:uniform_bonus_count", parameters);
	}

	default LootFunction explorationMap(MapJS data) {
		LootFunction lootFunction = new LootFunction("minecraft:exploration_map");
		lootFunction.putByKey("destination", data);
		lootFunction.putByKey("decoration", data);
		lootFunction.putByKey("zoom", data);
		lootFunction.putByKey("search_radius", data);
		lootFunction.putByKey("skip_existing_chunks", data);
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction customFunction(MapJS function) {
		LootFunction lootFunction = LootFunction.of(function);
		return handleNewFunctionImpl(lootFunction);
	}
}
