package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class GenericLootEventJS extends LootEventJS {
	public GenericLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public String getType() {
		return "minecraft:generic";
	}

	@Override
	public String getDirectory() {
		return "";
	}

	public void addGeneric(ResourceLocation id, Consumer<LootBuilder> b) {
		LootBuilder builder = createLootBuilder(null, b);
		JsonObject json = builder.toJson();
		addJson(builder.customId == null ? id : builder.customId, json);
	}
}
