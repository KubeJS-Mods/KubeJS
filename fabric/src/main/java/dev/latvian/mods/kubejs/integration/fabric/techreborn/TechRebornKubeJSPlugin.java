package dev.latvian.mods.kubejs.integration.fabric.techreborn;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.resources.ResourceLocation;

public class TechRebornKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void addRecipes(RegisterRecipeHandlersEvent event) {
		if (Platform.isModLoaded("techreborn")) {
			for (var s : new String[]{
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
			}) {
				event.register(new ResourceLocation(s), TRRecipeJS::new);
			}

			for (var s : new String[]{
					"techreborn:industrial_grinder",
					"techreborn:industrial_sawmill",
					"techreborn:fluid_replicator",
			}) {
				event.register(new ResourceLocation(s), TRRecipeWithTankJS::new);
			}

			// To be implemented later
			// "techreborn:fusion_reactor", // See FusionReactorRecipe
			// "techreborn:rolling_machine", // See RollingMachineRecipe

		}
	}
}
