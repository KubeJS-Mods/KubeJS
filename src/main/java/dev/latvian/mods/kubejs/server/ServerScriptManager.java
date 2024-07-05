package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.ServerRegistryKubeEvent;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedDataStage;
import dev.latvian.mods.kubejs.script.data.KubeFileResourcePack;
import dev.latvian.mods.kubejs.script.data.VirtualDataPack;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerScriptManager extends ScriptManager {
	private static ServerScriptManager staticInstance;

	public static List<PackResources> createPackResources(List<PackResources> original) {
		var packs = new ArrayList<>(original);

		var filePacks = new ArrayList<PackResources>();
		KubeFileResourcePack.scanAndLoad(KubeJSPaths.DATA, filePacks);
		filePacks.sort((p1, p2) -> p1.packId().compareToIgnoreCase(p2.packId()));
		filePacks.add(new KubeFileResourcePack(PackType.SERVER_DATA));

		int beforeModsIndex = KubeFileResourcePack.findBeforeModsIndex(packs);
		int afterModsIndex = KubeFileResourcePack.findAfterModsIndex(packs);

		var manager = new ServerScriptManager();
		packs.add(beforeModsIndex, manager.virtualPacks.get(GeneratedDataStage.BEFORE_MODS));
		packs.add(afterModsIndex, manager.internalDataPack);
		packs.add(afterModsIndex + 1, manager.registriesDataPack);
		packs.add(afterModsIndex + 2, manager.virtualPacks.get(GeneratedDataStage.AFTER_MODS));
		packs.addAll(afterModsIndex + 3, filePacks);
		packs.add(manager.virtualPacks.get(GeneratedDataStage.LAST));
		manager.reload();
		staticInstance = manager;

		if (!FMLLoader.isProduction()) {
			KubeJS.LOGGER.info("Loaded " + packs.size() + " data packs: " + packs.stream().map(PackResources::packId).collect(Collectors.joining(", ")));
		}

		return packs;
	}

	public static ServerScriptManager release() {
		var instance = Objects.requireNonNull(staticInstance);
		staticInstance = null;
		return instance;
	}

	public final Map<ResourceKey<?>, PreTagKubeEvent> preTagEvents;
	public final RecipeSchemaStorage recipeSchemaStorage;
	public RecipeViewerData recipeViewerData;
	public final VirtualDataPack internalDataPack;
	public final VirtualDataPack registriesDataPack;
	public final Map<GeneratedDataStage, VirtualDataPack> virtualPacks;
	public boolean firstLoad;

	private ServerScriptManager() {
		super(ScriptType.SERVER);
		this.preTagEvents = new ConcurrentHashMap<>();
		this.recipeSchemaStorage = new RecipeSchemaStorage();
		this.recipeViewerData = null;

		this.internalDataPack = new VirtualDataPack(GeneratedDataStage.INTERNAL);
		this.registriesDataPack = new VirtualDataPack(GeneratedDataStage.REGISTRIES);
		this.virtualPacks = GeneratedDataStage.forScripts(VirtualDataPack::new);

		this.firstLoad = true;

		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	@Override
	public void loadFromDirectory() {
		ConsoleJS.SERVER.startCapturingErrors();
		super.loadFromDirectory();

		if (FMLLoader.getDist().isDedicatedServer()) {
			loadPackFromDirectory(KubeJSPaths.LOCAL_SERVER_SCRIPTS, "local server", true);
		}
	}

	@Override
	public void loadAdditional() {
		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateDataJsons(internalDataPack);
		}

		KubeJSPlugins.forEachPlugin(internalDataPack, KubeJSPlugin::generateData);

		if (firstLoad) {
			firstLoad = false;

			if (ServerEvents.REGISTRY.hasListeners()) {
				var ops = RegistryAccessContainer.current.json();

				for (var reg : DataPackRegistriesHooks.getDataPackRegistries()) {
					var key = (ResourceKey) reg.key();

					if (ServerEvents.REGISTRY.hasListeners(key)) {
						ServerEvents.REGISTRY.post(ScriptType.SERVER, key, new ServerRegistryKubeEvent(registriesDataPack, key, ops, reg.elementCodec()));
					}
				}
			}
		}
	}

	@Override
	public void reload() {
		internalDataPack.reset();

		for (var pack : virtualPacks.values()) {
			pack.reset();
		}

		recipeViewerData = null;

		super.reload();

		PreTagKubeEvent.handle(preTagEvents);

		for (var pack : virtualPacks.values()) {
			if (ServerEvents.GENERATE_DATA.hasListeners(pack.stage)) {
				ServerEvents.GENERATE_DATA.post(ScriptType.SERVER, pack.stage, pack);
			}
		}
	}

	public boolean recipes(RecipeManagerKJS recipeManager, ResourceManager resourceManager, Map<ResourceLocation, JsonElement> map) {
		if (ServerEvents.COMPOSTABLE_RECIPES.hasListeners()) {
			ServerEvents.COMPOSTABLE_RECIPES.post(ScriptType.SERVER, new CompostableRecipesKubeEvent());
		}

		boolean result = false;
		RecipesKubeEvent.TEMP_ITEM_TAG_LOOKUP.setValue(getRegistries().cachedItemTags);
		recipeSchemaStorage.fireEvents(resourceManager);

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);

		if (ServerEvents.RECIPES.hasListeners()) {
			new RecipesKubeEvent(this).post(recipeManager, map);
			result = true;
		}

		recipeViewerData = RecipeViewerData.collect();
		RecipesKubeEvent.TEMP_ITEM_TAG_LOOKUP.setValue(null);
		return result;
	}

	@Override
	protected void fullReload() {
		var server = ServerLifecycleHooks.getCurrentServer();

		if (server != null) {
			server.execute(() -> server.kjs$runCommand("reload"));
		}
	}

	public void reloadAndCapture() {
		reload();
		staticInstance = this;
	}
}
