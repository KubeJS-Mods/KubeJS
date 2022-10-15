package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class WeakNBTIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<WeakNBTIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(WeakNBTIngredient::new, WeakNBTIngredient::new);

	private final Item item;
	private final CompoundTag nbt;
	private final NbtPredicate predicate;

	public WeakNBTIngredient(Item item, CompoundTag nbt) {
		this.item = item;
		this.nbt = nbt;
		this.predicate = new NbtPredicate(this.nbt);
	}

	public WeakNBTIngredient(FriendlyByteBuf buf) {
		this(KubeJSRegistries.items().byRawId(buf.readVarInt()), buf.readAnySizeNbt());
	}

	public WeakNBTIngredient(JsonObject json) {
		this.item = ShapedRecipe.itemFromJson(json);

		try {
			this.nbt = TagParser.parseTag(json.get("nbt").getAsString());
		} catch (CommandSyntaxException var3) {
			throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
		}

		this.predicate = new NbtPredicate(this.nbt);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.getItem() == item && predicate.matches(stack.getTag());
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("item", item.kjs$getId());
		json.addProperty("nbt", nbt.toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(KubeJSRegistries.items().getRawId(item));
		buf.writeNbt(nbt);
	}
}
