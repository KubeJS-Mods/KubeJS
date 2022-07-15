package dev.latvian.mods.kubejs.server;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.recipe.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipeTypeJS;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.script.ScriptManager;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerScriptManager {
	public static ServerScriptManager instance;

	public static ScriptManager getScriptManager() {
		return instance.scriptManager;
	}

	private final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");

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
		scriptManager.reload(resourceManager);
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

		ConsoleJS.SERVER.pushLineNumber();

		KubeJSEvents.DATAGEN_LOW_DATA.post(new DataPackEventJS(virtualDataPackLow, wrappedResourceManager));
		KubeJSEvents.DATAGEN_HIGH_DATA.post(new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager));

		ConsoleJS.SERVER.popLineNumber();
		ConsoleJS.SERVER.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		var modEvent = new RegisterRecipeTypesEvent(typeMap);
		KubeJSPlugins.forEachPlugin(plugin -> plugin.registerRecipeTypes(modEvent));
		KubeJSEvents.SERVER_RECIPE_TYPE_REGISTRY.post(new RecipeTypeRegistryEventJS(typeMap));

		// Currently custom ingredients are only supported on Forge
		if (Platform.isForge()) {
			RecipesEventJS.customIngredientMap = new HashMap<>();
		}

		RecipesEventJS.modifyResultCallbackMap = new HashMap<>();

		CustomIngredientAction.MAP.clear();

		RecipesEventJS.instance = new RecipesEventJS(typeMap);

		return wrappedResourceManager;
	}
}
