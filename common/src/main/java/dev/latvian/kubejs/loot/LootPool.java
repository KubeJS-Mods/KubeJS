package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.loot.condition.LootCondition;
import dev.latvian.kubejs.loot.condition.LootConditionImpl;
import dev.latvian.kubejs.loot.condition.LootConditionList;
import dev.latvian.kubejs.loot.entry.AbstractLootEntry;
import dev.latvian.kubejs.loot.entry.LootEntryList;
import dev.latvian.kubejs.loot.function.LootFunction;
import dev.latvian.kubejs.loot.function.LootFunctionImpl;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.kubejs.util.CustomDataOwner;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class LootPool implements CustomDataOwner, JsonSerializable, LootConditionImpl, LootFunctionImpl {
	private JsonElement rolls = new JsonObject();
	private final JsonObject additionalData = new JsonObject();
	public final LootConditionList conditions = new LootConditionList();
	public final LootFunctionList functions = new LootFunctionList();
	public final LootEntryList entries = new LootEntryList("entries");

	public LootPool() {
		setRolls(1);
	}

	@HideFromJS
	public LootPool(JsonObject object) {
		JsonObject copiedJsonPool = (JsonObject) JsonUtilsJS.copy(object);

		conditions.addAll((JsonArray) JsonUtilsJS.extract("conditions", copiedJsonPool));
		functions.addAll((JsonArray) JsonUtilsJS.extract("functions", copiedJsonPool));

		rolls = JsonUtilsJS.extract("rolls", copiedJsonPool);

		JsonElement entriesArray = JsonUtilsJS.extract("entries", copiedJsonPool);
		if (entriesArray instanceof JsonArray) {
			((JsonArray) entriesArray).forEach(entry -> {
				JsonObject entryAsObject = entry.getAsJsonObject();
				entries.add(AbstractLootEntry.of(entryAsObject));
			});
		}

		setCustomData(copiedJsonPool);
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

	public JsonObject toJson() {
		JsonObject object = new JsonObject();

		if (rolls != null) {
			object.add("rolls", rolls);
		}

		functions.serializeInto(object);
		conditions.serializeInto(object);
		entries.serializeInto(object);

		serializeCustomData(object);
		return object;
	}

	public void modify(Consumer<LootPool> consumer) {
		consumer.accept(this);
	}

	public AbstractLootEntry addEntry(Object o) {
		AbstractLootEntry entry = AbstractLootEntry.of(o);
		entries.add(entry);
		return entry;
	}

	public void addEntry(Object o, Consumer<AbstractLootEntry> action) {
		AbstractLootEntry entry = AbstractLootEntry.of(o);
		entries.add(entry);
		action.accept(entry);
	}

	@Override
	@HideFromJS
	public JsonObject getCustomData() {
		return additionalData;
	}

	@Override
	@HideFromJS
	public void handleNewConditionImpl(LootCondition condition) {
		conditions.handleNewConditionImpl(condition);
	}

	@Override
	@HideFromJS
	public LootFunction handleNewFunctionImpl(LootFunction lootFunction) {
		return functions.handleNewFunctionImpl(lootFunction);
	}
}
