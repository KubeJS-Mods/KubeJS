package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.AtlasTextureObject;
import dev.latvian.mods.kubejs.client.painter.screen.GradientObject;
import dev.latvian.mods.kubejs.client.painter.screen.ItemObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.RectangleObject;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenGroup;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.client.painter.screen.TextObject;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.MutableNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<String, PainterFactory> objectRegistry;
	private final PainterObjectStorage storage;
	private ScreenPainterObject[] screenObjects;
	public final UnitContext unitContext;
	private final VariableSet variables;
	public final MutableNumberUnit deltaUnit;
	public final MutableNumberUnit screenWidthUnit;
	public final MutableNumberUnit screenHeightUnit;
	public final MutableNumberUnit mouseXUnit;
	public final MutableNumberUnit mouseYUnit;

	public Painter() {
		lock = new Object();
		objectRegistry = new HashMap<>();
		storage = new PainterObjectStorage(this);
		screenObjects = null;
		unitContext = UnitContext.DEFAULT.sub();
		variables = new VariableSet();
		deltaUnit = variables.setMutable("$D", 1D);
		variables.set("$SX", 0D);
		variables.set("$SY", 0D);
		screenWidthUnit = variables.setMutable("$SW", 1D);
		screenHeightUnit = variables.setMutable("$SH", 1D);
		mouseXUnit = variables.setMutable("$MX", 0D);
		mouseYUnit = variables.setMutable("$MY", 0D);

		// Legacy
		variables.set("$delta", deltaUnit);
		variables.set("$screenW", screenWidthUnit);
		variables.set("$screenH", screenHeightUnit);
		variables.set("$mouseX", mouseXUnit);
		variables.set("$mouseY", mouseYUnit);
	}

	public Unit unitOf(Context cx, Object o) {
		return unitOf(ConsoleJS.getCurrent(cx), o);
	}

	public Unit unitOf(ConsoleJS console, Object o) {
		if (o instanceof Unit unit) {
			return unit;
		} else if (o instanceof Number number) {
			return FixedNumberUnit.of(number.floatValue());
		}

		try {
			if (o instanceof String) {
				return unitContext.parse(o.toString());
			} else if (o instanceof StringTag tag) {
				return unitContext.parse(tag.getAsString());
			}
		} catch (Exception ex) {
			console.error("Failed to parse Unit: " + ex);
		}

		return FixedNumberUnit.ZERO;
	}

	@HideFromJS
	public void registerObject(String name, PainterFactory supplier) {
		objectRegistry.put(name, supplier);
	}

	public void registerBuiltinObjects() {
		registerObject("screen_group", ScreenGroup::new);
		registerObject("rectangle", RectangleObject::new);
		registerObject("text", TextObject::new);
		registerObject("atlas_texture", AtlasTextureObject::new);
		registerObject("gradient", GradientObject::new);
		registerObject("item", ItemObject::new);
	}

	@Nullable
	public PainterObject make(String type) {
		var supplier = objectRegistry.get(type);
		return supplier == null ? null : supplier.create(this);
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

			if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
				ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
			}
		}
	}

	public void clear() {
		synchronized (lock) {
			storage.clear();
			screenObjects = null;

			if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
				ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
			}
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

	public void inGameScreenDraw(GuiGraphics graphics, float delta) {
		var mc = Minecraft.getInstance();

		if (mc.player == null || mc.options.renderDebug || mc.screen != null) {
			return;
		}

		if (!ClientEvents.PAINT_SCREEN.hasListeners() && getScreenObjects().length == 0) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		//RenderSystem.disableLighting();

		var event = new PaintScreenEventJS(mc, graphics, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(event.width / 2D);
		mouseYUnit.set(event.height / 2D);
		ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);

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

	public void guiScreenDraw(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		var mc = Minecraft.getInstance();

		if (mc.player == null) {
			return;
		}

		if (!ClientEvents.PAINT_SCREEN.hasListeners() && getScreenObjects().length == 0) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		//RenderSystem.disableLighting();

		var event = new PaintScreenEventJS(mc, screen, graphics, mouseX, mouseY, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(mouseX);
		mouseYUnit.set(mouseY);

		event.resetShaderColor();
		ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);

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
