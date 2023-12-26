package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.EntitySpawnedEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public interface EntityEvents {
	EventGroup GROUP = EventGroup.of("EntityEvents");

	Extra SUPPORTS_ENTITY_TYPE = new Extra().transformer(EntityEvents::transformEntityType).identity();

	private static Object transformEntityType(Object o) {
		if (o == null || o instanceof EntityType) {
			return o;
		}

		var id = ResourceLocation.tryParse(o.toString());
		return id == null ? null : RegistryInfo.ENTITY_TYPE.getValue(id);
	}

	EventHandler DEATH = GROUP.common("death", () -> LivingEntityDeathEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler HURT = GROUP.common("hurt", () -> LivingEntityHurtEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler CHECK_SPAWN = GROUP.common("checkSpawn", () -> CheckLivingEntitySpawnEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler SPAWNED = GROUP.common("spawned", () -> EntitySpawnedEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler ENTITY_DROPS = GROUP.common("drops", () -> LivingEntityDropsEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
}
