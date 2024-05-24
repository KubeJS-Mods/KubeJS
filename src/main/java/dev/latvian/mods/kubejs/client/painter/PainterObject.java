package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.nbt.CompoundTag;

public abstract class PainterObject implements SpecialEquality {
	public String id = "";
	public PainterObjectStorage parent;
	public Unit visible = FixedBooleanUnit.TRUE;

	public PainterObject id(String i) {
		id = i;
		return this;
	}

	protected void load(PainterObjectProperties properties) {
		visible = properties.getUnit("visible", visible);
	}

	public final void update(CompoundTag tag) {
		if (tag.getBoolean("remove")) {
			if (parent != null) {
				parent.remove(id);
			}
		} else {
			try {
				load(new PainterObjectProperties(tag));
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
	public boolean specialEquals(Context cx, Object o, boolean shallow) {
		if (this == o || id == o) {
			return true;
		} else if (o instanceof PainterObject po) {
			return id.equals(po.id);
		} else if (o instanceof String) {
			return id.equals(toString());
		}

		return false;
	}
}
