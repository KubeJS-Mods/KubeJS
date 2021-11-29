package dev.latvian.mods.kubejs;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.utils.EnvExecutor;
import dev.latvian.mods.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import dev.latvian.mods.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.fluid.KubeJSFluidEventHandler;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.world.KubeJSWorldEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS {
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static KubeJS instance;

	public static KubeJSCommon PROXY;
	public static boolean nextClientHasClientMod = false;
	public static CreativeModeTab tab = CreativeModeTab.TAB_MISC;

	public static ScriptManager startupScriptManager, clientScriptManager;

	public KubeJS() throws Throwable {
		instance = this;
		Locale.setDefault(Locale.US);

		if (Files.notExists(KubeJSPaths.README)) {
			UtilsJS.tryIO(() -> {
				List<String> list = new ArrayList<>();
				list.add("Find more info on the website: https://kubejs.com/");
				list.add("");
				list.add("Directory information:");
				list.add("");
				list.add("assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png");
				list.add("data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json");
				list.add("");
				list.add("startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things that can only happen while the game is loading (Can be reloaded with /kubejs reload_startup_scripts, but it may not work!)");
				list.add("server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, loot tables, and handling server events (Can be reloaded with /reload)");
				list.add("client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things (Can be reloaded with F3+T)");
				list.add("");
				list.add("config - KubeJS config storage. This is also the only directory that scripts can access other than world directory");
				list.add("exported - Data dumps like texture atlases end up here");
				list.add("");
				list.add("You can find type-specific logs in logs/kubejs/ directory");
				Files.write(KubeJSPaths.README, list);
			});
		}

		PROXY = EnvExecutor.getEnvSpecific(() -> KubeJSClient::new, () -> KubeJSCommon::new);

		long now = System.currentTimeMillis();
		LOGGER.info("Looking for KubeJS plugins...");

		for (Mod mod : Platform.getMods()) {
			try {
				KubeJSPlugins.load(mod.getModId(), mod.getFilePath());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		LOGGER.info("Done in " + (System.currentTimeMillis() - now) / 1000L + " s");

		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");

		Path oldStartupFolder = KubeJSPaths.DIRECTORY.resolve("startup");

		if (Files.exists(oldStartupFolder)) {
			UtilsJS.tryIO(() -> Files.move(oldStartupFolder, KubeJSPaths.STARTUP_SCRIPTS));
		}

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::init);

		if (!CommonProperties.get().serverOnly) {
			tab = CreativeTabRegistry.create(new ResourceLocation(KubeJS.MOD_ID, KubeJS.MOD_ID), () -> new ItemStack(Items.PURPLE_DYE));
		}

		startupScriptManager.unload();
		startupScriptManager.loadFromDirectory();
		startupScriptManager.load();

		KubeJSPlugins.forEachPlugin(KubeJSPlugin::initStartup);

		KubeJSOtherEventHandler.init();
		KubeJSWorldEventHandler.init();
		KubeJSPlayerEventHandler.init();
		KubeJSEntityEventHandler.init();
		KubeJSBlockEventHandler.init();
		KubeJSItemEventHandler.init();
		KubeJSFluidEventHandler.init();
		KubeJSServerEventHandler.init();
		KubeJSRecipeEventHandler.init();

		PROXY.init();
	}

	public static void loadScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}

		final String pathPrefix = path;

		UtilsJS.tryIO(() -> Files.walk(dir, 10).filter(Files::isRegularFile).forEach(file -> {
			String fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');

			if (fileName.endsWith(".js")) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName));
			}
		}));
	}

	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory() {
		return Platform.getGameFolder();
	}

	public static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory())) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}

	public static void verifyFilePath(File file) throws IOException {
		verifyFilePath(file.toPath());
	}

	public void setup() {
		UtilsJS.init();
		KubeJSNet.init();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
		// FIXME: Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(KubeJS.MOD_ID, "flat"), FlatChunkGeneratorKJS.CODEC);
		//KubeJSRegistries.chunkGenerators().register(new ResourceLocation(KubeJS.MOD_ID, "flat"), () -> FlatChunkGeneratorKJS.CODEC);
	}

	public void loadComplete() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::afterInit);
		ScriptsLoadedEvent.EVENT.invoker().run();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
		UtilsJS.postModificationEvents();
	}
}