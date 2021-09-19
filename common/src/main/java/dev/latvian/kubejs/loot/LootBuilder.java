package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LootBuilder {
	public String type = "minecraft:generic";
	public ResourceLocation customId = null;
	public JsonArray pools = new JsonArray();
	public JsonArray functions = new JsonArray();
	public JsonArray conditions = new JsonArray();

	public LootBuilder(@Nullable JsonElement prev) {
		if (prev instanceof JsonObject) {
			JsonObject o = (JsonObject) prev;

			if (o.has("pools")) {
				pools = o.get("pools").getAsJsonArray();
			}

			if (o.has("functions")) {
				functions = o.get("functions").getAsJsonArray();
			}

			if (o.has("conditions")) {
				conditions = o.get("conditions").getAsJsonArray();
			}
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", type);

		if (pools.size() > 0) {
			json.add("pools", pools);
		}

		if (functions.size() > 0) {
			json.add("functions", functions);
		}

		if (conditions.size() > 0) {
			json.add("conditions", conditions);
		}

		return json;
	}

	public void addPool(Consumer<LootBuilderPool> p) {
		LootBuilderPool pool = new LootBuilderPool();
		p.accept(pool);
		pools.add(pool.toJson());
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

	public void clearPools() {
		pools = new JsonArray();
	}

	public void clearFunctions() {
		functions = new JsonArray();
	}

	public void clearConditions() {
		conditions = new JsonArray();
	}
}
