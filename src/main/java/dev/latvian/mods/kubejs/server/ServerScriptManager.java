package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.TextIcons;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackKubeEvent;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScriptManager extends ScriptManager {
	private static final Component GEN_PACK_NAME = Component.empty().append(TextIcons.NAME).append(" (Generated)");

	private static ServerScriptManager staticInstance;

	public static void capture(RegistryAccess.Frozen registryAccess) {
		var registries = new RegistryAccessContainer(registryAccess);
		RegistryAccessContainer.current = registries;
		ServerScriptManager.staticInstance = new ServerScriptManager(registries);
		ServerScriptManager.staticInstance.reload();
	}

	public static ServerScriptManager release() {
		var instance = Objects.requireNonNull(ServerScriptManager.staticInstance);
		ServerScriptManager.staticInstance = null;
		return instance;
	}

	public final RegistryAccessContainer registries;
	public final Map<ResourceKey<?>, PreTagKubeEvent> preTagEvents;
	public final RecipeSchemaStorage recipeSchemaStorage;
	public RecipeViewerData recipeViewerData;

	private ServerScriptManager(RegistryAccessContainer registries) {
		super(ScriptType.SERVER);
		this.registries = registries;
		this.preTagEvents = new ConcurrentHashMap<>();
		this.recipeSchemaStorage = new RecipeSchemaStorage();

		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	@Override
	public RegistryAccessContainer getRegistries() {
		return registries;
	}

	@Override
	public void loadFromDirectory() {
		super.loadFromDirectory();

		if (FMLLoader.getDist().isDedicatedServer()) {
			loadPackFromDirectory(KubeJSPaths.LOCAL_SERVER_SCRIPTS, "local server", true);
		}
	}

	public static void addPacksFirst(AddPackFindersEvent event) {
		event.addRepositorySource(c -> c.accept(VirtualKubeJSDataPack.HIGH.pack));

		var genPack = new Pack(
			new PackLocationInfo("kubejs_generated", GEN_PACK_NAME, PackSource.BUILT_IN, Optional.of(new KnownPack(KubeJS.MOD_ID, "kubejs_generated", "1"))),
			BuiltInPackSource.fixedResources(new GeneratedServerResourcePack()),
			new Pack.Metadata(GEN_PACK_NAME, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
			new PackSelectionConfig(true, Pack.Position.BOTTOM, true)
		);

		event.addRepositorySource(c -> c.accept(genPack));

		for (var file : Objects.requireNonNull(KubeJSPaths.DATA.toFile().listFiles())) {
			var fileName = file.getName();

			if (file.isFile() && fileName.endsWith(".zip")) {
				var zipPack = new Pack(
					new PackLocationInfo(fileName, Component.literal(fileName), PackSource.BUILT_IN, Optional.of(new KnownPack(KubeJS.MOD_ID, "kubejs_generated", "1"))),
					new FilePackResources.FileResourcesSupplier(file),
					new Pack.Metadata(GEN_PACK_NAME, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true),
					new PackSelectionConfig(true, Pack.Position.BOTTOM, true)
				);

				event.addRepositorySource(c -> c.accept(zipPack));
			}
		}

		/*
		list.addLast(new GeneratedServerResourcePack());

		for (var file : Objects.requireNonNull(KubeJSPaths.DATA.toFile().listFiles())) {
			if (file.isFile() && file.getName().endsWith(".zip")) {
				var access = new FilePackResources.FileResourcesSupplier(file);
				list.addLast(access.openPrimary(new PackLocationInfo(file.getName(), Component.literal(file.getName()), PackSource.BUILT_IN, Optional.empty())));
			}
		}
		 */
	}

	public static void addPacksLast(AddPackFindersEvent event) {
		event.addRepositorySource(c -> c.accept(VirtualKubeJSDataPack.LOW.pack));
	}

	@Override
	public void reload() {
		ConsoleJS.SERVER.setCapturingErrors(true);
		super.reload();
		ConsoleJS.SERVER.info("Scripts loaded");
		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);
		PreTagKubeEvent.handle(preTagEvents);

		VirtualKubeJSDataPack.HIGH.reset();

		if (ServerEvents.HIGH_DATA.hasListeners()) {
			ServerEvents.HIGH_DATA.post(ScriptType.SERVER, new DataPackKubeEvent(VirtualKubeJSDataPack.HIGH));
		}

		VirtualKubeJSDataPack.LOW.reset();

		if (ServerEvents.LOW_DATA.hasListeners()) {
			ServerEvents.LOW_DATA.post(ScriptType.SERVER, new DataPackKubeEvent(VirtualKubeJSDataPack.LOW));
		}
	}

	/*
	// FIXME
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

		ConsoleJS.SERVER.info("Scripts loaded");

		CustomIngredientAction.MAP.clear();

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);

		PreTagKubeEvent.handle(preTagEvents);

		return wrappedResourceManager;
	}
	 */

	public boolean recipes(RecipeManagerKJS recipeManager, ResourceManager resourceManager, Map<ResourceLocation, JsonElement> map) {
		if (ServerEvents.COMPOSTABLE_RECIPES.hasListeners()) {
			ServerEvents.COMPOSTABLE_RECIPES.post(ScriptType.SERVER, new CompostableRecipesKubeEvent());
		}

		boolean result = false;
		RecipesKubeEvent.TEMP_ITEM_TAG_LOOKUP.setValue(registries.cachedItemTags);
		recipeSchemaStorage.fireEvents(resourceManager);

		if (ServerEvents.RECIPES.hasListeners()) {
			new RecipesKubeEvent(this).post(recipeManager, map);
			result = true;
		}

		recipeViewerData = RecipeViewerData.collect();
		RecipesKubeEvent.TEMP_ITEM_TAG_LOOKUP.setValue(null);
		return result;
	}
}
