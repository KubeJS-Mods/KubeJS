package dev.latvian.mods.kubejs.level.gen.filter.mob;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record IDFilter(ResourceLocation id) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return RegistryInfo.ENTITY_TYPE.getId(data.type).equals(id);
	}
}
