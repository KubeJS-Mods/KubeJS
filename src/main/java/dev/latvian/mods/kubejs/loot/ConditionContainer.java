package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Map;

public interface ConditionContainer {
	ConditionContainer addCondition(JsonObject o);

	default ConditionContainer randomChance(double chance) {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:random_chance");
		json.addProperty("chance", chance);
		return addCondition(json);
	}

	default ConditionContainer randomChanceWithLooting(double chance, double multiplier) {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:random_chance_with_looting");
		json.addProperty("chance", chance);
		json.addProperty("looting_multiplier", multiplier);
		return addCondition(json);
	}

	// Block
	default ConditionContainer survivesExplosion() {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:survives_explosion");
		return addCondition(json);
	}

	// Entity
	default ConditionContainer entityProperties(LootContext.EntityTarget entity, JsonObject properties) {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:entity_properties");
		json.addProperty("entity", entity.name);
		json.add("predicate", properties);
		return addCondition(json);
	}

	// Entity
	default ConditionContainer killedByPlayer() {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:killed_by_player");
		return addCondition(json);
	}

	// Entity
	default ConditionContainer entityScores(LootContext.EntityTarget entity, Map<String, Object> scores) {
		var json = new JsonObject();
		json.addProperty("condition", "minecraft:entity_scores");
		json.addProperty("entity", entity.name);

		var s = new JsonObject();

		for (var entry : scores.entrySet()) {
			s.add(entry.getKey(), KubeJSCodecs.numberProviderJson(UtilsJS.numberProviderOf(entry.getValue())));
		}

		json.add("scores", s);
		return addCondition(json);
	}
}
