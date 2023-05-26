package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CustomIngredientWithParent extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CustomIngredientWithParent> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("custom"), CustomIngredientWithParent::new, CustomIngredientWithParent::new);

	public final Ingredient parent;
	public final Predicate<ItemStack> predicate;

	public CustomIngredientWithParent(Ingredient parent, Predicate<ItemStack> predicate) {
		this.parent = parent;
		this.predicate = predicate;
	}

	private CustomIngredientWithParent(JsonObject json) {
		parent = IngredientJS.ofJson(json.get("parent"));
		predicate = stack -> true;
	}

	private CustomIngredientWithParent(FriendlyByteBuf buf) {
		parent = IngredientJS.ofNetwork(buf);
		predicate = stack -> true;
	}

	@Override
	public boolean test(ItemStack stack) {
		return predicate.test(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		var list = new ArrayList<ItemStack>();

		for (var stack : parent.getItems()) {
			if (predicate.test(stack)) {
				list.add(stack);
			}
		}

		return list;
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
