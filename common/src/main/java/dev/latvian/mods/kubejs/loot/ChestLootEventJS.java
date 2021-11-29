package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ChestLootEventJS extends LootEventJS {
	public ChestLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public String getType() {
		return "minecraft:chest";
	}

	@Override
	public String getDirectory() {
		return "chests";
	}

	public void addChest(ResourceLocation id, Consumer<LootBuilder> b) {
		LootBuilder builder = createLootBuilder(null, b);
		JsonObject json = builder.toJson();
		addJson(builder.customId == null ? id : builder.customId, json);
	}
}
