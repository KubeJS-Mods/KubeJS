package dev.latvian.kubejs.integration.techreborn;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

public class TechRebornIntegration implements KubeJSInitializer
{
	@Override
	public void onKubeJSInitialization()
	{
		if (Platform.isModLoaded("techreborn"))
		{
			RegisterRecipeHandlersEvent.EVENT.register(event -> {
				for (String s : new String[] {
						// Default recipes
						"techreborn:alloy_smelter",
						"techreborn:assembling_machine",
						"techreborn:centrifuge",
						"techreborn:chemical_reactor",
						"techreborn:compressor",
						"techreborn:distillation_tower",
						"techreborn:extractor",
						"techreborn:grinder",
						"techreborn:implosion_compressor",
						"techreborn:industrial_electrolyzer",
						"techreborn:recycler",
						"techreborn:scrapbox",
						"techreborn:vacuum_freezer",
						"techreborn:solid_canning_machine",
						"techreborn:wire_mill",
						// Similar enough that the same serializer works
						"techreborn:blast_furnace",
				})
				{
					event.register(new ResourceLocation(s), TRRecipeJS::new);
				}

				for (String s : new String[] {
						"techreborn:industrial_grinder",
						"techreborn:industrial_grinder",
						"techreborn:fluid_replicator",
				})
				{
					event.register(new ResourceLocation(s), TRRecipeWithTankJS::new);
				}

				// To be implemented later
				// "techreborn:fusion_reactor", // See FusionReactorRecipe
				// "techreborn:rolling_machine", // See RollingMachineRecipe
			});
		}
	}
}
