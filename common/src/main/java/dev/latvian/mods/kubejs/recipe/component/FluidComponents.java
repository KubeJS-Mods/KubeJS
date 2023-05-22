package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.EmptyItemError;

import java.util.List;

public interface FluidComponents {
	RecipeComponent<FluidStackJS> INPUT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "input_fluid";
		}

		@Override
		public RecipeComponentType getType() {
			return RecipeComponentType.INPUT;
		}

		@Override
		public JsonElement write(FluidStackJS value) {
			return value == EmptyFluidStackJS.INSTANCE ? null : value.toJson();
		}

		@Override
		public FluidStackJS read(Object from) {
			var i = FluidStackJS.of(from);

			if (i.isEmpty()) {
				throw new EmptyItemError(from + " is not a valid fluid!", from);
			}

			return i;
		}

		@Override
		public boolean shouldRead(Object from) {
			return !FluidStackJS.of(from).isEmpty();
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<FluidStackJS> DEFAULT_INPUT = INPUT.optional(EmptyFluidStackJS.INSTANCE);
	RecipeComponent<List<FluidStackJS>> INPUT_ARRAY = INPUT.asArray();

	RecipeComponent<FluidStackJS> OUTPUT = new RecipeComponentWithParent<>() {
		@Override
		public RecipeComponent<FluidStackJS> parentComponent() {
			return INPUT;
		}

		@Override
		public String componentType() {
			return "output_fluid";
		}

		@Override
		public RecipeComponentType getType() {
			return RecipeComponentType.OUTPUT;
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<FluidStackJS> DEFAULT_OUTPUT = OUTPUT.optional(EmptyFluidStackJS.INSTANCE);
	RecipeComponent<List<FluidStackJS>> OUTPUT_ARRAY = OUTPUT.asArray();
}
