package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import dev.latvian.kubejs.script.ScriptSource;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author LatvianModder
 */
public class ServerScriptManager
{
	public static ServerScriptManager instance;

	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER);
	public final VirtualKubeJSDataPack virtualDataPackFirst = new VirtualKubeJSDataPack(true);
	public final VirtualKubeJSDataPack virtualDataPackLast = new VirtualKubeJSDataPack(false);

	public void reloadScripts(SimpleReloadableResourceManager resourceManager)
	{
		scriptManager.unload();

		Map<String, List<ResourceLocation>> packs = new HashMap<>();

		for (ResourceLocation resource : resourceManager.getAllResourceLocations("kubejs", s -> s.endsWith(".js")))
		{
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (Map.Entry<String, List<ResourceLocation>> entry : packs.entrySet())
		{
			ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (ResourceLocation id : entry.getValue())
			{
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (ScriptFileInfo fileInfo : pack.info.scripts)
			{
				ScriptSource scriptSource = info -> new InputStreamReader(resourceManager.getResource(info.location).getInputStream());
				Throwable error = fileInfo.preload(scriptSource);

				if (error == null)
				{
					if (fileInfo.shouldLoad(FMLEnvironment.dist))
					{
						pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
					}
				}
				else
				{
					KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}

			pack.scripts.sort(null);
			scriptManager.packs.put(pack.info.namespace, pack);
		}

		//Loading is required in prepare stage to allow virtual data pack overrides
		virtualDataPackFirst.resetData();
		ScriptType.SERVER.console.setLineNumber(true);
		scriptManager.load();

		new DataPackEventJS(virtualDataPackFirst).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_FIRST);
		new DataPackEventJS(virtualDataPackLast).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LAST);

		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		MinecraftForge.EVENT_BUS.post(new RegisterRecipeHandlersEvent(typeMap));
		RecipeEventJS.instance = new RecipeEventJS(typeMap);
	}

	public IFutureReloadListener createReloadListener()
	{
		return (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
			if (!(resourceManager instanceof SimpleReloadableResourceManager))
			{
				throw new RuntimeException("Resource manager is not SimpleReloadableResourceManager, KubeJS will not work! Unsupported resource manager class: " + resourceManager.getClass());
			}

			reloadScripts((SimpleReloadableResourceManager) resourceManager);
			return CompletableFuture.supplyAsync(Object::new, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(o -> {}, gameExecutor);
		};
	}
}
