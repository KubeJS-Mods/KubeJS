package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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
	public static final Extra REGISTRY = new Extra().transformer(Extra::toRegistryKey).identity();
	public static final Extra REQUIRES_REGISTRY = REGISTRY.copy().required();

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

	private static ResourceKey<? extends Registry<?>> toRegistryKey(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof ResourceKey rl) {
			return rl;
		} else if (object instanceof ResourceLocation rl) {
			return ResourceKey.createRegistryKey(rl);
		}

		var s = object.toString();
		return s.isBlank() ? null : ResourceKey.createRegistryKey(new ResourceLocation(s));
	}

	public Transformer transformer;
	public boolean identity;
	public boolean required;
	public Predicate<Object> validator;
	public Transformer toString;

	public Extra() {
		this.transformer = Transformer.IDENTITY;
		this.identity = false;
		this.required = false;
		this.validator = UtilsJS.ALWAYS_TRUE;
		this.toString = Transformer.IDENTITY;
	}

	public Extra copy() {
		Extra t = new Extra();
		t.transformer = transformer;
		t.identity = identity;
		t.required = required;
		t.validator = validator;
		t.toString = toString;
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

	public Extra validator(Predicate<Object> validator) {
		this.validator = validator;
		return this;
	}

	public Extra toString(Transformer factory) {
		this.toString = factory;
		return this;
	}
}
