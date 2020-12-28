package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.fabric.WorldgenAddEventJSFabric;
import dev.latvian.kubejs.world.gen.fabric.WorldgenRemoveEventJSFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

public class KubeJSFabric implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		try
		{
			KubeJS kubeJS = new KubeJS();
			FabricLoader.getInstance().getEntrypoints("kubejs-init", KubeJSInitializer.class).forEach((it) -> {
				it.onKubeJSInitialization();
				KubeJS.LOGGER.log(Level.DEBUG, "[KubeJS] Initialized entrypoint " + it.getClass().getSimpleName() + ".");
			});
			kubeJS.setup();
			kubeJS.loadComplete();

			BiomeModifications.create(new ResourceLocation("kubejs", "worldgen_additions")).add(ModificationPhase.ADDITIONS, BiomeSelectors.all(), (s, m) -> new WorldgenAddEventJSFabric(s, m).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD));
			BiomeModifications.create(new ResourceLocation("kubejs", "worldgen_removals")).add(ModificationPhase.REMOVALS, BiomeSelectors.all(), (s, m) -> new WorldgenRemoveEventJSFabric(s, m).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_REMOVE));
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}
}
