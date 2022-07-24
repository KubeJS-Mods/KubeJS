package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;
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
	private ContextualResult cachedResult;

	private TagIngredientJS(String t) {
		tag = Tags.item(UtilsJS.getMCID(t));
	}

	public ResourceLocation getTag() {
		return tag.location();
	}

	public Collection<Holder<Item>> getHolders() {
		if (cachedResult == null || cachedResult.context != context) {
			// results are cached depending on the current tag context
			cachedResult = new ContextualResult(context, Sets.newLinkedHashSet(context.getTag(tag)));
		}
		return cachedResult.holders();
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return context.areTagsBound() ? stack.is(tag) : getHolders().contains(stack.getItem().builtInRegistryHolder());
	}

	@Override
	public boolean testItem(Item item) {
		if (item == Items.AIR) {
			return false;
		}
		var holder = item.builtInRegistryHolder();
		return context.areTagsBound() ? holder.is(tag) : getHolders().contains(holder);
	}

	@Override
	public void gatherStacks(ItemStackSet set) {
		for (var holder : getHolders()) {
			set.addItem(holder.value());
		}
	}

	@Override
	public void gatherItemTypes(Set<Item> set) {
		for (var holder : getHolders()) {
			set.add(holder.value());
		}
	}

	@Override
	public ItemStack getFirst() {
		for (var holder : getHolders()) {
			return new ItemStack(holder);
		}

		return ItemStack.EMPTY;
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

	public record ContextualResult(Context context, Collection<Holder<Item>> holders) {
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
					return manager.tags().getOrDefault(tag.location(), Set.of());
				}
			};
		}
	}
}