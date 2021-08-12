package dev.latvian.kubejs.client.painter;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.kubejs.client.painter.world.WorldPainterObject;
import dev.latvian.kubejs.net.PainterUpdatedEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class Painter {
	public static final transient Painter INSTANCE = new Painter();
	public static final Random RANDOM = new Random();
	// /kubejs painter add @p {Text:{id:'text', text:'Test', x: 30, y: 40, scale: 2.0}}

	public static final int DRAW_ALWAYS = 0;
	public static final int DRAW_INGAME = 1;
	public static final int DRAW_GUI = 2;

	public static final int CENTER = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	public static final int TOP = -1;
	public static final int BOTTOM = 1;

	private final Object lock = new Object();
	private final Map<String, Supplier<PainterObject>> objectRegistry = new HashMap<>();
	private final PainterObjectStorage storage = new PainterObjectStorage();
	private ScreenPainterObject[] screenObjects = null;
	private WorldPainterObject[] worldObjects = null;

	private Painter() {
	}

	@HideFromJS
	public void registerObject(String name, Supplier<PainterObject> supplier) {
		objectRegistry.put(name, supplier);
	}

	@Nullable
	public PainterObject make(String type) {
		Supplier<PainterObject> supplier = objectRegistry.get(type);
		return supplier == null ? null : supplier.get();
	}

	@Nullable
	public PainterObject getObject(String key) {
		synchronized (lock) {
			return storage.getObject(key);
		}
	}

	public void paint(CompoundTag root) {
		synchronized (lock) {
			storage.handle(root);
			screenObjects = null;
			worldObjects = null;
			new PainterUpdatedEventJS().post(KubeJSEvents.CLIENT_PAINTER_UPDATED);
		}
	}

	public void clear() {
		synchronized (lock) {
			storage.clear();
			screenObjects = null;
			worldObjects = null;
			new PainterUpdatedEventJS().post(KubeJSEvents.CLIENT_PAINTER_UPDATED);
		}
	}

	@HideFromJS
	public ScreenPainterObject[] getScreenObjects() {
		if (screenObjects == null) {
			synchronized (lock) {
				screenObjects = storage.createScreenObjects();
			}
		}

		return screenObjects;
	}

	@HideFromJS
	public WorldPainterObject[] getWorldObjects() {
		if (worldObjects == null) {
			synchronized (lock) {
				worldObjects = storage.createWorldObjects();
			}
		}

		return worldObjects;
	}
}
