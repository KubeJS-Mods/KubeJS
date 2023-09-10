package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.function.Consumer;

public class EntityLootEventJS extends LootEventJS {
	public EntityLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public String getType() {
		return "minecraft:entity";
	}

	@Override
	public String getDirectory() {
		return "entities";
	}

	public void addEntity(EntityType<?> type, Consumer<LootBuilder> b) {
		var builder = createLootBuilder(null, b);
		var json = builder.toJson();
		var entityId = builder.customId == null ? RegistryInfo.ENTITY_TYPE.getId(type) : builder.customId;

		if (entityId != null) {
			addJson(entityId, json);
		}
	}

	public void modifyEntity(EntityType<?> type, Consumer<LootBuilder> b) {
		var entityId = RegistryInfo.ENTITY_TYPE.getId(type);

		if (entityId != null) {
			modify(entityId, b);
		}
	}
}
