package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record IDFilter(ResourceLocation id) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(data.type).equals(id);
	}
}
