package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LootTableEntry implements FunctionContainer, ConditionContainer {
	public final JsonObject json;

	public LootTableEntry(JsonObject o) {
		json = o;
	}

	public LootTableEntry weight(int weight) {
		json.addProperty("weight", weight);
		return this;
	}

	public LootTableEntry quality(int quality) {
		json.addProperty("quality", quality);
		return this;
	}

	@Override
	public LootTableEntry addFunction(JsonObject o) {
		var a = (JsonArray) json.get("functions");

		if (a == null) {
			a = new JsonArray();
			json.add("functions", a);
		}

		a.add(o);
		return this;
	}

	@Override
	public LootTableEntry addCondition(JsonObject o) {
		var a = (JsonArray) json.get("conditions");

		if (a == null) {
			a = new JsonArray();
			json.add("conditions", a);
		}

		a.add(o);
		return this;
	}
}
