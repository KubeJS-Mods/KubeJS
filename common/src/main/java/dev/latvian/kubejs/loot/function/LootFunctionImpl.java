package dev.latvian.kubejs.loot.function;

import dev.latvian.kubejs.loot.LootTableUtils;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.HideFromJS;

public interface LootFunctionImpl extends JsonSerializable {
	@HideFromJS
	LootFunction handleNewFunctionImpl(LootFunction lootFunction);

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

	default LootFunction applyBonus(String enchantment) {
		LootFunction lootFunction = new LootFunction("minecraft:apply_bonus");
		lootFunction.put("enchantment", enchantment);
		lootFunction.put("formula", "minecraft:ore_drops");
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction applyBonus(String enchantment, int extra, float probability) {
		LootFunction lootFunction = new LootFunction("minecraft:apply_bonus");
		lootFunction.put("enchantment", enchantment);
		lootFunction.put("formula", "minecraft:binomial_with_bonus_count");
		MapJS parameters = new MapJS();
		parameters.put("extra", extra);
		parameters.put("probability", probability);
		lootFunction.put("parameters", parameters);
		return handleNewFunctionImpl(lootFunction);
	}

	default LootFunction applyBonus(String enchantment, int bonusMultiplier) {
		LootFunction lootFunction = new LootFunction("minecraft:apply_bonus");
		lootFunction.put("enchantment", enchantment);
		lootFunction.put("formula", "minecraft:uniform_bonus_count");
		MapJS parameters = new MapJS();
		parameters.put("bonusMultiplier", bonusMultiplier);
		lootFunction.put("parameters", parameters);
		return handleNewFunctionImpl(lootFunction);
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

	default LootFunction custom(MapJS function) {
		LootFunction lootFunction = LootFunction.of(function);
		return handleNewFunctionImpl(lootFunction);
	}
}
