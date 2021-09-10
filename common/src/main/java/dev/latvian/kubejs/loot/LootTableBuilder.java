package dev.latvian.kubejs.loot;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.function.LootFunction;
import dev.latvian.kubejs.loot.function.LootFunctionImpl;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.kubejs.util.CustomDataOwner;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LootTableBuilder implements JsonSerializable, CustomDataOwner, LootFunctionImpl {
	public String type;

	public final LootFunctionList functions = new LootFunctionList();
	public final List<LootPool> pools = new ArrayList<>();
	private final JsonObject additionalData = new JsonObject();

	public LootTableBuilder() {
	}

	public LootTableBuilder(String type) {
		this.type = type;
	}

	@HideFromJS
	public LootTableBuilder(JsonObject object) {
		JsonObject copiedJsonTable = (JsonObject) JsonUtilsJS.copy(object);

		JsonElement rawType = JsonUtilsJS.extract("type", copiedJsonTable);
		this.type = rawType != null ? rawType.getAsString() : null;

		functions.addAll((JsonArray) JsonUtilsJS.extract("functions", copiedJsonTable));

		JsonArray pools = (JsonArray) JsonUtilsJS.extract("pools", copiedJsonTable);
		if (pools != null) {
			pools.forEach(pool -> {
				JsonObject poolAsObject = pool.getAsJsonObject();
				getPools().add(new LootPool(poolAsObject));
			});
		}

		setCustomData(copiedJsonTable);
	}

	public List<LootPool> getPools() {
		return pools;
	}

	public void pool(Consumer<LootPool> consumer) {
		LootPool p = new LootPool();
		consumer.accept(p);
		getPools().add(p);
	}

	public String getType() {
		return type == null ? "" : type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JsonObject toJson() {
		JsonObject result = new JsonObject();

		if (type != null) {
			result.addProperty("type", type);
		}

		functions.serializeInto(result);

		if (!getPools().isEmpty()) {
			JsonArray poolsArray = new JsonArray();
			getPools().forEach(lootPool -> {
				poolsArray.add(lootPool.toJson());
			});
			result.add("pools", poolsArray);
		}

		serializeCustomData(result);
		return result;
	}

	public void clear() {
		functions.clear();
		pools.clear();
		clearCustomData();
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public String toPrettyString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
	}

	@Override
	public JsonObject getCustomData() {
		return additionalData;
	}

	public void merge(LootTableBuilder builder) {
		builder.pools.forEach(lootPool -> {
			pools.add(new LootPool(lootPool.toJson()));
		});
	}

	@Override
	@HideFromJS
	public LootFunction handleNewFunctionImpl(LootFunction lootFunction) {
		return functions.handleNewFunctionImpl(lootFunction);
	}
}
