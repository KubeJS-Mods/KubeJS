package dev.latvian.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.UtilsJS;
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
public class LootBuilderPool implements FunctionContainer, ConditionContainer {
	public RandomIntGenerator rolls = new ConstantIntValue(1);
	public RandomIntGenerator bonusRolls = null;
	public final JsonArray conditions = new JsonArray();
	public final JsonArray functions = new JsonArray();
	public final JsonArray entries = new JsonArray();

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("rolls", UtilsJS.randomIntGeneratorJson(rolls));

		if (bonusRolls != null) {
			json.add("bonus_rolls", UtilsJS.randomIntGeneratorJson(bonusRolls));
		}

		if (conditions.size() > 0) {
			json.add("conditions", conditions);
		}

		if (functions.size() > 0) {
			json.add("functions", functions);
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

	@Override
	public LootBuilderPool addFunction(JsonObject o) {
		functions.add(o);
		return this;
	}

	@Override
	public LootBuilderPool addCondition(JsonObject o) {
		conditions.add(o);
		return this;
	}

	public LootTableEntry addEntry(JsonObject json) {
		entries.add(json);
		return new LootTableEntry(json);
	}

	public LootTableEntry addEmpty(int weight) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:empty");
		return addEntry(json).weight(weight);
	}

	public LootTableEntry addLootTable(ResourceLocation table) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:loot_table");
		json.addProperty("name", table.toString());
		return addEntry(json);
	}

	public LootTableEntry addItem(ItemStack item, int weight, @Nullable RandomIntGenerator count) {
		ResourceLocation id = KubeJSRegistries.items().getId(item.getItem());

		if (id == null || item.isEmpty()) {
			return new LootTableEntry(new JsonObject());
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:item");
		json.addProperty("name", id.toString());

		LootTableEntry entry = addEntry(json);

		if (weight >= 0) {
			entry.weight(weight);
		}

		if (count == null && item.getCount() > 1) {
			count = ConstantIntValue.exactly(item.getCount());
		}

		if (count != null) {
			entry.count(count);
		}

		if (item.getTag() != null) {
			entry.nbt(item.getTag());
		}

		return entry;
	}

	public LootTableEntry addItem(ItemStack item, int weight) {
		return addItem(item, weight, null);
	}

	public LootTableEntry addItem(ItemStack item) {
		return addItem(item, -1, null);
	}

	public LootTableEntry addTag(String tag, boolean expand) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:tag");
		json.addProperty("name", tag);
		json.addProperty("expand", expand);
		return addEntry(json);
	}
}
