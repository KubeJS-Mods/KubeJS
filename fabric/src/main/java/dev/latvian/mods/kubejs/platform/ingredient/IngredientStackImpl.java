package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class IngredientStackImpl extends KubeJSIngredient implements IngredientStack {
	public static final KubeJSIngredientSerializer<IngredientStackImpl> SERIALIZER = new KubeJSIngredientSerializer<>(IngredientStackImpl::new, IngredientStackImpl::new);

	private final Ingredient ingredient;
	private final int count;

	public IngredientStackImpl(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	private IngredientStackImpl(JsonObject json) {
		this.ingredient = Ingredient.fromJson(json.get("ingredient"));
		this.count = json.has("count") ? json.get("count").getAsInt() : 1;
	}

	private IngredientStackImpl(FriendlyByteBuf buf) {
		this.ingredient = Ingredient.fromNetwork(buf);
		this.count = buf.readVarInt();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && ingredient.test(stack);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
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
	public IngredientStackImpl kjs$asStack() {
		return this;
	}

	@Override
	public IngredientStackImpl kjs$withCount(int count) {
		return new IngredientStackImpl(ingredient, count);
	}

	@Override
	public List<Ingredient> kjs$unwrapStackIngredient() {
		Ingredient[] array = new Ingredient[count];
		Arrays.fill(array, ingredient);
		return Arrays.asList(array);
	}

	@Override
	public Ingredient getIngredient() {
		return ingredient;
	}

	@Override
	public int getCount() {
		return count;
	}
}
