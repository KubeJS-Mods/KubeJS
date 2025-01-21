package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class KubeJSKeybinds {
	private static final Map<String, KubeKeybind> registeredKeybinds = new HashMap<>();
	private static final Set<KubeKeybindState> listeningKeybinds = new HashSet<>();

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
				if (listeningKeybind.keyDown) {
					ClientEvents.KEY_UP.post(new ClientPlayerKubeEvent(client.player));
				}
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

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof KubeKeybindState that)) return false;
			return Objects.equals(keybind.keybindId, that.keybind.keybindId);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(keybind.keybindId);
		}
	}
}
