package dev.latvian.kubejs.loot.entry;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import net.minecraft.util.GsonHelper;
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

		if(json.has("name")) {
			setName(GsonHelper.getAsString(json, "name"));
		}

		if(json.has("weight")) {
			setWeight(GsonHelper.getAsInt(json, "weight"));
		}

		if(json.has("quality")) {
			setQuality(GsonHelper.getAsInt(json, "quality"));
		}

		if(json.has("expand")) {
			setExpand(GsonHelper.getAsBoolean(json, "expand"));
		}
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

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getQuality() {
		return quality == null ? 0 : quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public boolean isExpand() {
		return expand == null || expand;
	}

	public void setExpand(boolean expand) {
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

		return json;
	}
}
