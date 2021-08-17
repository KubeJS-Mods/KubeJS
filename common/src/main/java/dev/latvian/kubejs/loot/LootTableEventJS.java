package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.loot.handler.BasicLootTableHandler;
import dev.latvian.kubejs.loot.handler.BlockLootTableHandler;
import dev.latvian.kubejs.loot.handler.CustomLootTableHandler;
import dev.latvian.kubejs.loot.handler.EntityLootTableHandler;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class LootTableEventJS extends EventJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	private final Map<ResourceLocation, JsonElement> lootTables;

	public final BlockLootTableHandler blocks;
	public final EntityLootTableHandler entities;
	public final BasicLootTableHandler chests;
	public final BasicLootTableHandler fishing;
	public final BasicLootTableHandler gifts;
	public final CustomLootTableHandler custom;

	public LootTableEventJS(Map<ResourceLocation, JsonElement> map1) {
		super();
		this.lootTables = map1;
		this.blocks = new BlockLootTableHandler(map1);
		this.entities = new EntityLootTableHandler(map1);
		this.chests = new BasicLootTableHandler(map1, "minecraft:chest", "chests/");
		this.fishing = new BasicLootTableHandler(map1, "minecraft:fishing", "gameplay/");
		this.gifts = new BasicLootTableHandler(map1, "minecraft:gift", "gameplay/");
		this.custom = new CustomLootTableHandler(map1);
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

	public void testSelfBuild(ResourceLocation rl) {
		JsonObject json = (JsonObject) lootTables.get(rl);
		if (json == null) {
			throw new IllegalArgumentException("No table found");
		}

		LootTableBuilder builder = new LootTableBuilder(json);
		testSelfBuild(builder, rl);
	}

	public LootTableBuilder newBuilder() {
		return new LootTableBuilder();
	}

	public void forEachTables(Consumer<ResourceLocation> action) {
		lootTables.forEach((resourceLocation, element) -> {
			action.accept(resourceLocation);
		});
	}

	public int countTables() {
		return lootTables.size();
	}

	public void raw(ResourceLocation rl, Consumer<MapJS> action) {
		JsonObject jsonObject = (JsonObject) lootTables.get(rl);
		if (jsonObject == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", rl));
		}

		MapJS tableAsMap = MapJS.of(jsonObject);
		if (tableAsMap == null) {
			throw new IllegalStateException(String.format("Converting internal loot table '%s' to MapJS went wrong. Please report this to the devs", rl));
		}

		action.accept(tableAsMap);
		lootTables.put(rl, tableAsMap.toJson());
		if (ServerSettings.instance.logChangingLootTables) {
			ScriptType.SERVER.console.info(String.format("[Raw edit] '%s'", rl));
		}
	}
}
