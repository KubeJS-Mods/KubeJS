package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@FunctionalInterface
public interface RecipeComponentFactory extends CustomJavaToJsWrapper {
	record Simple(RecipeComponent<?> component) implements RecipeComponentFactory {
		@Override
		public RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args) {
			return component;
		}
	}

	record Dynamic(DynamicRecipeComponent component) implements RecipeComponentFactory {
		@Override
		@Nullable
		public RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args) {
			return component.factory().create(cx, scope, args);
		}
	}

	class RecipeComponentFactoryJS extends BaseFunction {
		private final RecipeComponentFactory factory;

		public RecipeComponentFactoryJS(RecipeComponentFactory factory) {
			this.factory = factory;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			var map = args.length == 0 ? Map.of() : MapJS.of(args[0]);
			var component = factory.create(cx, scope, (Map) map);

			if (component == null) {
				throw new RuntimeException("Invalid dynamic recipe component arguments: " + map);
			}

			return component;
		}
	}

	@Nullable
	RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args);

	@Override
	default Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new RecipeComponentFactoryJS(this);
	}
}
