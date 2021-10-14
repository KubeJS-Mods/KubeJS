package dev.latvian.kubejs.client.painter;

import dev.latvian.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.kubejs.client.painter.world.WorldPainterObject;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.architectury.architectury.utils.NbtType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PainterObjectStorage {
	private static final ScreenPainterObject[] NO_SCREEN_OBJECTS = new ScreenPainterObject[0];
	private static final WorldPainterObject[] NO_WORLD_OBJECTS = new WorldPainterObject[0];

	private final Map<String, PainterObject> objects = new LinkedHashMap<>();

	@Nullable
	public PainterObject getObject(String key) {
		return objects.get(key);
	}

	public Collection<PainterObject> getObjects() {
		return objects.isEmpty() ? Collections.emptyList() : objects.values();
	}

	public void handle(CompoundTag root) {
		for (String key : root.getAllKeys()) {
			CompoundTag tag = root.getCompound(key);

			if (key.equals("*")) {
				if (tag.getBoolean("remove")) {
					objects.clear();
				} else {
					for (PainterObject o : objects.values()) {
						o.update(tag);
					}
				}
			} else if (key.equals("$")) {
				for (String k : tag.getAllKeys()) {
					if (tag.contains(k, NbtType.NUMBER)) {
						Painter.INSTANCE.setVariable(k, FixedUnit.of(tag.getFloat(k)));
					} else {
						Painter.INSTANCE.setVariable(k, Painter.INSTANCE.unitStorage.parse(tag.getString(k)));
					}
				}
			} else {
				PainterObject o = objects.get(key);

				if (o != null) {
					o.update(tag);
				} else if (key.indexOf(' ') != -1) {
					ConsoleJS.CLIENT.error("Painter id can't contain spaces!");
				} else {
					String type = tag.getString("type");
					PainterObject o1 = Painter.INSTANCE.make(type);

					if (o1 != null) {
						o1.id = key;
						o1.parent = this;
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

	public WorldPainterObject[] createWorldObjects() {
		return objects.isEmpty() ? NO_WORLD_OBJECTS : objects.values().stream().filter(o -> o instanceof WorldPainterObject).map(o -> (WorldPainterObject) o).toArray(WorldPainterObject[]::new);
	}

	public void remove(String id) {
		objects.remove(id);
	}
}
