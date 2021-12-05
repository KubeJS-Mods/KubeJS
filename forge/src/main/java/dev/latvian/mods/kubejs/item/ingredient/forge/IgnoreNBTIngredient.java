package dev.latvian.mods.kubejs.item.ingredient.forge;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class IgnoreNBTIngredient extends Ingredient {
	public static final IIngredientSerializer<IgnoreNBTIngredient> SERIALIZER = new IIngredientSerializer<>() {
		@Override
		public IgnoreNBTIngredient parse(FriendlyByteBuf buf) {
			ItemStack stack = buf.readItem();
			stack.setTag(null);
			return new IgnoreNBTIngredient(stack);
		}

		@Override
		public IgnoreNBTIngredient parse(JsonObject json) {
			ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("item").getAsString())));
			stack.setTag(null);
			return new IgnoreNBTIngredient(stack);
		}

		@Override
		public void write(FriendlyByteBuf buf, IgnoreNBTIngredient ingredient) {
			buf.writeItem(ingredient.item);
		}
	};

	private final ItemStack item;

	public IgnoreNBTIngredient(ItemStack stack) {
		super(Stream.of(new Ingredient.ItemValue(stack)));
		item = stack;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.getItem() == item.getItem();
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:ignore_nbt");
		json.addProperty("item", item.getItem().getRegistryName().toString());
		return json;
	}
}
