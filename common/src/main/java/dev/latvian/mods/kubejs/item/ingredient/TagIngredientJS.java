package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	// private static final Map<String, TagIngredientJS> tagIngredientCache = new HashMap<>();

	public static Context context = Context.EMPTY;

	public static TagIngredientJS createTag(String tag) {
		return new TagIngredientJS(tag).validateTag();
	}

	public static void resetContext() {
		context = Context.EMPTY;
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
			if (context.areTagsBound()) {
				// tags are bound, so we can cache the result from the registry
				holders = Registry.ITEM.getTagOrEmpty(tag);
			} else {
				// tags have not been bound yet, so we can't trust that the tag won't change
				// (for example as a result of other mods' modifications)
				return context.getTag(tag);
			}
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
		if (RecipeJS.itemErrors && context.isEmpty(tag)) {
			throw new RecipeExceptionJS("Tag %s doesn't contain any items!".formatted(this)).error();
		}
		return this;
	}

	@Override
	public Ingredient createVanillaIngredient() {
		return Ingredient.of(tag);
	}

	public interface Context {
		boolean isEmpty(TagKey<Item> tag);

		boolean areTagsBound();

		Iterable<Holder<Item>> getTag(TagKey<Item> tag);

		Context EMPTY = new Context() {
			@Override
			public boolean isEmpty(TagKey<Item> tag) {
				return true;
			}

			@Override
			public boolean areTagsBound() {
				return false;
			}

			@Override
			public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
				KubeJS.LOGGER.warn("Tried to get tag {} from an empty tag context!", tag.location());
				return List.of();
			}
		};

		Context REGISTRY = new Context() {
			@Override
			public boolean isEmpty(TagKey<Item> tag) {
				return Registry.ITEM.getTag(tag).isEmpty();
			}

			@Override
			public boolean areTagsBound() {
				return true;
			}

			@Override
			public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
				return Registry.ITEM.getTagOrEmpty(tag);
			}
		};

		static Context usingResult(TagManager.LoadResult<Item> manager) {
			return new Context() {
				@Override
				public boolean isEmpty(TagKey<Item> tag) {
					return Iterables.isEmpty(getTag(tag));
				}

				@Override
				public boolean areTagsBound() {
					return false;
				}

				@Override
				public Iterable<Holder<Item>> getTag(TagKey<Item> tag) {
					return manager.tags().getOrDefault(tag.location(), Tag.empty()).getValues();
				}
			};
		}
	}
}