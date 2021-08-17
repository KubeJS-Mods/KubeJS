package dev.latvian.kubejs.loot.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.LootTableBuilder;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class LootTableHandler {
	protected static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	protected final Map<ResourceLocation, JsonElement> lootTables;

	public LootTableHandler(Map<ResourceLocation, JsonElement> tables) {
		this.lootTables = tables;
	}

	public abstract String getType();

	@Nullable
	public LootTableBuilder get(ResourceLocation id) {
		ResourceLocation tableLocation = tryGetLootTableId(id);
		JsonObject jsonObject = (JsonObject) lootTables.get(tableLocation);
		return jsonObject != null
				? new LootTableBuilder(jsonObject)
				: null;
	}

	public void build(Object objects, Consumer<LootTableBuilder> action) {
		LootTableBuilder builder = new LootTableBuilder(getType());
		action.accept(builder);

		getLocations(objects).forEach(resourceLocation -> {
			if (add(resourceLocation, builder) && ServerSettings.instance.logChangingLootTables) {
				ScriptType.SERVER.console.info(String.format("[Build] '%s'", resourceLocation));
			}
		});
	}

	public void merge(Object objects, Consumer<LootTableBuilder> action) {
		LootTableBuilder builder = new LootTableBuilder(getType());
		action.accept(builder);

		getLocations(objects).forEach(resourceLocation -> {
			LootTableBuilder origin = get(resourceLocation);
			if (origin == null) {
				return;
			}

			origin.merge(builder);
			if (add(resourceLocation, origin) && ServerSettings.instance.logChangingLootTables) {
				ScriptType.SERVER.console.info(String.format("[Merge] '%s'", resourceLocation));
			}
		});
	}

	public void modify(ResourceLocation id, Consumer<LootTableBuilder> action) {
		ResourceLocation tableLocation = tryGetLootTableId(id);
		LootTableBuilder builder = get(tableLocation);
		if (builder == null || tableLocation == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		action.accept(builder);

		if (add(tableLocation, builder) && ServerSettings.instance.logChangingLootTables) {
			ScriptType.SERVER.console.info(String.format("[Modify] '%s'", tableLocation));
		}
	}

	public void clear(Object objects) {
		getLocations(objects).forEach(rl -> {
			JsonElement table = lootTables.get(rl);
			if(table == null || !table.isJsonObject()) {
				return;
			}

			JsonObject asJsonObject = table.getAsJsonObject();
			asJsonObject.entrySet().forEach(entry -> {
				if(!entry.getKey().equals("type")) {
					asJsonObject.remove(entry.getKey());
				}
			});

			if (ServerSettings.instance.logChangingLootTables) {
				ScriptType.SERVER.console.info(String.format("[Clear] '%s'", rl));
			}
		});
	}

	public void print(ResourceLocation id) {
		ResourceLocation tableLocation = tryGetLootTableId(id);
		JsonElement table = lootTables.get(tableLocation);
		if(table == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		String s = JsonUtilsJS.toPrettyString(table);
		ScriptType.SERVER.console.info(String.format("Printing LootTable for '%s':\n%s", id, s));
	}


	public Object getValue(ResourceLocation id, String path) {
		ResourceLocation tableLocation = tryGetLootTableId(id);
		JsonElement table = lootTables.get(tableLocation);
		if (table == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		return JsonUtilsJS.get((JsonObject) table, path);
	}

	public boolean exists(ResourceLocation id) {
		return !id.equals(AIR_ID) && lootTables.containsKey(id);
	}

	public abstract Set<ResourceLocation> getLocations(Object objects);

	protected boolean add(ResourceLocation rl, LootTableBuilder builder) {
		if (rl.equals(AIR_ID)) {
			throw new IllegalArgumentException(String.format("Loot table for '%s' cannot be added.", AIR_ID));
		}

		if (exists(rl)) {
			lootTables.put(rl, builder.toJson());
			return true;
		}

		return false;
	}

	@Nullable
	protected ResourceLocation tryGetLootTableId(ResourceLocation id) {
		if (id.equals(AIR_ID)) {
			throw new IllegalArgumentException(String.format("'%s' is not allowed as argument.", AIR_ID));
		}

		if (exists(id)) {
			if (hasCorrectType(id)) {
				return id;
			}

			throw new IllegalArgumentException(String.format("Loot table '%s' does not has the correct type", id));
		}

		Set<ResourceLocation> resourceLocations = getLocations(id);
		if (resourceLocations.size() > 1) {
			throw new RuntimeException(String.format("Multiple tables found for '%s'. Try to be more specific. Found tables with given id: [%s].", id, resourceLocations));
		}

		return resourceLocations.stream().findFirst().orElse(null);
	}

	@Nullable
	protected String getTypeFromTable(ResourceLocation id) {
		JsonElement element = lootTables.get(id);
		if (!element.isJsonObject()) {
			return null;
		}

		return GsonHelper.getAsString(element.getAsJsonObject(), "type", null);
	}

	protected boolean hasCorrectType(ResourceLocation id) {
		String typeFromTable = getTypeFromTable(id);
		if(typeFromTable == null) {
			return false;
		}

		return typeFromTable.equals(getType());
	}

}
