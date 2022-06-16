package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PainterObjectProperties {
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

	public Unit getUnit(String key, Unit def) {
		if (hasString(key)) {
			return UnitContext.DEFAULT.parse(tag.getString(key));
		} else if (hasNumber(key)) {
			return FixedNumberUnit.of(tag.getFloat(key));
		}

		return def;
	}

	public Unit getColor(String key, Unit def) {
		if (hasString(key)) {
			var col = ColorWrapper.MAP.get(getString(key, ""));

			if (col != null) {
				return FixedColorUnit.of(col.getArgbJS(), true);
			}
		}

		return getUnit(key, def);
	}
}
