package dev.latvian.kubejs.loot;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.entry.LootEntry;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LootTableBuilder implements JsonSerializable {
	public final String type;

	public final LootFunctionList functions = new LootFunctionList();
	public final List<LootPool> pools = new ArrayList<>();

	public LootTableBuilder(String type) {
		this.type = type;
	}

	@HideFromJS
	public LootTableBuilder(JsonObject object) {
		JsonObject copiedObject = (JsonObject) JsonUtilsJS.copy(object);

		this.type = copiedObject.get("type").getAsString();

		functions.fill(copiedObject.getAsJsonArray("functions"));

		JsonArray pools = copiedObject.getAsJsonArray("pools");
		if(pools != null) {
			pools.forEach(pool -> {
				JsonObject poolAsObject = pool.getAsJsonObject();
				getPools().add(new LootPool(poolAsObject));
			});
		}
	}

	public List<LootPool> getPools() {
		return pools;
	}

	public void pool(Consumer<LootPool> consumer) {
		LootPool p = new LootPool();
		consumer.accept(p);
		getPools().add(p);
	}

	public JsonObject toJson() {
		JsonObject result = new JsonObject();

		result.addProperty("type", type);

		functions.fillJson(result);

		if(!getPools().isEmpty()) {
			JsonArray poolsArray = new JsonArray();
			getPools().forEach(lootPool -> {
				poolsArray.add(lootPool.toJson());
			});
			result.add("pools", poolsArray);
		}

		return result;
	}

	/**
	 * Most loot tables contains just one pool with one entry which holds
	 * all information through drop, conditions & functions.
	 * @return the first accessible entry in the loot table
	 */
	@Nullable
	public LootEntry getFirstEntry() {
		for (LootPool pool : getPools()) {
			for (LootEntry entry : pool.entries) {
				return entry;
			}
		}

		return null;
	}

	public void clear() {
		functions.clear();
		pools.clear();
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public String toPrettyString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(toJson());
	}
}
