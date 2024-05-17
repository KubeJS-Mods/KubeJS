package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnKubeEvent;
import dev.latvian.mods.kubejs.entity.EntitySpawnedKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDropsKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public interface EntityEvents {
	EventGroup GROUP = EventGroup.of("EntityEvents");

	Extra SUPPORTS_ENTITY_TYPE = new Extra().transformer(EntityEvents::transformEntityType).identity().describeType(context -> context.javaType(EntityType.class));

	private static Object transformEntityType(Object o) {
		if (o == null || o instanceof EntityType) {
			return o;
		}

		var id = ResourceLocation.tryParse(o.toString());
		return id == null ? null : RegistryInfo.ENTITY_TYPE.getValue(id);
	}

	EventHandler DEATH = GROUP.common("death", () -> LivingEntityDeathKubeEvent.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler HURT = GROUP.common("hurt", () -> LivingEntityHurtKubeEvent.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler CHECK_SPAWN = GROUP.common("checkSpawn", () -> CheckLivingEntitySpawnKubeEvent.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler SPAWNED = GROUP.common("spawned", () -> EntitySpawnedKubeEvent.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
	EventHandler ENTITY_DROPS = GROUP.common("drops", () -> LivingEntityDropsKubeEvent.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
}
