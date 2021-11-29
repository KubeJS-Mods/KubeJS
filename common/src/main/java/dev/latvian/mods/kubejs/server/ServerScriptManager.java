package dev.latvian.mods.kubejs.server;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.script.ScriptFile;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptPackInfo;
import dev.latvian.mods.kubejs.script.ScriptSource;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerScriptManager {
	public static ServerScriptManager instance;

	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");

	public void init(ServerResources serverResources) {
		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	public void reloadScriptManager(ResourceManager resourceManager) {
		scriptManager.unload();
		scriptManager.loadFromDirectory();

		Map<String, List<ResourceLocation>> packs = new HashMap<>();

		for (ResourceLocation resource : resourceManager.listResources("kubejs", s -> s.endsWith(".js"))) {
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (Map.Entry<String, List<ResourceLocation>> entry : packs.entrySet()) {
			ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (ResourceLocation id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (ScriptFileInfo fileInfo : pack.info.scripts) {
				ScriptSource.FromResource scriptSource = info -> resourceManager.getResource(info.id);
				Throwable error = fileInfo.preload(scriptSource);

				if (fileInfo.isIgnored()) {
					continue;
				}

				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}

			pack.scripts.sort(null);
			scriptManager.packs.put(pack.info.namespace, pack);
		}

		scriptManager.load();
	}

	public List<PackResources> resourcePackList(List<PackResources> list0) {
		VirtualKubeJSDataPack virtualDataPackLow = new VirtualKubeJSDataPack(false);
		VirtualKubeJSDataPack virtualDataPackHigh = new VirtualKubeJSDataPack(true);
		List<PackResources> list = new ArrayList<>();
		list.add(virtualDataPackLow);
		list.addAll(list0);
		list.add(new KubeJSServerResourcePack());
		list.add(virtualDataPackHigh);

		SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(PackType.SERVER_DATA);

		for (PackResources p : list) {
			resourceManager.add(p);
		}

		reloadScriptManager(resourceManager);

		ConsoleJS.SERVER.setLineNumber(true);
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, "server.datapack.last");
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LOW_PRIORITY);
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, "server.datapack.first");
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_HIGH_PRIORITY);

		UtilsJS.postModificationEvents();

		ConsoleJS.SERVER.setLineNumber(false);
		ConsoleJS.SERVER.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		RegisterRecipeHandlersEvent modEvent = new RegisterRecipeHandlersEvent(typeMap);
		KubeJSPlugins.forEachPlugin(plugin -> plugin.addRecipes(modEvent));
		RegisterRecipeHandlersEvent.EVENT.invoker().accept(modEvent);
		new RecipeTypeRegistryEventJS(typeMap).post(ScriptType.SERVER, KubeJSEvents.RECIPES_TYPE_REGISTRY);

		// Currently custom ingredients are only supported on Forge
		if (Platform.isForge()) {
			RecipeEventJS.customIngredientMap = new HashMap<>();
		}

		CustomIngredientAction.MAP.clear();

		RecipeEventJS.instance = new RecipeEventJS(typeMap);
		return list;
	}
}
