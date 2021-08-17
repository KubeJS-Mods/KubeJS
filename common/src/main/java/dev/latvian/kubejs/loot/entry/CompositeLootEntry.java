package dev.latvian.kubejs.loot.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.JsonUtilsJS;

import java.util.function.Consumer;

public class CompositeLootEntry extends AbstractLootEntry {

	public final LootEntryList children = new LootEntryList("children");

	CompositeLootEntry(String type) {
		super(type);
	}

	CompositeLootEntry(IngredientJS ingredient) {
		super("minecraft:group");
		ingredient.getStacks().forEach(itemStack -> {
			AbstractLootEntry child = AbstractLootEntry.of(itemStack);
			children.add(child);
		});
		addAdditionalIngredientData(ingredient);
	}

	CompositeLootEntry(JsonObject json) {
		super(json);

		JsonArray childrenArray = (JsonArray) JsonUtilsJS.extract("children", json);
		if (childrenArray != null) {
			childrenArray.forEach(entry -> {
				JsonObject entryAsJson = entry.getAsJsonObject();
				AbstractLootEntry child = AbstractLootEntry.of(entryAsJson);
				children.add(child);
			});
		}

		setCustomData(json);
	}

	@Override
	protected boolean isValidEntryType(String type) {
		return VALID_GROUP_ENTRY_TYPES.contains(type);
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException(String.format("Loot table entries with type '%s' cannot be access by name", AbstractLootEntry.VALID_GROUP_ENTRY_TYPES));
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = super.toJson();
		children.serializeInto(json);
		serializeCustomData(json);
		return json;
	}

	public AbstractLootEntry addEntry(Object o) {
		AbstractLootEntry entry = AbstractLootEntry.of(o);
		children.add(entry);
		return entry;
	}

	public void addEntry(Object o, Consumer<AbstractLootEntry> action) {
		AbstractLootEntry entry = AbstractLootEntry.of(o);
		children.add(entry);
		action.accept(entry);
	}
}
