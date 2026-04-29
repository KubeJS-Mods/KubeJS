package dev.latvian.mods.kubejs.script;

import java.util.concurrent.TimeUnit;

/// Daemon thread started mod initialization that handles non-blocking
/// logging and file writes from [ConsoleJS].
public class KubeJSBackgroundThread extends Thread {
	private static volatile boolean running = true;

	public KubeJSBackgroundThread() {
		super("kubejs-background-thread");
		setDaemon(true);
	}

	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}

			for (var type : ScriptType.VALUES) {
				type.console.flush(false);
			}
		}
	}

	public static void shutdown() {
		running = false;

		for (var type : ScriptType.VALUES) {
			type.console.flush(true);

			type.executor.shutdown();

			try {
				if (!type.executor.awaitTermination(3L, TimeUnit.SECONDS)) {
					type.executor.shutdownNow();
				}
			} catch (InterruptedException ignored) {
				type.executor.shutdownNow();
			}
		}
	}
}
