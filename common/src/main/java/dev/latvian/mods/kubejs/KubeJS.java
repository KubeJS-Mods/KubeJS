package dev.latvian.mods.kubejs;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.utils.EnvExecutor;
import dev.latvian.mods.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import dev.latvian.mods.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.mods.kubejs.level.KubeJSWorldEventHandler;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.mods.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSBackgroundThread;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.Remapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CactusBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS {
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static KubeJS instance;
	private static Path gameDirectory;

	public static KubeJSCommon PROXY;
	public static boolean nextClientHasClientMod = false;
	public static CreativeModeTab tab = CreativeModeTab.TAB_MISC;

	public static ScriptManager startupScriptManager, clientScriptManager;

	public KubeJS() throws Throwable {
		instance = this;
		gameDirectory = Platform.getGameFolder().normalize().toAbsolutePath();
		Locale.setDefault(Locale.US);
		new KubeJSBackgroundThread().start();

		if (Files.notExists(KubeJSPaths.README)) {
			UtilsJS.tryIO(() -> Files.writeString(KubeJSPaths.README,
					"""
							Find more info on the website: https://kubejs.com/
											
							Directory information:
											
							assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png
							data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json
											
							startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things that can only happen while the game is loading (Can be reloaded with /kubejs reload_startup_scripts, but it may not work!)
							server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, loot tables, and handling server events (Can be reloaded with /reload)
							client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things (Can be reloaded with F3+T)
											
							config - KubeJS config storage. This is also the only directory that scripts can access other than world directory
							exported - Data dumps like texture atlases end up here
											
							You can find type-specific logs in logs/kubejs/ directory
							""".trim()));
		}

		PROXY = EnvExecutor.getEnvSpecific(() -> KubeJSClient::new, () -> KubeJSCommon::new);

		var now = System.currentTimeMillis();
		LOGGER.info("Looking for KubeJS plugins...");

		for (var mod : Platform.getMods()) {
			try {
				for (var path : mod.getFilePaths()) {
					KubeJSPlugins.load(path, mod.getModId());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		LOGGER.info("Done in " + (System.currentTimeMillis() - now) / 1000L + " s");

		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");

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
		KubeJSServerEventHandler.init();
		KubeJSRecipeEventHandler.init();

		PROXY.init();

		if (CommonProperties.get().disableClassFilter) {
			ConsoleJS.STARTUP.warn("Class filter is disabled!");
		}

		if (CommonProperties.get().printRemappedClasses) {
			ConsoleJS.STARTUP.info("Remapped classes:");
			var remapper = RemappingHelper.getMinecraftRemapper();

			for (var entry : remapper.classMap.entrySet()) {
				ConsoleJS.STARTUP.info("");
				ConsoleJS.STARTUP.info("- " + entry.getKey() + " => " + entry.getValue());

				if (entry.getValue().children != null) {
					for (var child : entry.getValue().children.entrySet()) {
						ConsoleJS.STARTUP.info("  " + child.getKey() + " -> " + child.getValue());
					}
				}
			}

			ConsoleJS.STARTUP.info("");
			ConsoleJS.STARTUP.info(remapper.classMap.size() + " classes");

			Class<?> testClass = CactusBlock.class;
			ConsoleJS.STARTUP.info("Test: " + testClass.getName() + " => " + remapper.getMappedClass(testClass));

			for (var field : CactusBlock.class.getDeclaredFields()) {
				ConsoleJS.STARTUP.info("  " + field.getName() + " -> " + remapper.getMappedField(testClass, field));
			}

			for (var method : CactusBlock.class.getDeclaredMethods()) {
				StringBuilder sb = new StringBuilder("  ");
				sb.append(method.getName());
				sb.append('(');
				if (method.getParameterCount() > 0) {
					for (Class<?> param : method.getParameterTypes()) {
						sb.append(Remapper.getTypeName(param.getTypeName()));
					}
				}

				sb.append(") -> ");
				sb.append(remapper.getMappedMethod(testClass, method));

				ConsoleJS.STARTUP.info(sb);
			}
		}
	}

	public static void loadScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}

		final var pathPrefix = path;

		UtilsJS.tryIO(() -> Files.walk(dir, 10).filter(Files::isRegularFile).forEach(file -> {
			var fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');

			if (fileName.endsWith(".js")) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName));
			}
		}));
	}

	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory() {
		return gameDirectory;
	}

	public static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(gameDirectory)) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}

	public void setup() {
		UtilsJS.init();
		KubeJSNet.init();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
		// KubeJSRegistries.chunkGenerators().register(new ResourceLocation(KubeJS.MOD_ID, "flat"), () -> KJSFlatLevelSource.CODEC);
	}

	public void loadComplete() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::afterInit);
		ScriptsLoadedEvent.EVENT.invoker().run();
		new StartupEventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
		UtilsJS.postModificationEvents();
	}
}