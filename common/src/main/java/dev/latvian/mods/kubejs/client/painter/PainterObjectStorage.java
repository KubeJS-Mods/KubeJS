package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.UnitContext;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PainterObjectStorage {
	private static final ScreenPainterObject[] NO_SCREEN_OBJECTS = new ScreenPainterObject[0];

	private final Map<String, PainterObject> objects = new LinkedHashMap<>();
	private final UnitVariables variables;

	public PainterObjectStorage(UnitVariables variables) {
		this.variables = variables;
	}

	@Nullable
	public PainterObject getObject(String key) {
		return objects.get(key);
	}

	public Collection<PainterObject> getObjects() {
		return objects.isEmpty() ? Collections.emptyList() : objects.values();
	}

	public void handle(CompoundTag root) {
		for (var key : root.getAllKeys()) {
			var tag = root.getCompound(key);

			if (key.equals("*")) {
				if (tag.getBoolean("remove")) {
					objects.clear();
				} else {
					for (var o : objects.values()) {
						o.update(tag);
					}
				}
			} else if (key.equals("$")) {
				for (var k : tag.getAllKeys()) {
					if (tag.contains(k, Tag.TAG_ANY_NUMERIC)) {
						variables.getVariables().set(k, FixedNumberUnit.of(tag.getFloat(k)));
					} else {
						variables.getVariables().set(k, UnitContext.DEFAULT.parse(tag.getString(k)));
					}
				}
			} else {
				var o = objects.get(key);

				if (o != null) {
					o.update(tag);
				} else if (key.indexOf(' ') != -1) {
					ConsoleJS.CLIENT.error("Painter id can't contain spaces!");
				} else {
					var type = tag.getString("type");
					var o1 = PainterRegistry.make(type);

					if (o1 != null) {
						o1.id = key;
						o1.setRemoveListener(this::remove);
						o1.init(variables);
						o1.update(tag);
						objects.put(key, o1);
					} else {
						ConsoleJS.CLIENT.error("Unknown Painter type: " + type);
					}
				}
			}
		}
	}

	public void clear() {
		objects.clear();
	}

	public ScreenPainterObject[] createScreenObjects() {
		return objects.isEmpty() ? NO_SCREEN_OBJECTS : objects.values().stream().filter(o -> o instanceof ScreenPainterObject).map(o -> (ScreenPainterObject) o).toArray(ScreenPainterObject[]::new);
	}

	public void remove(PainterObject painterObject) {
		objects.remove(painterObject.id);
	}
}
