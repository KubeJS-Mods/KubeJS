package dev.latvian.kubejs.util;

import dev.latvian.kubejs.script.ScriptType;

import java.util.concurrent.TimeUnit;

public class KubeJSBackgroundThread extends Thread {
	public static boolean running = true;

	public KubeJSBackgroundThread() {
		super("KubeJS Background Thread");
	}

	@Override
	public void run() {
		ScriptType[] types = ScriptType.values();

		while (running) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (ScriptType type : types) {
				type.console.flush();
			}
		}

		for (ScriptType type : types) {
			type.console.flush();
			type.executor.shutdown();

			boolean b;
			try {
				b = type.executor.awaitTermination(3L, TimeUnit.SECONDS);
			} catch (InterruptedException var3) {
				b = false;
			}

			if (!b) {
				type.executor.shutdownNow();
			}
		}

	}
}
