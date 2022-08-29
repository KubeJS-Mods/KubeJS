package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class IgnoreNBTIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<IgnoreNBTIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(IgnoreNBTIngredient::new, IgnoreNBTIngredient::new);

	private final Item item;

	public IgnoreNBTIngredient(Item item) {
		this.item = item;
	}

	private IgnoreNBTIngredient(JsonObject json) {
		this(KubeJSRegistries.items().get(new ResourceLocation(json.get("item").getAsString())));
	}

	private IgnoreNBTIngredient(FriendlyByteBuf buf) {
		this(buf.readById(Registry.ITEM));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.getItem() == item;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("item", KubeJSRegistries.items().getId(item).toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(Registry.ITEM, item);
	}
}
