package dev.latvian.mods.kubejs.script;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KubeJSBackgroundThread extends Thread {
	public static boolean running = true;

	public KubeJSBackgroundThread() {
		super("KubeJS Background Thread");
		setDaemon(true);
	}

	@Override
	public void run() {
		var types = ScriptType.values();

		for (var type : types) {
			type.executor = Executors.newSingleThreadExecutor();
		}

		while (running) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (var type : types) {
				type.console.flush(false);
			}
		}

		for (var type : types) {
			type.console.flush(true);
			((ExecutorService) type.executor).shutdown();

			boolean b;
			try {
				b = ((ExecutorService) type.executor).awaitTermination(3L, TimeUnit.SECONDS);
			} catch (InterruptedException var3) {
				b = false;
			}

			if (!b) {
				((ExecutorService) type.executor).shutdownNow();
			}
		}
	}
}
