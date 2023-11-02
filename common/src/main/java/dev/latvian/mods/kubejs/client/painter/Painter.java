package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.AtlasTextureObject;
import dev.latvian.mods.kubejs.client.painter.screen.GradientObject;
import dev.latvian.mods.kubejs.client.painter.screen.ItemObject;
import dev.latvian.mods.kubejs.client.painter.screen.LineObject;
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
	public static final Painter INSTANCE = new Painter("global");

	public final String id;
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
	public final MutableNumberUnit defaultLineSizeUnit;

	public Painter(String id) {
		this.id = id;
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
		defaultLineSizeUnit = variables.setMutable("$LINE", 2.5D);

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
		registerObject("line", LineObject::new);
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
		Minecraft.getInstance().execute(() -> {
			synchronized (lock) {
				storage.handle(root);
				screenObjects = null;

				if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
					ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
				}
			}
		});
	}

	public void clear() {
		Minecraft.getInstance().execute(() -> {
			synchronized (lock) {
				storage.clear();
				screenObjects = null;

				if (ClientEvents.PAINTER_UPDATED.hasListeners()) {
					ClientEvents.PAINTER_UPDATED.post(ScriptType.CLIENT, new ClientEventJS());
				}
			}
		});
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

		RenderSystem.enableDepthTest();

		var event = new PaintScreenEventJS(mc, graphics, this, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(event.width / 2D);
		mouseYUnit.set(event.height / 2D);
		defaultLineSizeUnit.set(Math.max(2.5D, event.mc.getWindow().getWidth() / 1920D * 2.5D));

		ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && object.draw.ingame()) {
				object.preDraw(event);
			}
		}

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && object.draw.ingame()) {
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

		var event = new PaintScreenEventJS(mc, screen, graphics, this, mouseX, mouseY, delta);
		deltaUnit.set(delta);
		screenWidthUnit.set(event.width);
		screenHeightUnit.set(event.height);
		mouseXUnit.set(mouseX);
		mouseYUnit.set(mouseY);
		defaultLineSizeUnit.set(Math.max(2.5D, event.mc.getWindow().getWidth() / 1920D * 2.5D));

		event.resetShaderColor();
		ClientEvents.PAINT_SCREEN.post(ScriptType.CLIENT, event);

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && object.draw.gui()) {
				object.preDraw(event);
			}
		}

		for (var object : getScreenObjects()) {
			if (object.visible.getBoolean(event) && object.draw.gui()) {
				object.draw(event);
			}
		}
	}
}
