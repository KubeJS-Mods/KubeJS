package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.unit.FixedNumberUnit;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PainterObjectStorage {
	private static final ScreenPainterObject[] NO_SCREEN_OBJECTS = new ScreenPainterObject[0];

	public final Painter painter;
	private final Map<String, PainterObject> objects = new LinkedHashMap<>();

	public PainterObjectStorage(Painter p) {
		painter = p;
	}

	@Nullable
	public PainterObject getObject(String key) {
		return objects.get(key);
	}

	public Collection<PainterObject> getObjects() {
		return objects.isEmpty() ? List.of() : objects.values();
	}

	public void handle(HolderLookup.Provider registries, CompoundTag root) {
		if (root.contains("bulk")) {
			var bulk = root.getList("bulk", Tag.TAG_COMPOUND);

			for (int i = 0; i < bulk.size(); i++) {
				handle(registries, bulk.getCompound(i));
			}

			return;
		}

		if (root.get("*") instanceof CompoundTag tag) {
			if (tag.getBoolean("remove")) {
				objects.clear();
			} else {
				for (var o : objects.values()) {
					o.update(registries, tag);
				}
			}
		}

		if (root.get("$") instanceof CompoundTag tag) {
			for (var k : tag.getAllKeys()) {
				if (tag.contains(k, Tag.TAG_ANY_NUMERIC)) {
					painter.setVariable(k, FixedNumberUnit.of(tag.getFloat(k)));
				} else {
					painter.setVariable(k, painter.unitOf(ConsoleJS.CLIENT, tag.get(k)));
				}
			}
		}

		for (var key : root.getAllKeys()) {
			if (key.equals("*") || key.equals("$")) {
				continue;
			}

			var tag = root.getCompound(key);
			var o = objects.get(key);

			if (o != null) {
				o.update(registries, tag);
			} else if (key.indexOf(' ') != -1) {
				ConsoleJS.CLIENT.error("Painter id can't contain spaces!");
			} else {
				var type = tag.getString("type");
				var o1 = painter.make(type);

				if (o1 != null) {
					o1.id = key;
					o1.parent = this;
					o1.update(registries, tag);
					objects.put(key, o1);
				} else {
					ConsoleJS.CLIENT.error("Unknown Painter type: " + type);
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

	public void remove(String id) {
		objects.remove(id);
	}
}
