package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class EventTargetType<T> {
	@FunctionalInterface
	public interface Transformer {
		Transformer IDENTITY = o -> o;

		@Nullable
		Object transform(Object source);
	}

	public static <T> EventTargetType<T> create(Class<T> type) {
		return new EventTargetType<>(type);
	}

	public static final EventTargetType<String> STRING = create(String.class).transformer(EventTargetType::toString);
	public static final EventTargetType<ResourceLocation> ID = create(ResourceLocation.class).transformer(EventTargetType::toResourceLocation);
	public static final EventTargetType<ResourceKey<Registry<?>>> REGISTRY = Cast.to(create(ResourceKey.class).transformer(EventTargetType::toRegistryKey).identity());

	public static <T> EventTargetType<ResourceKey<T>> registryKey(ResourceKey<Registry<T>> registry, Class<?> type) {
		return Cast.to(create(ResourceKey.class).identity().transformer(o -> toKey(registry, o)).describeType(TypeInfo.of(ResourceKey.class).withParams(TypeInfo.of(type))));
	}

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

	private static ResourceKey<?> toKey(ResourceKey registry, Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof ResourceKey<?> rl) {
			return rl;
		} else if (object instanceof RegistryObjectKJS<?> wrk) {
			return wrk.kjs$getRegistryKey();
		} else if (object instanceof ResourceLocation rl) {
			return ResourceKey.create(registry, rl);
		} else {
			var s = object.toString();
			return s.isBlank() ? null : ResourceKey.create(registry, ResourceLocation.parse(s));
		}
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
		return s.isBlank() ? null : ResourceKey.createRegistryKey(ResourceLocation.parse(s));
	}

	public final Class<T> type;
	public Transformer transformer;
	public boolean identity;
	public Predicate<Object> validator;
	public Transformer toString;
	public TypeInfo describeType;

	private EventTargetType(Class<T> type) {
		this.type = type;
		this.transformer = Transformer.IDENTITY;
		this.identity = false;
		this.validator = UtilsJS.ALWAYS_TRUE;
		this.toString = Transformer.IDENTITY;
		this.describeType = TypeInfo.STRING;
	}

	public EventTargetType<T> transformer(Transformer factory) {
		this.transformer = factory;
		return this;
	}

	public EventTargetType<T> identity() {
		this.identity = true;
		return this;
	}

	public EventTargetType<T> validator(Predicate<Object> validator) {
		this.validator = validator;
		return this;
	}

	public EventTargetType<T> describeType(TypeInfo describeType) {
		this.describeType = describeType;
		return this;
	}

	public EventTargetType<T> toString(Transformer factory) {
		this.toString = factory;
		return this;
	}
}
