package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StrongNBTIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<StrongNBTIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(StrongNBTIngredient::new, StrongNBTIngredient::new);

	private final Item item;
	private final CompoundTag nbt;

	public StrongNBTIngredient(Item item, @Nullable CompoundTag nbt) {
		this.item = item;
		this.nbt = nbt;
	}

	public StrongNBTIngredient(FriendlyByteBuf buf) {
		this(KubeJSRegistries.items().byRawId(buf.readVarInt()), buf.readAnySizeNbt());
	}

	public StrongNBTIngredient(JsonObject json) {
		this.item = ShapedRecipe.itemFromJson(json);

		try {
			this.nbt = json.has("nbt") ? TagParser.parseTag(json.get("nbt").getAsString()) : null;
		} catch (CommandSyntaxException var3) {
			throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
		}
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && item == stack.getItem() && Objects.equals(nbt, stack.getTag());
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("item", item.kjs$getId());

		if (nbt != null) {
			json.addProperty("nbt", nbt.toString());
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(KubeJSRegistries.items().getRawId(item));
		buf.writeNbt(nbt);
	}
}
