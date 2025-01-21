package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.StringUtilsWrapper;
import dev.latvian.mods.kubejs.bindings.event.KeyBindEvents;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class KubeJSKeybinds {
	private static final Map<String, KubeKey> REGISTERED = new LinkedHashMap<>();
	public static final EventTargetType<KubeKey> TARGET = EventTargetType.create(KubeKey.class).identity().transformer(KubeJSKeybinds::get0).describeType(TypeInfo.STRING);

	public static class KeyEvent extends ClientPlayerKubeEvent {
		protected final KubeKey key;

		public KeyEvent(LocalPlayer player, KubeKey key) {
			super(player);
			this.key = key;
		}
	}

	public static class TickingKeyEvent extends KeyEvent {
		public TickingKeyEvent(LocalPlayer player, KubeKey key) {
			super(player, key);
		}

		public int getTicks() {
			return key.ticksPressed;
		}
	}

	public static class KubeKey {
		public final String id;
		public transient KeyMapping mapping;
		public transient boolean down = false;
		private boolean shouldTick = false;
		public transient int ticksPressed = 0;

		public KubeKey(String id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || obj instanceof KubeKey o && Objects.equals(id, o.id);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}
	}

	public static void triggerReload() {
		for (var key : REGISTERED.values()) {
			key.shouldTick = false;
		}

		KeyBindEvents.TICK.forEachListener(ScriptType.CLIENT, container -> ((KubeKey) container.target).shouldTick = true);
	}

	public static void triggerKeyEvents(Minecraft client) {
		for (var key : REGISTERED.values()) {
			if (key.mapping == null) {
				continue;
			}

			if (client.kjs$isKeyMappingDown(key.mapping)) {
				if (!key.down) {
					key.down = true;
					KeyBindEvents.PRESSED.post(ScriptType.CLIENT, key, new KeyEvent(client.player, key));
				}

				if (key.shouldTick) {
					KeyBindEvents.TICK.post(ScriptType.CLIENT, key, new TickingKeyEvent(client.player, key));
				}

				key.ticksPressed++;
			} else {
				if (key.down) {
					key.down = false;
					KeyBindEvents.RELEASED.post(ScriptType.CLIENT, key, new TickingKeyEvent(client.player, key));
					key.ticksPressed = 0;
				}
			}
		}
	}

	@Nullable
	private static KubeKey get0(Object o) {
		return o == null ? null : getOrCreate(o.toString());
	}

	@Nullable
	public static KubeKey get(String id) {
		return REGISTERED.get(id);
	}

	public static KubeKey getOrCreate(String id) {
		return REGISTERED.computeIfAbsent(id, KubeKey::new);
	}

	public static void generateLang(LangKubeEvent event) {
		for (var key : REGISTERED.values()) {
			event.add(KubeJS.MOD_ID, "key.kubejs." + key.id, StringUtilsWrapper.toTitleCase(key.id));
		}
	}
}
