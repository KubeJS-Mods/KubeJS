package dev.latvian.kubejs;

import dev.latvian.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.kubejs.client.KubeJSClient;
import dev.latvian.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.integration.IntegrationManager;
import dev.latvian.kubejs.integration.aurora.AuroraIntegration;
import dev.latvian.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import dev.latvian.mods.aurora.Aurora;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author LatvianModder
 */
@Mod(KubeJS.MOD_ID)
public class KubeJS
{
	public static KubeJS instance;
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public final KubeJSCommon proxy;
	public static boolean nextClientHasClientMod = false;

	public static ScriptManager startupScriptManager, clientScriptManager;

	public KubeJS()
	{
		Locale.setDefault(Locale.US);
		instance = this;
		startupScriptManager = new ScriptManager(ScriptType.STARTUP);
		clientScriptManager = new ScriptManager(ScriptType.CLIENT);
		//noinspection Convert2MethodRef
		proxy = DistExecutor.runForDist(() -> () -> new KubeJSClient(), () -> () -> new KubeJSCommon());

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.addListener(KubeJSEvents::registerDocumentation);
		new KubeJSServerEventHandler().init();
		new KubeJSWorldEventHandler().init();
		new KubeJSPlayerEventHandler().init();
		new KubeJSEntityEventHandler().init();
		new KubeJSBlockEventHandler().init();
		new KubeJSItemEventHandler().init();

		File folder = getGameDirectory().resolve("kubejs").toFile();

		if (!folder.exists())
		{
			folder.mkdirs();
		}

		proxy.init(folder);

		/* FIXME: File langFile = new File(folder, "resources/lang/en_us.lang");

		if (langFile.exists() && langFile.isFile())
		{
			try (InputStream stream = new FileInputStream(langFile))
			{
				LanguageMap.inject(stream);
			}
			catch (Exception ex)
			{
			}
		}
		 */

		File startupFolder = new File(folder, "startup");

		if (!startupFolder.exists())
		{
			startupFolder.mkdirs();

			try
			{
				try (PrintWriter scriptsJsonWriter = new PrintWriter(new FileWriter(new File(startupFolder, "scripts.json"))))
				{
					scriptsJsonWriter.println("{");
					scriptsJsonWriter.println("	\"scripts\": [");
					scriptsJsonWriter.println("		{\"file\": \"example.js\"}");
					scriptsJsonWriter.println("	]");
					scriptsJsonWriter.println("}");
				}

				try (PrintWriter exampleJsWriter = new PrintWriter(new FileWriter(new File(startupFolder, "example.js"))))
				{
					exampleJsWriter.println("log.info('Hello, World! (You will only see this line once in console, during startup)')");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		startupScriptManager.unload();

		if (new File(startupFolder, "scripts.json").exists())
		{
			try (FileReader reader = new FileReader(new File(startupFolder, "scripts.json")))
			{
				ScriptPack pack = new ScriptPack(startupScriptManager, new ScriptPackInfo("startup", reader, ""));

				for (ScriptFileInfo fileInfo : pack.info.scripts)
				{
					pack.scripts.add(new ScriptFile(pack, fileInfo, info -> new FileReader(new File(startupFolder, info.file))));
				}

				startupScriptManager.packs.put(pack.info.namespace, pack);
			}
			catch (Exception ex)
			{
			}
		}

		startupScriptManager.load();
	}

	public static String appendModId(String id)
	{
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory()
	{
		return FMLPaths.GAMEDIR.get();
	}

	public static void verifyFilePath(Path path) throws IOException
	{
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory()))
		{
			throw new IOException("You can't access files outside Minecraft directory!");
		}
	}

	public static void verifyFilePath(File file) throws IOException
	{
		verifyFilePath(file.toPath());
	}

	private void setup(FMLCommonSetupEvent event)
	{
		UtilsJS.init();
		IntegrationManager.init();
		KubeJSNet.init();

		if (ModList.get().isLoaded(Aurora.MOD_ID))
		{
			new AuroraIntegration().init();
		}

		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
	}

	private void loadComplete(FMLLoadCompleteEvent event)
	{
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
	}
}