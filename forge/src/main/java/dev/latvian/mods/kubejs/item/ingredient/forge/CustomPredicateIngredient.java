package dev.latvian.mods.kubejs.item.ingredient.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicateJS;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class CustomPredicateIngredient extends Ingredient {
	public static final IIngredientSerializer<CustomPredicateIngredient> SERIALIZER = new IIngredientSerializer<CustomPredicateIngredient>() {
		@Override
		public CustomPredicateIngredient parse(FriendlyByteBuf buf) {
			return new CustomPredicateIngredient(Ingredient.fromNetwork(buf), buf.readUUID(), false);
		}

		@Override
		public CustomPredicateIngredient parse(JsonObject json) {
			return new CustomPredicateIngredient(CraftingHelper.getIngredient(json.get("ingredient")), UUID.fromString(json.get("uuid").getAsString()), true);
		}

		@Override
		public void write(FriendlyByteBuf buf, CustomPredicateIngredient ingredient) {
			ingredient.ingredient.toNetwork(buf);
			buf.writeUUID(ingredient.uuid);
		}
	};

	private final Ingredient ingredient;
	private final UUID uuid;
	private final boolean isServer;

	private CustomPredicateIngredient(Ingredient in, UUID id, boolean s) {
		super(Stream.empty());
		ingredient = in;
		uuid = id;
		isServer = s;
	}

	@Override
	@NotNull
	public ItemStack[] getItems() {
		return ((IngredientKJS) ingredient).getItemsKJS();
	}

	@Override
	@NotNull
	public IntList getStackingIds() {
		return ingredient.getStackingIds();
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		if (isServer && target != null && ingredient.test(target) && RecipeEventJS.customIngredientMap != null) {
			IngredientWithCustomPredicateJS i = RecipeEventJS.customIngredientMap.get(uuid);
			return i != null && i.predicate.test(target);
		}

		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<CustomPredicateIngredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:custom_predicate");
		json.add("ingredient", ingredient.toJson());
		json.addProperty("uuid", uuid.toString());
		return json;
	}

	@Override
	public boolean isEmpty() {
		return ingredient.isEmpty();
	}
}
