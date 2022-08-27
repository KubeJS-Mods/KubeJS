package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class OrIngredient extends Ingredient {
	public static final Ingredient[] EMPTY_ARRAY = new Ingredient[0];

	public static Ingredient ofList(List<Ingredient> list) {
		return list.isEmpty() ? EMPTY : list.size() == 1 ? list.get(0) : new OrIngredient(list.toArray(EMPTY_ARRAY));
	}

	public final Ingredient[] ingredients;

	public OrIngredient(Ingredient[] ingredients) {
		super(Stream.empty());
		this.ingredients = ingredients;
	}

	public OrIngredient(FriendlyByteBuf buf) {
		super(Stream.empty());
		this.ingredients = new Ingredient[buf.readVarInt()];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = Ingredient.fromNetwork(buf);
		}
	}

	public OrIngredient(JsonObject json) {
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
				if (in.test(stack)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:or");

		JsonArray array = new JsonArray();

		for (var in : ingredients) {
			array.add(in.toJson());
		}

		json.add("ingredients", array);
		return json;
	}

	@Override
	public void kjs$gatherStacks(ItemStackSet set) {
		for (var in : ingredients) {
			in.kjs$gatherStacks(set);
		}
	}

	@Override
	public void kjs$gatherItemTypes(Set<Item> set) {
		for (var in : ingredients) {
			in.kjs$gatherItemTypes(set);
		}
	}

	@Override
	public ItemStack kjs$getFirst() {
		for (var in : ingredients) {
			var stack = in.kjs$getFirst();

			if (!stack.isEmpty()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
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
