package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class NotIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<NotIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(NotIngredient::new, NotIngredient::new);

	public final Ingredient ingredient;

	public NotIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public NotIngredient(FriendlyByteBuf buf) {
		this(IngredientJS.ofNetwork(buf));
	}

	public NotIngredient(JsonObject json) {
		this(IngredientJS.ofJson(json.get("ingredient")));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !ingredient.test(stack);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.add("ingredient", ingredient.toJson());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		ingredient.toNetwork(buf);
	}

	@Override
	public Ingredient kjs$not() {
		return ingredient;
	}
}
