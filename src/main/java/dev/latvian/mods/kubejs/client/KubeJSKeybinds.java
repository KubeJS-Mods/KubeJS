package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubeJSKeybinds {
	private static final Map<String, KubeKeybind> registeredKeybinds = new HashMap<>();
	private static final List<KubeKeybindState> listeningKeybinds = new ArrayList<>();

	public static void triggerReload() {
		listeningKeybinds.clear();
		ClientEvents.KEY_DOWN.forEachListener(ScriptType.CLIENT, container -> {
			String target = container.target.toString();
			if (registeredKeybinds.containsKey(target)) {
				listeningKeybinds.add(new KubeKeybindState(registeredKeybinds.get(target)));
			}
		});
		ClientEvents.KEY_PRESSED.forEachListener(ScriptType.CLIENT, container -> {
			String target = container.target.toString();
			if (registeredKeybinds.containsKey(target)) {
				listeningKeybinds.add(new KubeKeybindState(registeredKeybinds.get(target)));
			}
		});
	}

	public static void triggerKeyEvents(Minecraft client) {
		for (KubeKeybindState listeningKeybind : listeningKeybinds) {
			if (client.kjs$isKeyMappingDown(listeningKeybind.keybind.keyMapping)) {
				ClientPlayerKubeEvent event = new ClientPlayerKubeEvent(client.player);
				if (!listeningKeybind.keyDown) {
					ClientEvents.KEY_PRESSED.post(event, listeningKeybind.keybind.keybindId);
				}
				listeningKeybind.keyDown = true;
				ClientEvents.KEY_DOWN.post(event, listeningKeybind.keybind.keybindId);
			} else {
				listeningKeybind.keyDown = false;
			}
		}
	}

	public static KeyMapping getKeybind(String keybindId) {
		KubeKeybind kubeKeybind = registeredKeybinds.get(keybindId);
		if (kubeKeybind == null) {
			return null;
		}
		return kubeKeybind.keyMapping;
	}

	public static void addKeybind(KubeKeybind kubeKeybind) {
		registeredKeybinds.put(kubeKeybind.keybindId, kubeKeybind);
	}

	public record KubeKeybind(String keybindId, KeyMapping keyMapping) {
	}

	private static class KubeKeybindState {
		final KubeKeybind keybind;
		boolean keyDown = false;

		private KubeKeybindState(KubeKeybind keybind) {
			this.keybind = keybind;
		}
	}
}
