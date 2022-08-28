package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IngredientStack extends Ingredient {
	public final Ingredient ingredient;
	public final int count;

	public IngredientStack(Ingredient ingredient, int count) {
		super(Stream.empty());
		this.ingredient = ingredient;
		this.count = count;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && ingredient.test(stack);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:stack");
		json.add("ingredient", ingredient.toJson());
		json.addProperty("count", count);
		return json;
	}

	@Override
	public IngredientStack kjs$asStack() {
		return this;
	}

	@Override
	public Ingredient kjs$withCount(int count) {
		return new IngredientStack(ingredient, count);
	}

	@Override
	public List<Ingredient> kjs$unwrapStackIngredient() {
		Ingredient[] array = new Ingredient[count];
		Arrays.fill(array, ingredient);
		return Arrays.asList(array);
	}
}
