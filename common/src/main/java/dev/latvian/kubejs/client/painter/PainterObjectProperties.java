package dev.latvian.kubejs.client.painter;

import dev.latvian.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.rhino.util.unit.Unit;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
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
		return tag.contains(key, NbtType.NUMBER);
	}

	public boolean hasString(String key) {
		return tag.contains(key, NbtType.STRING);
	}

	public String getString(String key, String def) {
		return has(key, NbtType.STRING) ? tag.getString(key) : def;
	}

	@Nullable
	public ResourceLocation getResourceLocation(String key, @Nullable ResourceLocation def) {
		String s = getString(key, "").trim();
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

	public int getRGB(String key, int def) {
		Tag t = tag.get(key);

		if (t instanceof StringTag) {
			return ColorWrapper.of(t.getAsString()).getRgbKJS();
		} else if (t instanceof NumericTag) {
			return ColorWrapper.of(((NumericTag) t).getAsInt()).getRgbKJS();
		}

		return def;
	}

	public int getARGB(String key, int def) {
		Tag t = tag.get(key);

		if (t instanceof StringTag) {
			return ColorWrapper.of(t.getAsString()).getArgbNormalizedKJS();
		} else if (t instanceof NumericTag) {
			return ColorWrapper.of(((NumericTag) t).getAsInt()).getArgbNormalizedKJS();
		}

		return def;
	}

	public Unit getUnit(String key, Unit def) {
		if (hasString(key)) {
			return Unit.parse(tag.getString(key), Painter.INSTANCE.unitVariables);
		} else if (hasNumber(key)) {
			return Unit.fixed(((NumericTag) tag.get(key)).getAsFloat());
		}

		return def;
	}
}
