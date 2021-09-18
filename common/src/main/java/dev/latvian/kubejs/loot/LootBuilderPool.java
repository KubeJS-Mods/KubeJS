package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class LootBuilderPool {
	private RandomIntGenerator rolls = new ConstantIntValue(1);
	private final JsonArray conditions = new JsonArray();
	private final JsonArray entries = new JsonArray();

	public void toJson(LootEventJS<?> event, JsonArray array) {
		JsonObject json = new JsonObject();
		json.add("rolls", event.gsonConditions.toJsonTree(rolls));

		if (conditions.size() > 0) {
			json.add("conditions", conditions);
		}

		if (entries.size() > 0) {
			json.add("entries", entries);
		}

		array.add(json);
	}

	public void setRolls(int r) {
		rolls = new ConstantIntValue(r);
	}

	public void setUniformRolls(float min, float max) {
		rolls = new RandomValueBounds(min, max);
	}

	public void setBinomialRolls(int n, float p) {
		rolls = new BinomialDistributionGenerator(n, p);
	}

	public void addCondition(JsonObject o) {
		conditions.add(o);
	}

	public void addEntry(JsonObject o) {
		entries.add(o);
	}

	public void addItem(ItemStack item, int weight, @Nullable RandomIntGenerator count) {
		ResourceLocation id = KubeJSRegistries.items().getId(item.getItem());

		if (id == null || item.isEmpty()) {
			return;
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:item");
		json.addProperty("name", id.toString());

		if (weight >= 0) {
			json.addProperty("weight", weight);
		}

		JsonArray f = new JsonArray();

		if (count instanceof JsonSerializableKJS) {
			JsonObject o = new JsonObject();
			o.addProperty("function", "minecraft:set_count");
			o.add("count", ((JsonSerializableKJS) count).toJsonKJS());
			f.add(o);
		}

		if (item.getTag() != null) {
			JsonObject o = new JsonObject();
			o.addProperty("function", "minecraft:set_nbt");
			o.addProperty("tag", item.getTag().toString());
			f.add(o);
		}

		if (f.size() > 0) {
			json.add("functions", f);
		}

		addEntry(json);
	}

	public void addItem(ItemStack item, int weight) {
		addItem(item, weight, null);
	}

	public void addItem(ItemStack item) {
		addItem(item, -1, null);
	}
}
