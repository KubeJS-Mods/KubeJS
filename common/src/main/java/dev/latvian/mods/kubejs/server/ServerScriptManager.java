package dev.latvian.mods.kubejs.server;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipeTypeJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerScriptManager {
	public static ServerScriptManager instance;

	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");

	public ServerScriptManager() {
		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	public void updateResources(ReloadableServerResources serverResources) {
		KubeJSReloadListener.resources = serverResources;
		KubeJSReloadListener.recipeContext = RecipePlatformHelper.get().createRecipeContext(serverResources);
	}

	public void reloadScriptManager(ResourceManager resourceManager) {
		scriptManager.unload();
		scriptManager.loadFromDirectory();

		Map<String, List<ResourceLocation>> packs = new HashMap<>();

		for (var resource : resourceManager.listResources("kubejs", s -> s.getPath().endsWith(".js")).keySet()) {
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (var entry : packs.entrySet()) {
			var pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (var id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (var fileInfo : pack.info.scripts) {
				var scriptSource = (ScriptSource.FromResource) info -> resourceManager.getResourceOrThrow(info.id);
				var error = fileInfo.preload(scriptSource);

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

	public MultiPackResourceManager wrapResourceManager(PackType type, List<PackResources> packs) {
		// TODO: Wrap the resource manager to inject KubeJS' virtual data packs and server scripts.
		var virtualDataPackLow = new VirtualKubeJSDataPack(false);
		var virtualDataPackHigh = new VirtualKubeJSDataPack(true);

		var list = new LinkedList<>(packs);

		list.addFirst(virtualDataPackLow);
		list.addLast(new KubeJSServerResourcePack());
		list.addLast(virtualDataPackHigh);

		var wrappedResourceManager = new MultiPackResourceManager(type, list);

		reloadScriptManager(wrappedResourceManager);

		ConsoleJS.SERVER.setLineNumber(true);

		new DataPackEventJS(virtualDataPackLow, wrappedResourceManager).post(ScriptType.SERVER, "server.datapack.last");
		new DataPackEventJS(virtualDataPackLow, wrappedResourceManager).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LOW_PRIORITY);
		new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager).post(ScriptType.SERVER, "server.datapack.first");
		new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_HIGH_PRIORITY);

		ConsoleJS.SERVER.setLineNumber(false);
		ConsoleJS.SERVER.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		var modEvent = new RegisterRecipeTypesEvent(typeMap);
		KubeJSPlugins.forEachPlugin(plugin -> plugin.registerRecipeTypes(modEvent));
		new RecipeTypeRegistryEventJS(typeMap).post(ScriptType.SERVER, KubeJSEvents.RECIPES_TYPE_REGISTRY);

		// Currently custom ingredients are only supported on Forge
		if (Platform.isForge()) {
			RecipeEventJS.customIngredientMap = new HashMap<>();
		}

		RecipeEventJS.modifyResultCallbackMap = new HashMap<>();

		CustomIngredientAction.MAP.clear();

		RecipeEventJS.instance = new RecipeEventJS(typeMap);

		return wrappedResourceManager;
	}
}
