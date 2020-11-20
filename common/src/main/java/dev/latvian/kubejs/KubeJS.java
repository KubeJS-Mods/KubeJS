package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.kubejs.docs.KubeJSDocs;
import dev.latvian.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.fluid.FluidRegistryEventJS;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import me.shedaniel.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
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
public class KubeJS
{
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static KubeJS instance;

	public final KubeJSCommon proxy;
	public static boolean nextClientHasClientMod = false;

	public static ScriptManager startupScriptManager, clientScriptManager;

	public KubeJS() throws Throwable
	{
		Locale.setDefault(Locale.US);

		instance = this;

		if (Files.notExists(KubeJSPaths.README))
		{
			UtilsJS.tryIO(() -> {
				List<String> list = new ArrayList<>();
				list.add("Find more info on the website: https://kubejs.latvian.dev/");
				list.add("");
				list.add("Directory information:");
				list.add("");
				list.add("assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png");
				list.add("data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json");
				list.add("");
				list.add("startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things");
				list.add("server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, and handling server events");
				list.add("client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things");
				list.add("");
				list.add("config - KubeJS config storage. This is also the only directory that scripts can access other than world directory");
				list.add("exported - Data dumps like texture atlases end up here");
				Files.write(KubeJSPaths.README, list);
			});
		}

		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");
		String proxyClass = Platform.getEnv() == EnvType.CLIENT ? "dev.latvian.kubejs.client.KubeJSClient" : "dev.latvian.kubejs.KubeJSCommon";
		proxy = (KubeJSCommon) Class.forName(proxyClass).getDeclaredConstructor().newInstance();

		KubeJSDocs.init();
		KubeJSOtherEventHandler.init();
		KubeJSWorldEventHandler.init();
		KubeJSPlayerEventHandler.init();
		KubeJSEntityEventHandler.init();
		KubeJSBlockEventHandler.init();
		KubeJSItemEventHandler.init();
		KubeJSRecipeEventHandler.init();
		KubeJSFluidEventHandler.init();
		KubeJSServerEventHandler.init();

		Path oldStartupFolder = KubeJSPaths.DIRECTORY.resolve("startup");

		if (Files.exists(oldStartupFolder))
		{
			UtilsJS.tryIO(() -> Files.move(oldStartupFolder, KubeJSPaths.STARTUP_SCRIPTS));
		}

		startupScriptManager.unload();
		startupScriptManager.loadFromDirectory();
		startupScriptManager.load();

		proxy.init();

		new BlockRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_REGISTRY);
		new ItemRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY);
		new FluidRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.FLUID_REGISTRY);
	}

	public static void loadScripts(ScriptPack pack, Path dir, String path)
	{
		if (!path.isEmpty() && !path.endsWith("/"))
		{
			path += "/";
		}

		final String pathPrefix = path;

		UtilsJS.tryIO(() -> Files.walk(dir, 10).filter(Files::isRegularFile).forEach(file -> {
			String fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');

			if (fileName.endsWith(".js"))
			{
				pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName));
			}
		}));
	}

	public static String appendModId(String id)
	{
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory()
	{
		return Platform.getGameFolder();
	}

	public static Path verifyFilePath(Path path) throws IOException
	{
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory()))
		{
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}

	public static void verifyFilePath(File file) throws IOException
	{
		verifyFilePath(file.toPath());
	}

	public void setup()
	{
		UtilsJS.init();
		KubeJSNet.init();
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
	}

	public void loadComplete()
	{
		ScriptsLoadedEvent.EVENT.invoker().run();
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
	}
}