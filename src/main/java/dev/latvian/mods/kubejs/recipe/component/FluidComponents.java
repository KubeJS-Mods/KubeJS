package dev.latvian.mods.kubejs.recipe.component;

public interface FluidComponents {
	/* FIXME
	RecipeComponent<InputFluid> INPUT = new RecipeComponent<>() {
		@Override
		public ComponentRole role() {
			return ComponentRole.INPUT;
		}

		@Override
		public Class<?> componentClass() {
			return InputFluid.class;
		}

		@Override
		public JsonElement write(RecipeJS recipe, InputFluid value) {
			return recipe.writeInputFluid(value);
		}

		@Override
		public InputFluid read(RecipeJS recipe, Object from) {
			return recipe.readInputFluid(from);
		}

		@Override
		public boolean hasPriority(RecipeJS recipe, Object from) {
			return recipe.inputFluidHasPriority(from);
		}

		@Override
		public boolean isInput(RecipeJS recipe, InputFluid value, ReplacementMatch match) {
			return match instanceof FluidLike m && value.matches(m);
		}

		@Override
		public String checkEmpty(RecipeKey<InputFluid> key, InputFluid value) {
			if (value.kjs$isEmpty()) {
				return "Input fluid '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public String toString() {
			return "input_fluid";
		}
	};

	RecipeComponent<InputFluid[]> INPUT_ARRAY = INPUT.asArray();
	RecipeComponent<Either<InputFluid, InputItem>> INPUT_OR_ITEM = INPUT.or(ItemComponents.INPUT);
	RecipeComponent<Either<InputFluid, InputItem>[]> INPUT_OR_ITEM_ARRAY = INPUT_OR_ITEM.asArray();

	RecipeComponent<OutputFluid> OUTPUT = new RecipeComponent<>() {
		@Override
		public ComponentRole role() {
			return ComponentRole.OUTPUT;
		}

		@Override
		public Class<?> componentClass() {
			return OutputFluid.class;
		}

		@Override
		@Nullable
		public JsonElement write(RecipeJS recipe, OutputFluid value) {
			return recipe.writeOutputFluid(value);
		}

		@Override
		public OutputFluid read(RecipeJS recipe, Object from) {
			return recipe.readOutputFluid(from);
		}

		@Override
		public boolean hasPriority(RecipeJS recipe, Object from) {
			return recipe.outputFluidHasPriority(from);
		}

		@Override
		public boolean isOutput(RecipeJS recipe, OutputFluid value, ReplacementMatch match) {
			return match instanceof FluidLike m && value.matches(m);
		}

		@Override
		public String checkEmpty(RecipeKey<OutputFluid> key, OutputFluid value) {
			if (value.kjs$isEmpty()) {
				return "Output fluid '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public String toString() {
			return "output_fluid";
		}
	};

	RecipeComponent<OutputFluid[]> OUTPUT_ARRAY = OUTPUT.asArray();
	RecipeComponent<Either<OutputFluid, OutputItem>> OUTPUT_OR_ITEM = OUTPUT.or(ItemComponents.OUTPUT);
	RecipeComponent<Either<OutputFluid, OutputItem>[]> OUTPUT_OR_ITEM_ARRAY = OUTPUT_OR_ITEM.asArray();
	 */
}
