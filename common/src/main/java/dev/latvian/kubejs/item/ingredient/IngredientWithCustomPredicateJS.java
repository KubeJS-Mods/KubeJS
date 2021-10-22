package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class IngredientWithCustomPredicateJS implements IngredientJS {
	public final UUID uuid;
	public final IngredientJS ingredient;
	public final Predicate<ItemStack> predicate;

	public IngredientWithCustomPredicateJS(@Nullable UUID id, IngredientJS i, Predicate<ItemStack> p) {
		uuid = id;
		ingredient = i;
		predicate = p;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return ingredient.test(stack) && predicate.test(stack.getItemStack());
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return ingredient.testVanilla(stack) && predicate.test(stack);
	}

	@Override
	public boolean testVanillaItem(Item i) {
		return ingredient.testVanillaItem(i);
	}

	@Override
	public JsonElement toJson() {
		if (uuid != null) {
			JsonObject json = new JsonObject();
			json.addProperty("type", "kubejs:custom_predicate");
			json.add("ingredient", ingredient.toJson());
			json.addProperty("uuid", uuid.toString());
			return json;
		}

		return ingredient.toJson();
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		return ingredient.getStacks();
	}

	@Override
	public Set<Item> getVanillaItems() {
		return ingredient.getVanillaItems();
	}

	@Override
	public boolean isEmpty() {
		return ingredient.isEmpty();
	}

	@Override
	public int getCount() {
		return ingredient.getCount();
	}

	@Override
	public ItemStackJS getFirst() {
		return ingredient.getFirst();
	}

	@Override
	public Predicate<ItemStack> getVanillaPredicate() {
		return ingredient.getVanillaPredicate().and(predicate);
	}

	@Override
	public boolean isInvalidRecipeIngredient() {
		return ingredient.isInvalidRecipeIngredient();
	}

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
