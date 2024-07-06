package dev.latvian.mods.kubejs.error;

public class UnknownRecipeTypeException extends KubeRuntimeException {
	public final String recipeType;

	public UnknownRecipeTypeException(String recipeType) {
		super("Unknown recipe type: " + recipeType);
		this.recipeType = recipeType;
	}
}