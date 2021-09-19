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
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

	public EntityLootEventJS(Map<ResourceLocation, JsonElement> c) {
		super(c);
	}

	@Override
	public String getDirectory() {
		return "entities";
	}

	public void addEntity(EntityType<?> type, Consumer<EntityLootBuilder> b) {
		EntityLootBuilder builder = new EntityLootBuilder();
		b.accept(builder);
		JsonObject json = builder.toJson(this);
		ResourceLocation entityId = KubeJSRegistries.entityTypes().getId(type);

		if (entityId != null && !entityId.equals(AIR_ID)) {
			addJson(entityId, json);
		}
	}
}
