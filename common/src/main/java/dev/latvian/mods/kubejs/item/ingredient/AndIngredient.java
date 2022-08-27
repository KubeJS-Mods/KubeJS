package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class AndIngredient extends Ingredient {
	public static Ingredient ofList(List<Ingredient> list) {
		return list.isEmpty() ? EMPTY : list.size() == 1 ? list.get(0) : new AndIngredient(list.toArray(OrIngredient.EMPTY_ARRAY));
	}

	public final Ingredient[] ingredients;

	public AndIngredient(Ingredient[] ingredients) {
		super(Stream.empty());
		this.ingredients = ingredients;
	}

	public AndIngredient(FriendlyByteBuf buf) {
		super(Stream.empty());
		this.ingredients = new Ingredient[buf.readVarInt()];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = Ingredient.fromNetwork(buf);
		}
	}

	public AndIngredient(JsonObject json) {
		super(Stream.empty());
		var array = json.get("ingredients").getAsJsonArray();
		this.ingredients = new Ingredient[array.size()];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = Ingredient.fromJson(array.get(i));
		}
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack != null) {
			for (var in : ingredients) {
				if (!in.test(stack)) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:and");

		JsonArray array = new JsonArray();

		for (var in : ingredients) {
			array.add(in.toJson());
		}

		json.add("ingredients", array);
		return json;
	}

	@Override
	public Ingredient kjs$and(Ingredient ingredient) {
		if (ingredient != Ingredient.EMPTY) {
			Ingredient[] in = new Ingredient[ingredients.length + 1];
			System.arraycopy(ingredients, 0, in, 0, ingredients.length);
			in[ingredients.length] = ingredient;
			return new AndIngredient(in);
		}

		return this;
	}
}
