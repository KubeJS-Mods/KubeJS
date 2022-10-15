package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class OrIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<OrIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(OrIngredient::new, OrIngredient::new);

	public final Ingredient[] ingredients;

	public OrIngredient(Ingredient[] ingredients) {
		this.ingredients = ingredients;
	}

	public OrIngredient(FriendlyByteBuf buf) {
		this.ingredients = new Ingredient[buf.readVarInt()];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = IngredientJS.ofNetwork(buf);
		}
	}

	public OrIngredient(JsonObject json) {
		var array = json.get("ingredients").getAsJsonArray();
		this.ingredients = new Ingredient[array.size()];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = IngredientJS.ofJson(array.get(i));
		}
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack != null) {
			for (var in : ingredients) {
				if (in.test(stack)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void dissolve() {
		if (this.itemStacks == null) {
			this.itemStacks = Arrays.stream(this.ingredients).flatMap(in -> Arrays.stream(in.getItems())).distinct().toArray(ItemStack[]::new);
		}
	}

	@Override
	public void toJson(JsonObject json) {
		JsonArray array = new JsonArray();

		for (var in : ingredients) {
			array.add(in.toJson());
		}

		json.add("ingredients", array);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(ingredients.length);

		for (var in : ingredients) {
			in.toNetwork(buf);
		}
	}

	@Override
	public Ingredient kjs$or(Ingredient ingredient) {
		if (ingredient != Ingredient.EMPTY) {
			Ingredient[] in = new Ingredient[ingredients.length + 1];
			System.arraycopy(ingredients, 0, in, 0, ingredients.length);
			in[ingredients.length] = ingredient;
			return new OrIngredient(in);
		}

		return this;
	}
}
