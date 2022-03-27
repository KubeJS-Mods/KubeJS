package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	private static final Map<String, TagIngredientJS> tagIngredientCache = new HashMap<>();

	public static TagIngredientJS createTag(String tag) {
		return tagIngredientCache.computeIfAbsent(tag, TagIngredientJS::new).validateTag();
	}

	public static void clearTagCache() {
		tagIngredientCache.clear();
	}

	private final TagKey<Item> tag;
	private Iterable<Holder<Item>> holders;

	private TagIngredientJS(String t) {
		tag = Tags.item(UtilsJS.getMCID(t));
	}

	public ResourceLocation getTag() {
		return tag.location();
	}

	public Iterable<Holder<Item>> getHolders() {
		if (holders == null) {
			holders = Registry.ITEM.getTagOrEmpty(tag);
		}
		return holders;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && stack.getItemStack().is(tag);
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && stack.is(tag);
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return item != Items.AIR && item.builtInRegistryHolder().is(tag);
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		return Util.make(new LinkedHashSet<>(), set -> {
			for (var holder : getHolders()) {
				set.add(new ItemStackJS(new ItemStack(holder)));
			}
		});
	}

	@Override
	public Set<Item> getVanillaItems() {
		return Util.make(new LinkedHashSet<>(), set -> {
			for (var holder : getHolders()) {
				set.add(holder.value());
			}
		});
	}

	@Override
	public ItemStackJS getFirst() {
		validateTag();

		for (var holder : getHolders()) {
			return new ItemStackJS(new ItemStack(holder));
		}

		return ItemStackJS.EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return !getHolders().iterator().hasNext();
	}

	@Override
	public String toString() {
		return "'#%s'".formatted(getTag());
	}

	@Override
	public JsonElement toJson() {
		var json = new JsonObject();
		json.addProperty("tag", getTag().toString());
		return json;
	}

	@Override
	public boolean anyStackMatches(IngredientJS ingredient) {
		if (ingredient instanceof TagIngredientJS tagIngredient && tag.equals(tagIngredient.tag)) {
			return true;
		}

		return IngredientJS.super.anyStackMatches(ingredient);
	}

	private TagIngredientJS validateTag() {
		if (RecipeJS.itemErrors && isEmpty()) {
			throw new RecipeExceptionJS("Tag %s doesn't contain any items!".formatted(this)).error();
		}
		return this;
	}

	@Override
	public Ingredient createVanillaIngredient() {
		return Ingredient.of(tag);
	}
}