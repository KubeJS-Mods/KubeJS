package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class LootBuilderPool implements FunctionContainer, ConditionContainer {
	public NumberProvider rolls = ConstantValue.exactly(1);
	public NumberProvider bonusRolls = null;
	public JsonArray conditions = new JsonArray();
	public JsonArray functions = new JsonArray();
	public JsonArray entries = new JsonArray();

	public LootBuilderPool() {}

	public LootBuilderPool(JsonElement existing) {
		if (existing instanceof JsonObject o) {
			if (o.has("conditions")) {
				conditions = o.get("conditions").getAsJsonArray();
			}

			if (o.has("functions")) {
				functions = o.get("functions").getAsJsonArray();
			}

			if (o.has("entries")) {
				entries = o.get("entries").getAsJsonArray();
			}
		}
	}

	public JsonObject toJson() {
		var json = new JsonObject();

		json.add("rolls", UtilsJS.numberProviderJson(rolls));

		if (bonusRolls != null) {
			json.add("bonus_rolls", UtilsJS.numberProviderJson(bonusRolls));
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
		rolls = UniformGenerator.between(min, max);
	}

	public void setBinomialRolls(int n, float p) {
		rolls = BinomialDistributionGenerator.binomial(n, p);
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

	public boolean removeItem(IngredientJS ing, boolean strict) {
		var index  = -1;
		for (var i = 0;i < entries.size();i++) {
			var entry = entries.get(i);
			if (entry instanceof JsonObject o) {
				if ("minecraft:item".equals(o.get("type").getAsString()) || !strict && "minecraft:tag".equals(o.get("type").getAsString())) {
					if (ing.test(ItemStackJS.of(o.get("name")))) {
						index = i;
						break;
					}
				}
			}
		}
		if (index != -1) {
			entries.remove((index));
			return true;
		}
		return false;
	}

	public boolean removeItem(IngredientJS ing) {
		return removeItem(ing, false);
	}

	public boolean removeTable(ResourceLocation rl) {
		var index  = -1;
		for (var i = 0;i < entries.size();i++) {
			var entry = entries.get(i);
			if (entry instanceof JsonObject o) {
				if (o.get("type").equals("minecraft:loot_table")) {
					if (rl.toString().equals(o.get("name").getAsString())) {
						index = i;
						break;
					}
				}
			}
		}
		if (index != -1) {
			entries.remove((index));
			return true;
		}
		return false;
	}

	public void clearEntries(){ entries = new JsonArray(); }

	public void clearFunctions() { functions = new JsonArray(); }

	public void clearConditions() {	conditions = new JsonArray(); }


	public LootTableEntry addEmpty(int weight) {
		var json = new JsonObject();
		json.addProperty("type", "minecraft:empty");
		return addEntry(json).weight(weight);
	}

	public LootTableEntry addLootTable(ResourceLocation table) {
		var json = new JsonObject();
		json.addProperty("type", "minecraft:loot_table");
		json.addProperty("name", table.toString());
		return addEntry(json);
	}

	public LootTableEntry addItem(ItemStack item, int weight, @Nullable NumberProvider count) {
		var id = KubeJSRegistries.items().getId(item.getItem());

		if (id == null || item.isEmpty()) {
			return new LootTableEntry(new JsonObject());
		}

		var json = new JsonObject();
		json.addProperty("type", "minecraft:item");
		json.addProperty("name", id.toString());

		var entry = addEntry(json);

		if (weight >= 0) {
			entry.weight(weight);
		}

		if (count == null && item.getCount() > 1) {
			count = ConstantValue.exactly(item.getCount());
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
		var json = new JsonObject();
		json.addProperty("type", "minecraft:tag");
		json.addProperty("name", tag);
		json.addProperty("expand", expand);
		return addEntry(json);
	}
}
