package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SubIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<SubIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(SubIngredient::new, SubIngredient::new);

	public final Ingredient base;
	public final Ingredient subtracted;

	public SubIngredient(Ingredient base, Ingredient subtracted) {
		this.base = base;
		this.subtracted = subtracted;
	}

	public SubIngredient(FriendlyByteBuf buf) {
		this.base = IngredientJS.ofNetwork(buf);
		this.subtracted = IngredientJS.ofNetwork(buf);
	}

	public SubIngredient(JsonObject json) {
		this.base = IngredientJS.ofJson(json.get("base"));
		this.subtracted = IngredientJS.ofJson(json.get("subtracted"));
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() && this.base.test(stack) && !this.subtracted.test(stack);
	}

	@Override
	public void dissolve() {
		if (this.itemStacks == null) {
			this.itemStacks = Arrays.stream(this.base.getItems()).filter(is -> !this.subtracted.test(is)).toArray(ItemStack[]::new);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.base.isEmpty();
	}

	// public boolean isSimple() {
	// 	return this.base.isSimple() && this.subtracted.isSimple();
	// }

	@Override
	public void toJson(JsonObject json) {
		json.add("base", this.base.toJson());
		json.add("subtracted", this.subtracted.toJson());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		this.base.toNetwork(buf);
		this.subtracted.toNetwork(buf);
	}
}
