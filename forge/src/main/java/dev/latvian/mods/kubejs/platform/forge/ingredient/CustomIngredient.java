package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class CustomIngredient extends KubeJSIngredient {
	public static final Codec<CustomIngredient> CODEC = Codec.unit(CustomIngredient::new);

	private Predicate<ItemStack> predicate;

	public CustomIngredient(Predicate<ItemStack> predicate) {
		this();
		this.predicate = predicate;
	}

	private CustomIngredient() {
		super(IngredientForgeHelper.CUSTOM);
		this.predicate = stack -> false;
	}

	@Override
	public boolean test(ItemStack stack) {
		return predicate.test(stack);
	}

	@Override
	public void toJson(JsonObject json) {
	}

	@Override
	public void write(FriendlyByteBuf buf) {
	}
}
