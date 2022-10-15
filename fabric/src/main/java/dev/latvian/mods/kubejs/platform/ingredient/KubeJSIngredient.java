package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.IngredientExtendable;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public abstract class KubeJSIngredient extends IngredientExtendable implements IngredientKJS {
	private static final Ingredient.Value[] EMPTY_VALUES = new Ingredient.Value[0];

	public KubeJSIngredient() {
		super(Stream.empty());
		values = EMPTY_VALUES;
	}

	@Override
	public ItemStack[] getItems() {
		if (this.itemStacks == null) {
			dissolve();
		}

		return this.itemStacks;
	}

	@Override
	public void dissolve() {
		if (this.itemStacks == null) {
			ItemStackSet stacks = new ItemStackSet();

			for (var stack : ItemStackJS.getList()) {
				if (test(stack)) {
					stacks.add(stack);
				}
			}

			this.itemStacks = stacks.toArray();
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public abstract void toJson(JsonObject json);

	public abstract void write(FriendlyByteBuf buf);
}
