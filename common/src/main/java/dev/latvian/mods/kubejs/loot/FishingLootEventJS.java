package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class FishingLootEventJS extends LootEventJS {
	public FishingLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public String getType() {
		return "minecraft:fishing";
	}

	@Override
	public String getDirectory() {
		return "gameplay/fishing";
	}

	public void addFishing(ResourceLocation id, Consumer<LootBuilder> b) {
		LootBuilder builder = createLootBuilder(null, b);
		JsonObject json = builder.toJson();
		addJson(builder.customId == null ? id : builder.customId, json);
	}
}
