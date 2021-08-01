package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LootTableEventJS extends EventJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	private final Map<ResourceLocation, JsonElement> lootTables;

	public LootTableEventJS(Map<ResourceLocation, JsonElement> map1) {
		super();
		this.lootTables = map1;
	}

	public void testSelfBuild(LootTableBuilder builder, ResourceLocation resourceLocation) {
		JsonObject jsonObjectB = (JsonObject) lootTables.get(resourceLocation);

		/*
		  Mojang takes numbers as numbers even if there are covert in "". And Mojang cannot
		  decide if one loot table numbers should be in "" and in another loot table numbers
		  should be numbers.
		  And MapJS covers the same logic. But GSON not.
		  This is the reason why we do the MapJS.of().toJson() thing.
		  Without it a "2" == 2 will fail. And we don't want it to fail.
		 */
		boolean equal = JsonUtilsJS.equal(MapJS.of(builder.toJson()).toJson(), MapJS.of(jsonObjectB).toJson());
		if (equal) {
			ScriptType.SERVER.console.info(String.format("Test check with builder against '%s' was successful.", resourceLocation));
		} else {
			ScriptType.SERVER.console.error(String.format("ERROR: Test check with builder against and '%s' FAILED.", resourceLocation));
		}
	}

	public void testSelfBuild(ResourceLocation resourceLocation) {
		LootTableBuilder builder = get(resourceLocation);
		testSelfBuild(builder, resourceLocation);
	}

	public void forEachTables(BiConsumer<LootTableBuilder, ResourceLocation> consumer) {
		lootTables.forEach((resourceLocation, element) -> {
			LootTableBuilder builder = new LootTableBuilder((JsonObject) element);
			consumer.accept(builder, new ResourceLocation(resourceLocation.toString()));
		});
	}

	public int countTables() {
		return lootTables.size();
	}

	public void raw(ResourceLocation id, Consumer<MapJS> consumer) {
		JsonObject jsonObject = (JsonObject) lootTables.get(id);
		if (jsonObject == null) {
			throw new IllegalArgumentException(String.format("No loot table for resource '%s'.", id));
		}

		MapJS tableAsMap = MapJS.of(jsonObject);
		if (tableAsMap == null) {
			throw new IllegalStateException(String.format("Converting internal loot table '%s' to MapJS went wrong. Please report this to the devs", id));
		}

		consumer.accept(tableAsMap);
		addIfContains(id, tableAsMap.toJson());
	}

	private void addIfContains(ResourceLocation key, JsonObject json) {
		if (lootTables.containsKey(key)) {
			lootTables.put(key, json);
		}
	}

	public LootTableBuilder get(ResourceLocation id) {
		JsonObject jsonObject = (JsonObject) lootTables.get(id);
		if (jsonObject == null) {
			throw new IllegalArgumentException(String.format("No loot table for resource '%s'.", id));
		}

		return new LootTableBuilder(jsonObject);
	}

	public void modify(ResourceLocation id, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = get(id);
		if (builder == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		consumer.accept(builder);

		addIfContains(id, builder.toJson());
	}
}
