package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public abstract class PainterObject implements SpecialEquality {
	public String id = "";
	public Unit visible = FixedBooleanUnit.TRUE;
	public Consumer<PainterObject> removeListener;

	public PainterObject id(String i) {
		id = i;
		return this;
	}

	@HideFromJS
	public void setRemoveListener(Consumer<PainterObject> listener) {
		removeListener = listener;
	}

	protected void load(PainterObjectProperties properties) {
		visible = properties.getUnit("visible", visible);
	}

	@HideFromJS
	public void init(UnitVariables variables) {}

	public final void update(CompoundTag tag) {
		if (tag.getBoolean("remove")) {
			if (removeListener != null) {
				removeListener.accept(this);
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
	public boolean specialEquals(Object o, boolean shallow) {
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
