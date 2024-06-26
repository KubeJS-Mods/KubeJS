package dev.latvian.mods.kubejs.script;

import java.io.IOException;
import java.nio.file.Files;

public class KubeJSFileWatcherThread extends Thread {
	public final ScriptType scriptType;
	public final ScriptFile[] files;
	public final Runnable reload;

	public KubeJSFileWatcherThread(ScriptType scriptType, ScriptFile[] files, Runnable reload) {
		super("KubeJS File Watcher");
		setDaemon(true);
		this.scriptType = scriptType;
		this.files = files;
		this.reload = reload;
	}

	@Override
	public void run() {
		scriptType.console.info("#%08X Started watching %d files".formatted(hashCode(), files.length));

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (scriptType.fileWatcherThread == this) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			boolean changed = false;

			for (var file : files) {
				try {
					var ms = Files.getLastModifiedTime(file.info.path).toMillis();

					if (file.lastModified != ms) {
						file.lastModified = ms;
						changed = true;
					}
				} catch (IOException ex) {
				}
			}

			if (changed) {
				scriptType.console.info("#%08X File change detected, reloading scripts...".formatted(hashCode()));
				reload.run();
				return;
			}
		}
	}
}
