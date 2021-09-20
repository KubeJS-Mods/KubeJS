package dev.latvian.kubejs.loot;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.EntityTargetKJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Map;

public interface ConditionContainer {
	ConditionContainer addCondition(JsonObject o);

	default ConditionContainer randomChance(double chance) {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:random_chance");
		json.addProperty("chance", chance);
		return addCondition(json);
	}

	default ConditionContainer randomChanceWithLooting(double chance, double multiplier) {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:random_chance_with_looting");
		json.addProperty("chance", chance);
		json.addProperty("looting_multiplier", multiplier);
		return addCondition(json);
	}

	// Block
	default ConditionContainer survivesExplosion() {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:survives_explosion");
		return addCondition(json);
	}

	// Entity
	default ConditionContainer entityProperties(LootContext.EntityTarget entity, JsonObject properties) {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:entity_properties");
		json.addProperty("entity", EntityTargetKJS.getNameKJS(entity));
		json.add("predicate", properties);
		return addCondition(json);
	}

	// Entity
	default ConditionContainer killedByPlayer() {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:killed_by_player");
		return addCondition(json);
	}

	// Entity
	default ConditionContainer entityScores(LootContext.EntityTarget entity, Map<String, Object> scores) {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:entity_scores");
		json.addProperty("entity", EntityTargetKJS.getNameKJS(entity));

		JsonObject s = new JsonObject();

		for (Map.Entry<String, Object> entry : scores.entrySet()) {
			s.add(entry.getKey(), UtilsJS.randomIntGeneratorJson(UtilsJS.randomIntGeneratorOf(entry.getValue())));
		}

		json.add("scores", s);
		return addCondition(json);
	}
}
