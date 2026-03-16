package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public sealed interface RecipeComponentType<T> {
	@SuppressWarnings("ClassCanBeRecord") // no it can't (leaking this)
	final class Unit<T> implements RecipeComponentType<T> {
		private final Identifier id;
		private final RecipeComponent<T> instance;

		private record Simple<T>(RecipeComponent<T> value) implements Function<RecipeComponentType<T>, RecipeComponent<T>> {
			@Override
			public RecipeComponent<T> apply(RecipeComponentType<T> type) {
				return value;
			}
		}

		public Unit(Identifier id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
			this.id = id;
			this.instance = instanceGetter.apply(this);
		}

		public RecipeComponent<T> instance() {
			return instance;
		}

		@Override
		public MapCodec<RecipeComponent<?>> mapCodec() {
			return MapCodec.unit(instance);
		}

		@Override
		public Identifier id() {
			return id;
		}

		/**
		 * Creates a new {@link RecipeKey} for this component with the given name.
		 *
		 * @param name The name of the key
		 * @return The created {@link RecipeKey}
		 */
		public RecipeKey<T> key(String name, ComponentRole role) {
			return instance.key(name, role);
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	record Dynamic<T>(Identifier id, RecipeComponentCodecFactory factory) implements RecipeComponentType<T> {
		private record Simple(MapCodec mapCodec) implements RecipeComponentCodecFactory<RecipeComponent<?>> {
			@Override
			public MapCodec<RecipeComponent<?>> create(RecipeComponentType<?> type) {
				return mapCodec;
			}
		}


		@Override
		public MapCodec<RecipeComponent<?>> mapCodec() {
			return factory.create(this);
		}
	}

	static <T> Unit<T> unit(Identifier id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
		return new Unit<>(id, instanceGetter);
	}

	static <T> Unit<T> unit(Identifier id, RecipeComponent<T> instance) {
		return new Unit<>(id, new Unit.Simple<>(instance));
	}

	static <CT extends RecipeComponent<?>> Dynamic<?> dynamic(Identifier id, RecipeComponentCodecFactory<CT> codecFactory) {
		return new Dynamic<>(id, codecFactory);
	}

	static <CT extends RecipeComponent<?>> Dynamic<?> dynamic(Identifier id, MapCodec<CT> mapCodec) {
		return new Dynamic<>(id, new Dynamic.Simple(mapCodec));
	}

	Identifier id();

	MapCodec<RecipeComponent<?>> mapCodec();
}
