package dev.latvian.kubejs.loot.condition;

import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public interface LootConditionImpl extends JsonSerializable {
	@HideFromJS
	void handleNewConditionImpl(LootCondition condition);

	default void survivesExplosion() {
		BasicCondition condition = new BasicCondition("minecraft:survives_explosion");
		handleNewConditionImpl(condition);
	}

	default void randomChance(float chance) {
		BasicCondition condition = new BasicCondition("minecraft:random_chance");
		condition.put("chance", chance);
		handleNewConditionImpl(condition);
	}

	default void randomChanceWithLooting(float chance, float lootingMultiplier) {
		BasicCondition condition = new BasicCondition("minecraft:random_chance_with_looting");
		condition.put("chance", chance);
		condition.put("looting_multiplier", lootingMultiplier);
		handleNewConditionImpl(condition);
	}

	default void weatherCheck(MapJS data) {
		BasicCondition condition = new BasicCondition("minecraft:weather_check");
		condition.putByKey("raining", data);
		condition.putByKey("thundering", data);
		handleNewConditionImpl(condition);
	}

	default void matchTool(String item) {
		BasicCondition condition = new BasicCondition("minecraft:match_tool");
		MapJS predicate = new MapJS();
		predicate.put("item", item);
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void matchToolEnchantment(ListJS enchantments) {
		BasicCondition condition = new BasicCondition("minecraft:match_tool");
		MapJS predicate = new MapJS();
		predicate.put("enchantments", enchantments);
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void killedByEntity(String entity) {
		MapJS predicate = new MapJS();
		predicate.put("type", entity);
		entityProperties("killer", predicate);
	}

	default void entityProperties(String thisOrKiller, MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:entity_properties");
		condition.put("predicate", predicate);
		condition.put("entity", thisOrKiller);
		handleNewConditionImpl(condition);
	}

	default void isOnFire() {
		BasicCondition condition = new BasicCondition("minecraft:entity_properties");
		MapJS predicate = new MapJS();
		MapJS flags = new MapJS();
		flags.put("in_on_fire", true);
		predicate.put("flags", flags);
		condition.put("predicate", predicate);
		condition.put("entity", "this");
		handleNewConditionImpl(condition);
	}

	default void killedByPlayer() {
		BasicCondition condition = new BasicCondition("minecraft:killed_by_player");
		handleNewConditionImpl(condition);
	}

	default void blockState(String block, MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:block_state_property");
		condition.put("block", block);
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void location(MapJS predicate) {
		location(new MapJS(), predicate);
	}

	default void location(MapJS offset, MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:killed_by_player");
		condition.putByKey("offsetX", offset);
		condition.putByKey("offsetY", offset);
		condition.putByKey("offsetZ", offset);
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void biome(String biome) {
		MapJS predicate = new MapJS();
		predicate.put("biome", biome);
		location(predicate);
	}

	default void tableBonus(String enchantment, float[] chances) {
		BasicCondition condition = new BasicCondition("minecraft:table_bonus");
		condition.put("enchantment", enchantment);
		condition.put("chances", ListJS.of(chances));
		handleNewConditionImpl(condition);
	}

	default void damageSource(MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:damage_source_properties ");
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default InvertedCondition inverted() {
		InvertedCondition invertedCondition = new InvertedCondition();
		handleNewConditionImpl(invertedCondition);
		return invertedCondition;
	}


	default void alternative(Consumer<AlternativeCondition> consumer) {
		AlternativeCondition alternativeCondition = new AlternativeCondition();
		handleNewConditionImpl(alternativeCondition);
		consumer.accept(alternativeCondition);
	}

	default void custom(MapJS condition) {
		LootCondition lootCondition = LootCondition.of(condition);
		handleNewConditionImpl(lootCondition);
	}
}
