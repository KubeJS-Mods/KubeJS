package dev.latvian.kubejs.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class LootEventJS extends EventJS {
	private final Map<ResourceLocation, JsonElement> lootMap;
	final Gson gsonConditions;
	final Gson gsonFunctions;
	final Gson gsonLootTables;

	public LootEventJS(Map<ResourceLocation, JsonElement> c) {
		lootMap = c;
		gsonConditions = Deserializers.createConditionSerializer().create();
		gsonFunctions = Deserializers.createFunctionSerializer().create();
		gsonLootTables = Deserializers.createLootTableSerializer().create();
	}

	public void addJson(ResourceLocation id, JsonObject json) {
		lootMap.put(new ResourceLocation(id.getNamespace(), getDirectory() + "/" + id.getPath()), json);
	}

	public abstract String getDirectory();
}
