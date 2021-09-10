package dev.latvian.kubejs.loot.condition;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.NonnullByDefault;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

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

	default void matchTool(IngredientJS item) {
		BasicCondition condition = new BasicCondition("minecraft:match_tool");
		ItemPredicate itemPredicate = ItemPredicate.fromJson(item.toJson());
		MapJS predicate = MapJS.of(itemPredicate.serializeToJson());
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void matchToolEnchantment(@NonnullByDefault Enchantment enchantment) {
		MapJS levels = new MapJS();
		levels.put("min", 1);
		matchToolEnchantment(enchantment, levels);
	}

	default void matchToolEnchantment(@NonnullByDefault Enchantment enchantment, @NonnullByDefault MapJS levels) {
		BasicCondition condition = new BasicCondition("minecraft:match_tool");

		MapJS enchantmentMap = new MapJS();
		enchantmentMap.put("enchantment", KubeJSRegistries.enchantments().getId(enchantment));
		enchantmentMap.put("levels", levels);

		MapJS predicate = new MapJS();
		predicate.put("enchantments", ListJS.orSelf(enchantmentMap));
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

	default void isOnFlag(String thisOrKiller, String flag, boolean active) {
		MapJS predicate = new MapJS();
		MapJS flags = new MapJS();
		flags.put(flag, active);
		predicate.put("flags", flags);
		entityProperties(thisOrKiller, predicate);
	}

	default void isOnFire(boolean onFire) {
		isOnFlag("this", "is_on_fire", onFire);
	}

	default void killedByPlayer() {
		BasicCondition condition = new BasicCondition("minecraft:killed_by_player");
		handleNewConditionImpl(condition);
	}

	default void blockState(Block block, MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:block_state_property");
		condition.put("block", KubeJSRegistries.blocks().getId(block));
		condition.put("predicate", predicate);
		handleNewConditionImpl(condition);
	}

	default void location(MapJS predicate) {
		location(new MapJS(), predicate);
	}

	default void location(MapJS offset, MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:location_check");
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

	default void tableBonus(Enchantment enchantment, float[] chances) {
		BasicCondition condition = new BasicCondition("minecraft:table_bonus");
		condition.put("enchantment", KubeJSRegistries.enchantments().getId(enchantment));
		condition.put("chances", ListJS.of(chances));
		handleNewConditionImpl(condition);
	}

	default void damageSource(MapJS predicate) {
		BasicCondition condition = new BasicCondition("minecraft:damage_source_properties");
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

	default void customCondition(Object condition) {
		LootCondition lootCondition = LootCondition.of(condition);
		if (lootCondition == null) {
			throw new IllegalArgumentException(String.format("Condition '%s' could not be created. Wrong format!", condition));
		}

		handleNewConditionImpl(lootCondition);
	}
}
