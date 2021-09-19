package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.ConsoleJS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public abstract class LootBuilder {
	public final List<LootBuilderPool> pools = new ArrayList<>();
	public final List<JsonObject> functions = new ArrayList<>();
	public final List<JsonObject> conditions = new ArrayList<>();

	public abstract String getType();

	public JsonObject toJson(LootEventJS event) {
		JsonObject json = new JsonObject();
		json.addProperty("type", getType());

		if (!pools.isEmpty()) {
			JsonArray p = new JsonArray();

			for (LootBuilderPool pool : pools) {
				pool.toJson(event, p);
			}

			json.add("pools", p);
		}

		if (!functions.isEmpty()) {
			JsonArray f = new JsonArray();

			for (JsonObject o : functions) {
				f.add(o);
			}

			json.add("functions", f);
		}

		if (!conditions.isEmpty()) {
			JsonArray f = new JsonArray();

			for (JsonObject o : conditions) {
				f.add(o);
			}

			json.add("conditions", f);
		}

		return json;
	}

	public void addPool(Consumer<LootBuilderPool> p) {
		LootBuilderPool pool = new LootBuilderPool();
		p.accept(pool);
		pools.add(pool);
	}

	public void pool(Consumer<LootBuilderPool> p) {
		addPool(p);
		ConsoleJS.SERVER.setLineNumber(true);
		ConsoleJS.SERVER.warn("This method is no longer supported! Use table.addPool(pool => {...})");
		ConsoleJS.SERVER.setLineNumber(false);
	}

	public void addFunction(JsonObject o) {
		functions.add(o);
	}

	public void addCondition(JsonObject o) {
		conditions.add(o);
	}
}
