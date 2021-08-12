package dev.latvian.kubejs.client.painter;

import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.CompoundTag;

public abstract class PainterObject implements SpecialEquality {
	public String id = "";
	public PainterObjectStorage parent;
	public boolean visible = true;

	public PainterObject id(String i) {
		id = i;
		return this;
	}

	protected void load(RenderObjectProperties properties) {
		visible = properties.getBoolean("visible", visible);
	}

	public final void update(CompoundTag tag) {
		if (tag.getBoolean("remove")) {
			if (parent != null) {
				parent.remove(id);
			}
		} else {
			try {
				load(new RenderObjectProperties(tag));
			} catch (Exception ex) {
				ConsoleJS.CLIENT.error("Failed to update Painter object " + id + "/" + getClass().getSimpleName() + ": " + ex);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof PainterObject && id.equals(((PainterObject) o).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		if (this == o || id == o) {
			return true;
		} else if (o instanceof PainterObject) {
			return id.equals(((PainterObject) o).id);
		} else if (o instanceof String) {
			return id.equals(toString());
		}

		return false;
	}
}
