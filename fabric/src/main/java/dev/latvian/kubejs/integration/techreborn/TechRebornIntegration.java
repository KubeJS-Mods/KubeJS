package dev.latvian.kubejs.integration.techreborn;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.special.SpecialRecipeSerializerManager;
import me.shedaniel.architectury.platform.Platform;

public class TechRebornIntegration implements KubeJSInitializer
{
	@Override
	public void onKubeJSInitialization()
	{
		if (Platform.isModLoaded("techreborn"))
		{
			SpecialRecipeSerializerManager.EVENT.register(() -> {
				for (String s : new String[] {
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
						"techreborn:wire_mill"
				})
				{
					SpecialRecipeSerializerManager.INSTANCE.ignoreSpecialFlag(s);
				}
			});
		}
	}
}
