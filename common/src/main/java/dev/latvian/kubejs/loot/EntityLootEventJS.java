package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
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
		LootBuilder builder = createLootBuilder(null, b);
		JsonObject json = builder.toJson();
		ResourceLocation entityId = builder.customId == null ? KubeJSRegistries.entityTypes().getId(type) : builder.customId;

		if (entityId != null) {
			addJson(entityId, json);
		}
	}

	public void modifyEntity(EntityType<?> type, Consumer<LootBuilder> b) {
		ResourceLocation entityId = KubeJSRegistries.entityTypes().getId(type);

		if (entityId != null) {
			modify(entityId, b);
		}
	}
}
