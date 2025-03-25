package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class RecipeComponentType<T> {
	public static class Unit<T> extends RecipeComponentType<T> {
		private record Simple<T>(RecipeComponent<T> value) implements Function<RecipeComponentType<T>, RecipeComponent<T>> {
			@Override
			public RecipeComponent<T> apply(RecipeComponentType<T> type) {
				return value;
			}
		}

		private final RecipeComponent<T> instance;
		private MapCodec<RecipeComponent<?>> mapCodec;

		private Unit(ResourceLocation id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
			super(id);
			this.instance = instanceGetter.apply(this);
		}

		@Override
		public boolean isUnit() {
			return true;
		}

		@Override
		public final RecipeComponent<T> instance() {
			return instance;
		}

		@Override
		public MapCodec<RecipeComponent<?>> mapCodec(RecipeComponentCodecFactory.Context ctx) {
			if (mapCodec == null) {
				mapCodec = MapCodec.unit(instance);
			}

			return mapCodec;
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static class Dynamic<T> extends RecipeComponentType<T> {
		@SuppressWarnings({"rawtypes", "unchecked"})
		private record Simple(MapCodec codec) implements RecipeComponentCodecFactory {
			@Override
			public MapCodec<RecipeComponent<?>> create(Context ctx) {
				return codec;
			}
		}

		private final RecipeComponentCodecFactory factory;

		private Dynamic(ResourceLocation id, RecipeComponentCodecFactory factory) {
			super(id);
			this.factory = factory;
		}

		@Override
		public MapCodec<RecipeComponent<?>> mapCodec(RecipeComponentCodecFactory.Context ctx) {
			return factory.create(ctx);
		}
	}

	public static <T> Unit<T> unit(ResourceLocation id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
		return new Unit<>(id, instanceGetter);
	}

	public static <T> Unit<T> unit(ResourceLocation id, RecipeComponent<T> instance) {
		return new Unit<>(id, new Unit.Simple<>(instance));
	}

	public static <T> Dynamic<T> dynamic(ResourceLocation id, RecipeComponentCodecFactory<? extends RecipeComponent<?>> codecFactory) {
		return new Dynamic<>(id, codecFactory);
	}

	public static <T> Dynamic<T> dynamic(ResourceLocation id, MapCodec<? extends RecipeComponent<?>> mapCodec) {
		return new Dynamic<>(id, new Dynamic.Simple(mapCodec));
	}

	private final ResourceLocation id;

	public RecipeComponentType(ResourceLocation id) {
		this.id = id;
	}

	public final ResourceLocation id() {
		return id;
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		return obj == this || obj instanceof RecipeComponentType<?> type && id.equals(type.id);
	}

	@Override
	public final String toString() {
		return id.toString();
	}

	public boolean isUnit() {
		return false;
	}

	public RecipeComponent<T> instance() {
		throw new NullPointerException("This recipe component type is not a unit type");
	}

	public abstract MapCodec<RecipeComponent<?>> mapCodec(RecipeComponentCodecFactory.Context ctx);

	/**
	 * Creates a new {@link RecipeKey} for this component with the given name.
	 *
	 * @param name The name of the key
	 * @return The created {@link RecipeKey}
	 */
	public RecipeKey<T> key(String name, ComponentRole role) {
		return instance().key(name, role);
	}

	public RecipeKey<T> inputKey(String name) {
		return key(name, ComponentRole.INPUT);
	}

	public RecipeKey<T> outputKey(String name) {
		return key(name, ComponentRole.OUTPUT);
	}

	public RecipeKey<T> otherKey(String name) {
		return key(name, ComponentRole.OTHER);
	}
}
