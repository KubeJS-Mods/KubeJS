package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientStackImpl extends KubeJSIngredient implements IngredientStack {
	public static final KubeJSIngredientSerializer<IngredientStackImpl> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("stack"), IngredientStackImpl::new, IngredientStackImpl::new);

	private final Ingredient ingredient;
	private final int count;

	public IngredientStackImpl(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	private IngredientStackImpl(JsonObject json) {
		this.ingredient = IngredientJS.ofJson(json.get("ingredient"));
		this.count = json.has("count") ? json.get("count").getAsInt() : 1;
	}

	private IngredientStackImpl(FriendlyByteBuf buf) {
		this.ingredient = IngredientJS.ofNetwork(buf);
		this.count = buf.readVarInt();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && ingredient.test(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		var list = new ArrayList<ItemStack>();

		for (var stack : ingredient.getItems()) {
			stack = stack.copy();
			stack.setCount(count);
			list.add(stack);
		}

		return list;
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.add("ingredient", ingredient.toJson());
		json.addProperty("count", count);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		ingredient.toNetwork(buf);
		buf.writeVarInt(count);
	}

	@Override
	public Ingredient getIngredient() {
		return ingredient;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	@SuppressWarnings("NonExtendableApiUsage")
	public Ingredient toVanilla() {
		return new IngredientImpl(this);
	}

	@SuppressWarnings("UnstableApiUsage")
	private static class IngredientImpl extends CustomIngredientImpl implements IngredientKJS {
		private final IngredientStackImpl ingredientStack;

		public IngredientImpl(IngredientStackImpl in) {
			super(in);
			ingredientStack = in;
		}

		@Override
		public IngredientStackImpl kjs$asStack() {
			return ingredientStack;
		}

		@Override
		public Ingredient kjs$withCount(int count) {
			return count < 1 ? Ingredient.EMPTY : count == 1 ? ingredientStack.ingredient : new IngredientStackImpl(ingredientStack.ingredient, count).toVanilla();
		}

		@Override
		public List<Ingredient> kjs$unwrapStackIngredient() {
			Ingredient[] array = new Ingredient[ingredientStack.count];
			Arrays.fill(array, ingredientStack.ingredient);
			return Arrays.asList(array);
		}
	}
}
