package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.neoforged.fml.loading.FMLLoader;

public class StartupScriptManager extends ScriptManager {
	public StartupScriptManager() {
		super(ScriptType.STARTUP);
	}

	@Override
	public void loadFromDirectory() {
		super.loadFromDirectory();

		if (FMLLoader.getDist().isDedicatedServer()) {
			loadPackFromDirectory(KubeJSPaths.LOCAL_STARTUP_SCRIPTS, "local startup", true);
		}
	}
}
