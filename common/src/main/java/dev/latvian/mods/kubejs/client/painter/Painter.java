package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPaintEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.net.PainterUpdatedEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.MutableNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
		deltaUnit = variables.setMutable("$Delta", 1D);
		variables.set("$ScreenX", 0D);
		variables.set("$ScreenY", 0D);
		screenWidthUnit = variables.setMutable("$ScreenW", 1D);
		screenHeightUnit = variables.setMutable("$ScreenH", 1D);
		mouseXUnit = variables.setMutable("$MouseX", 0D);
		mouseYUnit = variables.setMutable("MouseY", 0D);

		// Legacy
		variables.set("$delta", deltaUnit);
		variables.set("$screenW", screenWidthUnit);
		variables.set("$screenH", screenHeightUnit);
		variables.set("$mouseX", mouseXUnit);
		variables.set("$mouseY", mouseYUnit);
	}

	public Unit unitOf(Object o) {
		if (o instanceof Unit unit) {
			return unit;
		} else if (o instanceof Number number) {
			return FixedNumberUnit.of(number.floatValue());
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

	public void inGameScreenDraw(PoseStack matrices, float delta) {
		var mc = Minecraft.getInstance();

		if (mc.player == null || mc.options.renderDebug || mc.screen != null) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		//RenderSystem.disableLighting();

		var event = new ScreenPaintEventJS(mc, matrices, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(event.width / 2D);
		mouseYUnit.set(event.height / 2D);
		event.post(KubeJSEvents.CLIENT_PAINT_SCREEN);

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && (object.draw == Painter.DRAW_ALWAYS || object.draw == Painter.DRAW_INGAME)) {
				object.preDraw(event);
			}
		}

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && (object.draw == Painter.DRAW_ALWAYS || object.draw == Painter.DRAW_INGAME)) {
				object.draw(event);
			}
		}
	}

	public void guiScreenDraw(Screen screen, PoseStack matrices, int mouseX, int mouseY, float delta) {
		var mc = Minecraft.getInstance();

		if (mc.player == null) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		//RenderSystem.disableLighting();

		var event = new ScreenPaintEventJS(mc, screen, matrices, mouseX, mouseY, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(mouseX);
		mouseYUnit.set(mouseY);

		event.resetShaderColor();
		event.post(KubeJSEvents.CLIENT_PAINT_SCREEN);

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && (object.draw == Painter.DRAW_ALWAYS || object.draw == Painter.DRAW_GUI)) {
				object.preDraw(event);
			}
		}

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && (object.draw == Painter.DRAW_ALWAYS || object.draw == Painter.DRAW_GUI)) {
				object.draw(event);
			}
		}
	}
}
