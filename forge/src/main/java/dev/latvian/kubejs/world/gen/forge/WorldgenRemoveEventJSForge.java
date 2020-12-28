package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJSForge extends WorldgenRemoveEventJS
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

	private final BiomeLoadingEvent event;

	public WorldgenRemoveEventJSForge(BiomeLoadingEvent e)
	{
		event = e;
	}

	@Override
	public boolean verifyBiomes(WorldgenEntryList<String> biomes)
	{
		return biomes.verify(s -> {
			if (s.startsWith("#"))
			{
				return event.getCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return new ResourceLocation(s).equals(event.getName());
		});
	}
}
