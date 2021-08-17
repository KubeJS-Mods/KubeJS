package dev.latvian.kubejs.loot.entry;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.loot.condition.LootCondition;
import dev.latvian.kubejs.loot.condition.LootConditionImpl;
import dev.latvian.kubejs.loot.condition.LootConditionList;
import dev.latvian.kubejs.loot.function.LootFunction;
import dev.latvian.kubejs.loot.function.LootFunctionImpl;
import dev.latvian.kubejs.loot.function.LootFunctionList;
import dev.latvian.kubejs.util.CustomDataOwner;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.NamedObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.function.Consumer;

public abstract class AbstractLootEntry implements NamedObject, CustomDataOwner, LootConditionImpl, LootFunctionImpl {
	protected static ImmutableList<String> VALID_ENTRY_TYPES = ImmutableList.of("minecraft:empty", "minecraft:item", "minecraft:empty", "minecraft:loot_table", "minecraft:dynamic");
	protected static ImmutableList<String> VALID_GROUP_ENTRY_TYPES = ImmutableList.of("minecraft:group", "minecraft:sequence", "minecraft:alternatives");

	public final LootConditionList conditions = new LootConditionList();
	public final LootFunctionList functions = new LootFunctionList();

	protected String type;
	private final JsonObject additionalData = new JsonObject();

	public static AbstractLootEntry of(Object o) {
		if(o instanceof AbstractLootEntry) {
			return (AbstractLootEntry) o;
		}

		if (o instanceof JsonObject) {
			return of((JsonObject) o);
		}

		if (o instanceof CharSequence || o instanceof ResourceLocation) {
			return of(o.toString());
		}

		IngredientJS ingredient = IngredientJS.of(o);
		return of(ingredient);
	}

	public static AbstractLootEntry of(CharSequence c) {
		if (VALID_ENTRY_TYPES.contains(c.toString())) {
			return new SingleLootEntry(c.toString());
		}

		if (VALID_GROUP_ENTRY_TYPES.contains(c.toString())) {
			return new CompositeLootEntry(c.toString());
		}

		IngredientJS ingredient = IngredientJS.of(c);
		return of(ingredient);
	}

	public static AbstractLootEntry of(IngredientJS ingredient) {
		if (ingredient.isEmpty()) {
			return new SingleLootEntry("minecraft:empty");
		}

		if (ingredient instanceof TagIngredientJS) {
			return new SingleLootEntry((TagIngredientJS) ingredient);
		}

		if (ingredient.getStacks().size() == 1) {
			return new SingleLootEntry(ingredient);
		}

		return new CompositeLootEntry(ingredient);
	}

	public static AbstractLootEntry of(JsonObject object) {
		JsonObject copiedEntryJson = (JsonObject) JsonUtilsJS.copy(object);
		if (!copiedEntryJson.has("type")) {
			throw new IllegalArgumentException("Type is missing in json");
		}

		String type = GsonHelper.getAsString(copiedEntryJson, "type");
		if(VALID_GROUP_ENTRY_TYPES.contains(type)) {
			return new CompositeLootEntry(copiedEntryJson);
		}

		return new SingleLootEntry(copiedEntryJson);
	}

	AbstractLootEntry(String type) {
		this.type = type;
	}

	AbstractLootEntry(JsonObject json) {
		this(GsonHelper.getAsString(json, "type"));
		conditions.addAll((JsonArray) JsonUtilsJS.extract("conditions", json));
		functions.addAll((JsonArray) JsonUtilsJS.extract("functions", json));
	}

	protected void addAdditionalIngredientData(IngredientJS ingredient) {
		if (ingredient.getFirst().hasChance()) {
			conditions.randomChance((float) ingredient.getFirst().getChance());
		}
		if (ingredient.getCount() != 1) {
			functions.setCount(ingredient.getCount());
		}
		functions.setNbt(ingredient.getFirst().getNbt());
	}

	protected abstract boolean isValidEntryType(String type);

	public void setType(String type) {
		if (!isValidEntryType(type)) {
			throw new IllegalArgumentException(String.format("Type '%s' cannot be set for this entry. Consider to create or replace this entry with a new one.", type));
		}

		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = new JsonObject();

		object.addProperty("type", getType());
		functions.serializeInto(object);
		conditions.serializeInto(object);

		return object;
	}

	public void modify(Consumer<AbstractLootEntry> consumer) {
		consumer.accept(this);
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

	@Override
	@HideFromJS
	public JsonObject getCustomData() {
		return additionalData;
	}
}
