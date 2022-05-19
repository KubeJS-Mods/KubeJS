package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.net.PainterUpdatedEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.MutableNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Painter implements UnitVariables {
	public static final Painter INSTANCE = new Painter();

	public static final int DRAW_ALWAYS = 0;
	public static final int DRAW_INGAME = 1;
	public static final int DRAW_GUI = 2;

	public static final int CENTER = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	public static final int TOP = -1;
	public static final int BOTTOM = 1;

	private final Object lock;
	private final Map<String, Supplier<PainterObject>> objectRegistry;
	private final PainterObjectStorage storage;
	private ScreenPainterObject[] screenObjects;
	public final UnitContext unitContext;
	private final VariableSet variables;
	public final MutableNumberUnit deltaUnit;
	public final MutableNumberUnit screenWidthUnit;
	public final MutableNumberUnit screenHeightUnit;
	public final MutableNumberUnit mouseXUnit;
	public final MutableNumberUnit mouseYUnit;

	private Painter() {
		lock = new Object();
		objectRegistry = new HashMap<>();
		storage = new PainterObjectStorage();
		screenObjects = null;
		unitContext = UnitContext.DEFAULT.sub();
		variables = new VariableSet();
		deltaUnit = variables.setMutable("$delta", 1D);
		screenWidthUnit = variables.setMutable("$screenW", 1D);
		screenHeightUnit = variables.setMutable("$screenH", 1D);
		mouseXUnit = variables.setMutable("$mouseX", 0D);
		mouseYUnit = variables.setMutable("$mouseY", 0D);
	}

	public Unit unitOf(Object o) {
		if (o instanceof Unit unit) {
			return unit;
		} else if (o instanceof Number number) {
			return FixedNumberUnit.ofFixed(number.floatValue());
		} else if (o instanceof String) {
			return unitContext.parse(o.toString());
		}

		return FixedNumberUnit.ZERO;
	}

	@HideFromJS
	public void registerObject(String name, Supplier<PainterObject> supplier) {
		objectRegistry.put(name, supplier);
	}

	@Nullable
	public PainterObject make(String type) {
		var supplier = objectRegistry.get(type);
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
			new PainterUpdatedEventJS().post(KubeJSEvents.CLIENT_PAINTER_UPDATED);
		}
	}

	public void clear() {
		synchronized (lock) {
			storage.clear();
			screenObjects = null;
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

	public void setVariable(String key, Unit variable) {
		variables.set(key, variable);
	}

	@Override
	public VariableSet getVariables() {
		return variables;
	}
}
