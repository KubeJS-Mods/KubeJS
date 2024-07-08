package dev.latvian.mods.kubejs.recipe.schema;

import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface RecipeComponentFactory {
	RecipeComponent<?> readComponent(RegistryAccessContainer registries, RecipeSchemaStorage storage, StringReader reader) throws Exception;

	static RecipeComponentFactory readOneComponent(Function<RecipeComponent<?>, RecipeComponent<?>> factory) {
		return (registries, storage, reader) -> {
			reader.skipWhitespace();
			reader.expect('<');
			reader.skipWhitespace();
			var component = storage.readComponent(registries, reader);
			reader.skipWhitespace();
			reader.expect('>');
			return factory.apply(component);
		};
	}

	static RecipeComponentFactory readTwoComponents(BiFunction<RecipeComponent<?>, RecipeComponent<?>, RecipeComponent<?>> factory) {
		return (registries, storage, reader) -> {
			reader.skipWhitespace();
			reader.expect('<');
			reader.skipWhitespace();
			var key = storage.readComponent(registries, reader);
			reader.skipWhitespace();
			reader.expect(',');
			reader.skipWhitespace();
			var component = storage.readComponent(registries, reader);
			reader.skipWhitespace();
			reader.expect('>');
			return factory.apply(key, component);
		};
	}
}
