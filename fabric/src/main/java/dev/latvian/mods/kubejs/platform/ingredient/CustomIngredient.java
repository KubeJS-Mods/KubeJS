package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class CustomIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CustomIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(CustomIngredient::new, CustomIngredient::new);

	public final Ingredient parent;
	public final Predicate<ItemStack> predicate;

	public CustomIngredient(Ingredient parent, Predicate<ItemStack> predicate) {
		this.parent = parent;
		this.predicate = predicate;
	}

	private CustomIngredient(JsonObject json) {
		parent = IngredientJS.ofJson(json.get("parent"));
		predicate = stack -> true;
	}

	private CustomIngredient(FriendlyByteBuf buf) {
		parent = IngredientJS.ofNetwork(buf);
		predicate = stack -> true;
	}

	@Override
	public boolean test(ItemStack stack) {
		return predicate.test(stack);
	}

	@Override
	public IIngredientSerializer<CustomIngredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
