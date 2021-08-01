package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.loot.condition.LootConditionList;
import dev.latvian.kubejs.loot.entry.LootEntry;
import dev.latvian.kubejs.loot.entry.LootEntryList;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class LootPool {
	private JsonElement rolls = new JsonObject();
	public final LootConditionList conditions = new LootConditionList();
	public final LootFunctionList functions = new LootFunctionList();
	public final LootEntryList entries = new LootEntryList();

	public LootPool() {
		setRolls(1);
	}

	@HideFromJS
	public LootPool(JsonObject object) {
		conditions.fill(object.getAsJsonArray("conditions"));
		functions.fill(object.getAsJsonArray("functions"));

		rolls = object.get("rolls");

		JsonElement entriesArray = object.get("entries");
		if (entriesArray instanceof JsonArray) {
			((JsonArray) entriesArray).forEach(entry -> {
				JsonObject entryAsObject = entry.getAsJsonObject();
				entries.add(new LootEntry(entryAsObject));
			});
		}
	}

	public void setRolls(int r) {
		rolls = new JsonPrimitive(r);
	}

	public void setUniformRolls(float min, float max) {
		rolls = LootTableUtils.createUniformNumberProvider(min, max, true).toJson();
	}

	public void setBinomialRolls(int n, float p) {
		rolls = LootTableUtils.createBinomialNumberProvider(n, p).toJson();
	}

	public JsonElement toJson() {
		JsonObject object = new JsonObject();

		if (rolls != null) {
			object.add("rolls", rolls);
		}

		functions.fillJson(object);
		conditions.fillJson(object);

		if (!entries.isEmpty()) {
			object.add("entries", entries.toJson());
		}

		return object;
	}

	public void modify(Consumer<LootPool> consumer) {
		consumer.accept(this);
	}

	public LootEntry addEntry(IngredientJS ingredientJS) {
		LootEntry entry = new LootEntry(ingredientJS);
		entries.add(entry);
		return entry;
	}

	public LootEntry addLootingEntry(IngredientJS ingredientJS, float minCount, float maxCount) {
		LootEntry entry = addEntry(ingredientJS.withCount(1));
		entry.functions.setUniformCount(minCount, maxCount);
		return entry;
	}

	public LootEntry addLootingEntry(IngredientJS ingredientJS, float minCount, float maxCount, float minLooting, float maxLooting) {
		LootEntry entry = addLootingEntry(ingredientJS, minCount, maxCount);
		entry.functions.lootingEnchant(minLooting, maxLooting);
		return entry;
	}
}
