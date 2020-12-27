package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.world.level.biome.BiomeGenerationSettings;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJS extends EventJS
{
	/*
		if (biomeLoadingEvent.getName() != null && biomeLoadingEvent.getName().toString().equals(DIM_ID.toString()))
		{
			biomeLoadingEvent.getGeneration().getStructures().clear();
			if (FLOWERS.get())
			{
				biomeLoadingEvent.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).clear();
			}
			if (STRUCTURES.get())
			{
				biomeLoadingEvent.getGeneration().getFeatures(GenerationStage.Decoration.SURFACE_STRUCTURES).clear();
			}
			if (ENTITIES.get())
			{
				biomeLoadingEvent.getSpawns().getSpawnerTypes().forEach(spawnerType -> biomeLoadingEvent.getSpawns().getSpawner(spawnerType).clear());
			}
			if (LAKES.get())
			{
				biomeLoadingEvent.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).clear();
			}
		}
		 */

	private final BiomeGenerationSettings.Builder builder;

	public WorldgenRemoveEventJS(BiomeGenerationSettings.Builder b)
	{
		builder = b;
	}

	// Implement later //
}