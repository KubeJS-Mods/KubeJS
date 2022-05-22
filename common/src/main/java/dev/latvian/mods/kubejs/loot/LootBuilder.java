package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LootBuilder implements FunctionContainer, ConditionContainer {
	public String type = "minecraft:generic";
	public ResourceLocation customId = null;
	public JsonArray pools = new JsonArray();
	public JsonArray functions = new JsonArray();
	public JsonArray conditions = new JsonArray();

	public LootBuilder(@Nullable JsonElement prev) {
		if (prev instanceof JsonObject o) {

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
		var json = new JsonObject();
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
		var pool = new LootBuilderPool();
		p.accept(pool);
		pools.add(pool.toJson());
	}

	public void modifyPool(int index, Consumer<LootBuilderPool> p) {
		var pool = new LootBuilderPool(pools.get(index));
		p.accept((pool));
		pools.set(index, pool.toJson());
	}

	public LootBuilder addFunction(JsonObject o) {
		functions.add(o);
		return this;
	}

	public LootBuilder addCondition(JsonObject o) {
		conditions.add(o);
		return this;
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
