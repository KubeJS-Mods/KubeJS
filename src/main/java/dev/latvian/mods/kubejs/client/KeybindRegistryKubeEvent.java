package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.List;

public class KeybindRegistryKubeEvent implements ClientKubeEvent {
	private final List<Builder> builders = new ArrayList<>();

	public Builder register(String keybindId) {
		Builder builder = new Builder(keybindId);
		builders.add(builder);
		return builder;
	}

	@HideFromJS
	public List<KubeJSKeybinds.KubeKeybind> build() {
		return builders.stream().map(Builder::create).toList();
	}

	public static class Builder {
		private final String id;
		private KeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;
		private KeyModifier keyModifier = KeyModifier.NONE;
		private InputConstants.Type inputType = InputConstants.Type.KEYSYM;
		private int keyId = 0;
		private String category = "key.categories.kubejs";

		public Builder(String id) {
			this.id = id;
		}

		public Builder keyConflictContext(KeyConflictContext keyConflictContext) {
			this.keyConflictContext = keyConflictContext;
			return this;
		}

		public Builder keyModifier(KeyModifier keyModifier) {
			this.keyModifier = keyModifier;
			return this;
		}

		public Builder inputType(InputConstants.Type inputType) {
			this.inputType = inputType;
			return this;
		}

		public Builder keyId(int keyId) {
			this.keyId = keyId;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		@HideFromJS
		public KubeJSKeybinds.KubeKeybind create() {
			return new KubeJSKeybinds.KubeKeybind(id, new KeyMapping("key.kubejs.%s".formatted(id), keyConflictContext, keyModifier, inputType, keyId, category));
		}
	}
}
