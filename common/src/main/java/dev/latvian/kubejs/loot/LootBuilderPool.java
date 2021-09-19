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
	public RandomIntGenerator rolls = new ConstantIntValue(1);
	public final JsonArray conditions = new JsonArray();
	public final JsonArray entries = new JsonArray();

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		if (rolls instanceof JsonSerializableKJS) {
			json.add("rolls", ((JsonSerializableKJS) rolls).toJsonKJS());
		}

		if (conditions.size() > 0) {
			json.add("conditions", conditions);
		}

		if (entries.size() > 0) {
			json.add("entries", entries);
		}

		return json;
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

	public void addTag(String tag, boolean expand) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:tag");
		json.addProperty("name", tag);
		json.addProperty("expand", expand);
		addEntry(json);
	}

	// Block
	public void survivesExplosion() {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:survives_explosion");
		addCondition(json);
	}

	// Entity
	public void killer(JsonObject properties) {
		JsonObject json = new JsonObject();
		json.addProperty("condition", "minecraft:entity_properties");
		json.addProperty("entity", "killer");
		json.add("predicate", properties);
		addCondition(json);
	}
}
