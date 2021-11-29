package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	private static final Map<String, TagIngredientJS> tagIngredientCache = new HashMap<>();

	public static TagIngredientJS createTag(String tag) {
		TagIngredientJS i = tagIngredientCache.computeIfAbsent(tag, TagIngredientJS::new);

		if (RecipeJS.itemErrors && i.getActualTag().getValues().isEmpty()) {
			throw new RecipeExceptionJS("Tag '#" + tag + "' doesn't contain any items!").error();
		}

		return i;
	}

	public static void clearTagCache() {
		tagIngredientCache.clear();
	}

	private final ResourceLocation tag;
	private Tag<Item> actualTag;

	private TagIngredientJS(String t) {
		tag = UtilsJS.getMCID(t);
	}

	public String getTag() {
		return tag.toString();
	}

	public Tag<Item> getActualTag() {
		if (actualTag == null) {
			actualTag = Tags.items().getTag(tag);

			if (actualTag == null) {
				actualTag = SetTag.empty();
			}
		}

		return actualTag;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && getActualTag().contains(stack.getItem());
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && getActualTag().contains(stack.getItem());
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return item != Items.AIR && getActualTag().contains(item);
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		Tag<Item> t = getActualTag();

		if (t.getValues().size() > 0) {
			Set<ItemStackJS> set = new LinkedHashSet<>();

			for (Item item : t.getValues()) {
				set.add(new ItemStackJS(new ItemStack(item)));
			}

			return set;
		}

		return Collections.emptySet();
	}

	@Override
	public Set<Item> getVanillaItems() {
		Tag<Item> t = getActualTag();

		if (t.getValues().size() > 0) {
			return new LinkedHashSet<>(t.getValues());
		}

		return Collections.emptySet();
	}

	@Override
	public ItemStackJS getFirst() {
		Tag<Item> t = getActualTag();

		if (t.getValues().size() > 0) {
			for (Item item : t.getValues()) {
				return new ItemStackJS(new ItemStack(item));
			}
		}

		throw new RecipeExceptionJS("Tag " + tag + " has no items");
	}

	@Override
	public String toString() {
		return "'#" + tag + "'";
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("tag", tag.toString());
		return json;
	}

	@Override
	public boolean anyStackMatches(IngredientJS ingredient) {
		if (ingredient instanceof TagIngredientJS && tag.equals(((TagIngredientJS) ingredient).tag)) {
			return true;
		}

		return IngredientJS.super.anyStackMatches(ingredient);
	}

	@Override
	public Ingredient createVanillaIngredient() {
		return Ingredient.of(getActualTag());
	}
}