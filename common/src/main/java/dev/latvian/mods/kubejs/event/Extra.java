package dev.latvian.mods.kubejs.event;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Extra {
	@FunctionalInterface
	public interface Transformer {
		Transformer IDENTITY = o -> o;

		@Nullable
		Object transform(Object source);
	}

	public static final Extra STRING = new Extra().transformer(Extra::toString);
	public static final Extra REQUIRES_STRING = STRING.copy().required();
	public static final Extra ID = new Extra().transformer(Extra::toResourceLocation);
	public static final Extra REQUIRES_ID = ID.copy().required();

	private static String toString(Object object) {
		if (object == null) {
			return null;
		}

		var s = object.toString();
		return s.isBlank() ? null : s;
	}

	private static ResourceLocation toResourceLocation(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof ResourceLocation rl) {
			return rl;
		}

		var s = object.toString();
		return s.isBlank() ? null : ResourceLocation.tryParse(s);
	}

	public Transformer transformer;
	public boolean identity;
	public boolean required;

	public Extra() {
		this.transformer = Transformer.IDENTITY;
		this.identity = false;
		this.required = false;
	}

	public Extra copy() {
		Extra t = new Extra();
		t.transformer = transformer;
		t.identity = identity;
		t.required = required;
		return t;
	}

	public Extra transformer(Transformer factory) {
		this.transformer = factory;
		return this;
	}

	public Extra identity() {
		this.identity = true;
		return this;
	}

	public Extra required() {
		this.required = true;
		return this;
	}
}
