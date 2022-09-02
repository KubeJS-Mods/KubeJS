package dev.latvian.mods.kubejs.server;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
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
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
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

	public MultiPackResourceManager wrapResourceManager(CloseableResourceManager original) {
		var virtualDataPackLow = new VirtualKubeJSDataPack(false);
		var virtualDataPackHigh = new VirtualKubeJSDataPack(true);

		// safety check, should be able to use MultiPackResourceManager unless another mod has messed with it
		var list = new LinkedList<>(original instanceof MultiPackResourceManager mp ? mp.packs : original.listPacks().toList());

		list.addFirst(virtualDataPackLow);
		list.addLast(new KubeJSServerResourcePack());
		list.addLast(virtualDataPackHigh);

		var wrappedResourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, list);

		reloadScriptManager(wrappedResourceManager);

		ConsoleJS.SERVER.pushLineNumber();

		ServerEvents.LOW_DATA.post(new DataPackEventJS(virtualDataPackLow, wrappedResourceManager));
		ServerEvents.HIGH_DATA.post(new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager));

		ConsoleJS.SERVER.popLineNumber();
		ConsoleJS.SERVER.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		var modEvent = new RegisterRecipeTypesEvent(typeMap);
		KubeJSPlugins.forEachPlugin(plugin -> plugin.registerRecipeTypes(modEvent));
		ServerEvents.RECIPE_TYPE_REGISTRY.post(new RecipeTypeRegistryEventJS(typeMap));

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
