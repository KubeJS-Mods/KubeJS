package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class ServerScriptManager {
	public static ServerScriptManager instance;

	public static ScriptManager getScriptManager() {
		return instance.scriptManager;
	}

	private final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS);

	public ServerScriptManager() {
		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	public void updateResources(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
		KubeJSReloadListener.resources = serverResources;
		KubeJSReloadListener.recipeContext = RecipePlatformHelper.get().createRecipeContext(serverResources);
		UtilsJS.staticRegistryAccess = registryAccess;
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
		list.addLast(new GeneratedServerResourcePack());
		// list.addLast(KubeJSFolderPackResources.PACK);

		for (var file : Objects.requireNonNull(KubeJSPaths.DATA.toFile().listFiles())) {
			if (file.isFile() && file.getName().endsWith(".zip")) {
				list.addLast(new FilePackResources(file.getName(), file, false));
			}
		}

		list.addLast(virtualDataPackHigh);

		var wrappedResourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, list);

		reloadScriptManager(wrappedResourceManager);

		ServerEvents.LOW_DATA.post(ScriptType.SERVER, new DataPackEventJS(virtualDataPackLow, wrappedResourceManager));
		ServerEvents.HIGH_DATA.post(ScriptType.SERVER, new DataPackEventJS(virtualDataPackHigh, wrappedResourceManager));

		ConsoleJS.SERVER.info("Scripts loaded");

		// note we only set this map on the logical server, it'll be null on the client!
		RecipesEventJS.customIngredientMap = new HashMap<>();

		CustomIngredientAction.MAP.clear();

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::onServerReload);

		if (ServerEvents.RECIPES.hasListeners()) {
			RecipesEventJS.instance = new RecipesEventJS();
		}

		return wrappedResourceManager;
	}
}
