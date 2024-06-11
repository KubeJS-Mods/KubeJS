package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackKubeEvent;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScriptManager extends ScriptManager {
	private static final Component GEN_PACK_NAME = Component.empty().append(KubeJS.NAME_COMPONENT).append(" (Generated)");

	private static ServerScriptManager staticInstance;

	public static ServerScriptManager getScriptManager() {
		// return ((ReloadableServerResourcesKJS) ServerLifecycleHooks.getCurrentServer().getServerResources().managers()).kjs$getServerScriptManager();
		// I hate this, but it's required for Console.SERVER
		return staticInstance;
	}

	public final ReloadableServerResources resources;
	public final RegistryAccess registries;
	public final RegistryOps<Tag> nbtRegistryOps;
	public final RegistryOps<JsonElement> jsonRegistryOps;
	public final Map<ResourceKey<?>, PreTagKubeEvent> preTagEvents;
	public final RecipeSchemaStorage recipeSchemaStorage;

	public ServerScriptManager(ReloadableServerResources resources, RegistryAccess registryAccess) {
		super(ScriptType.SERVER);
		this.resources = resources;
		this.registries = registryAccess;
		this.nbtRegistryOps = registryAccess.createSerializationContext(NbtOps.INSTANCE);
		this.jsonRegistryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
		this.preTagEvents = new ConcurrentHashMap<>();
		this.recipeSchemaStorage = new RecipeSchemaStorage();

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
	public RegistryAccess getRegistries() {
		return registries;
	}

	@Override
	public RegistryOps<Tag> getNbtRegistryOps() {
		return nbtRegistryOps;
	}

	@Override
	public RegistryOps<JsonElement> getJsonRegistryOps() {
		return jsonRegistryOps;
	}

	@Override
	public DamageSources getDamageSources() {
		// Probably no better way to get this
		return ServerLifecycleHooks.getCurrentServer().kjs$getOverworld().damageSources();
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
		CustomIngredientAction.MAP.clear();
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

	public boolean recipes(RecipeManagerKJS recipeManager, HolderLookup.Provider registries, ResourceManager resourceManager, Map<ResourceLocation, JsonElement> map) {
		if (ServerEvents.COMPOSTABLE_RECIPES.hasListeners()) {
			ServerEvents.COMPOSTABLE_RECIPES.post(ScriptType.SERVER, new CompostableRecipesKubeEvent());
		}

		recipeSchemaStorage.fireEvents(resourceManager);

		if (ServerEvents.RECIPES.hasListeners()) {
			new RecipesKubeEvent(recipeSchemaStorage, registries).post(recipeManager, map);
			return true;
		}

		return false;
	}
}
