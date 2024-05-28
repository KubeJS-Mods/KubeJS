package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackKubeEvent;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScriptManager extends ScriptManager {
	private static ServerScriptManager staticInstance;

	public static ServerScriptManager getScriptManager() {
		// return ((ReloadableServerResourcesKJS) ServerLifecycleHooks.getCurrentServer().getServerResources().managers()).kjs$getServerScriptManager();
		// I hate this, but it's required for Console.SERVER
		return staticInstance;
	}

	public final ReloadableServerResources resources;
	public final HolderLookup.Provider registries;
	public final Map<ResourceKey<?>, PreTagKubeEvent> preTagEvents;

	public ServerScriptManager(ReloadableServerResources resources, RegistryAccess registryAccess) {
		super(ScriptType.SERVER);
		this.resources = resources;
		this.registries = registryAccess;
		this.preTagEvents = new ConcurrentHashMap<>();

		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}

		staticInstance = this;
	}

	@Override
	public HolderLookup.Provider getRegistries() {
		return registries;
	}

	public MultiPackResourceManager wrapResourceManager(HolderLookup.Provider registries, CloseableResourceManager original) {
		var virtualDataPackLow = new VirtualKubeJSDataPack(false);
		var virtualDataPackHigh = new VirtualKubeJSDataPack(true);

		// safety check, should be able to use MultiPackResourceManager unless another mod has messed with it
		var list = new LinkedList<>(original instanceof MultiPackResourceManager mp ? mp.packs : original.listPacks().toList());

		list.addFirst(virtualDataPackLow);
		list.addLast(new GeneratedServerResourcePack());

		for (var file : Objects.requireNonNull(KubeJSPaths.DATA.toFile().listFiles())) {
			if (file.isFile() && file.getName().endsWith(".zip")) {
				var access = new FilePackResources.FileResourcesSupplier(file);
				list.addLast(access.openPrimary(new PackLocationInfo(file.getName(), Component.literal(file.getName()), PackSource.BUILT_IN, Optional.empty())));
			}
		}

		list.addLast(virtualDataPackHigh);

		var wrappedResourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, list);

		ConsoleJS.SERVER.setCapturingErrors(true);
		reload(wrappedResourceManager);

		ServerEvents.LOW_DATA.post(ScriptType.SERVER, new DataPackKubeEvent(virtualDataPackLow, wrappedResourceManager));
		ServerEvents.HIGH_DATA.post(ScriptType.SERVER, new DataPackKubeEvent(virtualDataPackHigh, wrappedResourceManager));

		ConsoleJS.SERVER.info("Scripts loaded");

		// note we only set this map on the logical server, it'll be null on the client!
		RecipesKubeEvent.customIngredientMap = new HashMap<>();

		CustomIngredientAction.MAP.clear();

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::onServerReload);

		PreTagKubeEvent.handle(preTagEvents);

		if (ServerEvents.RECIPES.hasListeners()) {
			RecipesKubeEvent.instance = new RecipesKubeEvent(registries);
		}

		return wrappedResourceManager;
	}
}
