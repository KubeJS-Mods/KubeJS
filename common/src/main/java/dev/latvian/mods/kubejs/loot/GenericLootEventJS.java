package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

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
		var builder = createLootBuilder(null, b);
		var json = builder.toJson();
		addJson(builder.customId == null ? id : builder.customId, json);
	}
}
