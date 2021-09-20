package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ConditionalFunction implements FunctionContainer, ConditionContainer {
	public JsonObject function = null;
	public JsonArray conditions = new JsonArray();

	@Override
	public ConditionalFunction addFunction(JsonObject o) {
		function = o;
		return this;
	}

	@Override
	public ConditionalFunction addCondition(JsonObject o) {
		conditions.add(o);
		return this;
	}
}
