package dev.latvian.kubejs.loot.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.loot.AdditionalLootTableDataOwner;
import dev.latvian.kubejs.loot.condition.LootCondition;
import dev.latvian.kubejs.loot.condition.LootConditionImpl;
import dev.latvian.kubejs.loot.condition.LootConditionList;
import dev.latvian.kubejs.loot.function.LootFunction;
import dev.latvian.kubejs.loot.function.LootFunctionImpl;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class LootEntry implements JsonSerializable, AdditionalLootTableDataOwner, LootConditionImpl, LootFunctionImpl {
	public final LootConditionList conditions = new LootConditionList();
	public final LootFunctionList functions = new LootFunctionList();
	public final LootEntryList children = new LootEntryList();

	JsonObject data = new JsonObject();

	public LootEntry(IngredientJS ingredientJS) {
		if (ingredientJS.isEmpty()) {
			setEmptyType();
			return;
		}

		if (ingredientJS instanceof TagIngredientJS) {
			setTagType();
			setName(((TagIngredientJS) ingredientJS).getTag());
		} else if (ingredientJS.getStacks().size() == 1) {
			setItemType();
			setName(ingredientJS.getFirst().getId());
		} else {
			setGroupType();
			ingredientJS.getStacks().forEach(entry -> {
				LootEntry child = new LootEntry(entry);
				children.add(child);
			});
		}

		if (ingredientJS.getFirst().hasChance()) {
			conditions.randomChance((float) ingredientJS.getFirst().getChance());
		}
		if (ingredientJS.getCount() != 1) {
			functions.setCount(ingredientJS.getCount());
		}
		functions.setNbt(ingredientJS.getFirst().getNbt());
	}

	@HideFromJS
	public LootEntry(JsonObject object) {
		JsonObject copiedEntryJson = (JsonObject) JsonUtilsJS.copy(object);

		conditions.fill((JsonArray) JsonUtilsJS.extract("conditions", copiedEntryJson));
		functions.fill((JsonArray) JsonUtilsJS.extract("functions", copiedEntryJson));

		JsonArray childrenArray = (JsonArray) JsonUtilsJS.extract("children", copiedEntryJson);
		if (childrenArray != null) {
			childrenArray.forEach(entry -> {
				JsonObject entryAsObject = entry.getAsJsonObject();
				children.add(new LootEntry(entryAsObject));
			});
		}

		setAdditionalData(copiedEntryJson);
	}

	public void setType(String type) {
		data.addProperty("type", type);
	}

	public void setEmptyType() {
		setType("minecraft:empty");
	}

	public void setDynamicType() {
		setType("minecraft:dynamic");
	}

	public void setItemType() {
		setType("minecraft:item");
	}

	public void setTagType() {
		setType("minecraft:tag");
	}

	public void setGroupType() {
		setType("minecraft:group");
	}

	public void setAlternativesType() {
		setType("minecraft:alternatives");
	}

	public void setSequenceType() {
		setType("minecraft:sequence");
	}

	public String getType() {
		JsonElement element = data.get("type");
		return element != null ? element.getAsString() : "minecraft:empty";
	}

	public void setName(String name) {
		if (getType().equals("minecraft:tag")) {
			// tag needs expand. True means, that one of the tag item will be dropped.
			setExpand(true);
		} else {
			setExpand(null);
		}

		data.addProperty("name", name);
	}

	public String getName() {
		JsonElement element = data.get("name");
		return element != null ? element.getAsString() : null;
	}

	public Boolean getExpand() {
		JsonElement element = data.get("expand");
		return element != null ? element.getAsBoolean() : null;
	}

	public void setExpand(Boolean expand) {
		if (expand != null) {
			data.addProperty("expand", expand);
		}
	}

	public Integer getQuality() {
		JsonElement element = data.get("quality");
		return element != null ? element.getAsInt() : null;
	}

	public void setQuality(Integer quality) {
		if (quality != null) {
			data.addProperty("quality", quality);
		}
	}

	public Integer getWeight() {
		JsonElement element = data.get("weight");
		return element != null ? element.getAsInt() : null;
	}

	public void setWeight(Integer weight) {
		if (weight != null) {
			data.addProperty("weight", weight);
		}
	}

	public void modify(Consumer<LootEntry> consumer) {
		consumer.accept(this);
	}

	public JsonElement toJson() {
		JsonObject object = new JsonObject();

		fillAdditionalData(object);

		functions.fillJson(object);
		conditions.fillJson(object);
		children.fillJson("children", object);

		return object;
	}

	@Override
	public JsonObject getAdditionalData() {
		return data;
	}

	@Override
	@HideFromJS
	public void handleNewConditionImpl(LootCondition condition) {
		conditions.handleNewConditionImpl(condition);
	}

	@Override
	@HideFromJS
	public LootFunction handleNewFunctionImpl(LootFunction lootFunction) {
		return functions.handleNewFunctionImpl(lootFunction);
	}
}
