package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.EmptyItemError;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

public interface FluidComponents {
	RecipeComponent<FluidStackJS> INPUT = new RecipeComponent<>() {
		@Override
		public String componentType() {
			return "input_fluid";
		}

		@Override
		public ComponentRole role() {
			return ComponentRole.INPUT;
		}

		@Override
		public Class<?> componentClass() {
			return FluidStackJS.class;
		}

		@Override
		public JsonElement write(RecipeJS recipe, FluidStackJS value) {
			return value == EmptyFluidStackJS.INSTANCE ? null : value.toJson();
		}

		@Override
		public FluidStackJS read(RecipeJS recipe, Object from) {
			var i = FluidStackJS.of(from);

			if (i.isEmpty()) {
				throw new EmptyItemError(from + " is not a valid fluid!", from);
			}

			return i;
		}

		@Override
		public boolean shouldRead(RecipeJS recipe, Object from) {
			return !FluidStackJS.of(from).isEmpty();
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<FluidStackJS> DEFAULT_INPUT = INPUT.optional(EmptyFluidStackJS.INSTANCE);
	RecipeComponent<FluidStackJS[]> INPUT_ARRAY = INPUT.asArray();
	RecipeComponent<Either<FluidStackJS, InputItem>> INPUT_OR_ITEM = new EitherRecipeComponent<>(INPUT, ItemComponents.INPUT);
	RecipeComponent<Either<FluidStackJS, InputItem>[]> INPUT_OR_ITEM_ARRAY = INPUT_OR_ITEM.asArray();

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
		public ComponentRole role() {
			return ComponentRole.OUTPUT;
		}

		@Override
		public String toString() {
			return componentType();
		}
	};

	RecipeComponent<FluidStackJS> DEFAULT_OUTPUT = OUTPUT.optional(EmptyFluidStackJS.INSTANCE);
	RecipeComponent<FluidStackJS[]> OUTPUT_ARRAY = OUTPUT.asArray();
	RecipeComponent<Either<FluidStackJS, OutputItem>> OUTPUT_OR_ITEM = new EitherRecipeComponent<>(OUTPUT, ItemComponents.OUTPUT);
	RecipeComponent<Either<FluidStackJS, OutputItem>[]> OUTPUT_OR_ITEM_ARRAY = OUTPUT_OR_ITEM.asArray();
}
