package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.util.unit.ColorUnit;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PainterObjectProperties {
	public static final Unit WHITE_COLOR = new ColorUnit(FixedUnit.of(255F), FixedUnit.of(255F), FixedUnit.of(255F), FixedUnit.of(255F));

	public final CompoundTag tag;

	public PainterObjectProperties(CompoundTag t) {
		tag = t;
	}

	public boolean hasAny(String key) {
		return tag.contains(key);
	}

	public boolean has(String key, int type) {
		return tag.contains(key, type);
	}

	public boolean hasNumber(String key) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC);
	}

	public boolean hasString(String key) {
		return tag.contains(key, Tag.TAG_STRING);
	}

	public String getString(String key, String def) {
		return has(key, Tag.TAG_STRING) ? tag.getString(key) : def;
	}

	@Nullable
	public ResourceLocation getResourceLocation(String key, @Nullable ResourceLocation def) {
		var s = getString(key, "").trim();
		return s.isEmpty() ? def : new ResourceLocation(s);
	}

	public int getInt(String key, int def) {
		return hasNumber(key) ? tag.getInt(key) : def;
	}

	public float getFloat(String key, float def) {
		return hasNumber(key) ? tag.getFloat(key) : def;
	}

	public double getDouble(String key, double def) {
		return hasNumber(key) ? tag.getDouble(key) : def;
	}

	public boolean getBoolean(String key, boolean def) {
		return hasNumber(key) ? tag.getBoolean(key) : def;
	}

	public Unit getUnit(String key, Unit def) {
		if (hasString(key)) {
			return Painter.INSTANCE.unitStorage.parse(tag.getString(key));
		} else if (hasNumber(key)) {
			return FixedUnit.of(tag.getFloat(key));
		}

		return def;
	}

	public Unit getColor(String key, Unit def) {
		if (hasString(key)) {
			var col = ColorWrapper.MAP.get(getString(key, ""));

			if (col != null) {
				var i = col.getArgbKJS();
				return new ColorUnit(FixedUnit.of((i >> 16) & 0xFF), FixedUnit.of((i >> 8) & 0xFF), FixedUnit.of(i & 0xFF), FixedUnit.of((i >> 24) & 0xFF));
			}
		}

		return getUnit(key, def);
	}
}
