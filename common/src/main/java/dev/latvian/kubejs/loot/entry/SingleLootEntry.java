package dev.latvian.kubejs.loot.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import org.jetbrains.annotations.Nullable;

public class SingleLootEntry extends AbstractLootEntry {
	private Integer weight;
	private Integer quality;
	private String name;
	private Boolean expand;

	SingleLootEntry(String type) {
		super(type);
	}

	SingleLootEntry(TagIngredientJS ingredient) {
		this("minecraft:tag");
		name = ingredient.getTag();
		addAdditionalIngredientData(ingredient);
	}

	SingleLootEntry(IngredientJS ingredient) {
		super("minecraft:item");
		name = ingredient.getFirst().getId();
		addAdditionalIngredientData(ingredient);
	}

	SingleLootEntry(JsonObject json) {
		super(json);

		JsonElement name = JsonUtilsJS.extract("name", json);
		if(name != null) {
			setName(name.getAsString());
		}

		JsonElement weight = JsonUtilsJS.extract("weight", json);
		if(weight != null) {
			setWeight(weight.getAsInt());
		}

		JsonElement quality = JsonUtilsJS.extract("quality", json);
		if(quality != null) {
			setQuality(quality.getAsInt());
		}

		JsonElement expand = JsonUtilsJS.extract("expand", json);
		if(expand != null) {
			setExpand(expand.getAsBoolean());
		}

		setCustomData(json);
	}

	@Override
	protected boolean isValidEntryType(String type) {
		return VALID_ENTRY_TYPES.contains(type);
	}

	@Override
	@Nullable
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight == null ? 1 : weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public int getQuality() {
		return quality == null ? 0 : quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public boolean isExpand() {
		return expand == null || expand;
	}

	public void setExpand(Boolean expand) {
		this.expand = expand;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = super.toJson();

		if(getName() != null) {
			json.addProperty("name", getName());
		}

		if (weight != null) {
			json.addProperty("weight", getWeight());
		}

		if (quality != null) {
			json.addProperty("quality", getQuality());
		}

		if (getType().equals("minecraft:tag")) {
			json.addProperty("expand", isExpand());
		}

		serializeCustomData(json);

		return json;
	}
}
