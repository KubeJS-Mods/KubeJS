package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class CustomIngredient implements IngredientJS
{
	private final Ingredient ingredient;
	private final JsonObject json;

	public CustomIngredient(Ingredient i, JsonObject o)
	{
		ingredient = i;
		json = o;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return ingredient.test(stack.getItemStack());
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return ingredient.test(stack);
	}

	@Override
	public boolean testVanillaItem(Item item)
	{
		return ingredient.test(new ItemStack(item));
	}

	@Override
	public JsonElement toJson()
	{
		return json;
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (int i : ingredient.getStackingIds())
		{
			set.add(ItemStackJS.of(StackedContents.fromStackingIndex(i)));
		}

		return set;
	}

	@Override
	public Set<Item> getVanillaItems()
	{
		Set<Item> set = new LinkedHashSet<>();

		for (int i : ingredient.getStackingIds())
		{
			Item item = Item.byId(i);

			if (item != Items.AIR)
			{
				set.add(item);
			}
		}

		return set;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return json.toString();
	}
}
