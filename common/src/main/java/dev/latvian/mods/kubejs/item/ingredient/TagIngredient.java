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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class TagIngredient extends Ingredient {
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

	public record ContextualResult(Context context, Collection<Holder<Item>> holders) {
	}

	public static Context context = Context.EMPTY;

	public static void resetContext() {
		context = Context.EMPTY;
	}

	public static TagIngredient ofTag(String tag) {
		return new TagIngredient(Tags.item(UtilsJS.getMCID(tag))).validateTag();
	}

	public static TagIngredient ofTagFromNetwork(FriendlyByteBuf buf) {
		return ofTag(buf.readUtf());
	}

	public static TagIngredient ofTagFromJson(JsonObject json) {
		return ofTag(json.get("tag").getAsString());
	}

	public final TagKey<Item> tag;
	private ContextualResult cachedResult;

	private TagIngredient(TagKey<Item> tag) {
		super(Stream.empty());
		this.tag = tag;
	}

	private TagIngredient validateTag() {
		if (RecipeJS.itemErrors && context.isEmpty(tag)) {
			throw new RecipeExceptionJS("Tag %s doesn't contain any items!".formatted(this)).error();
		}

		return this;
	}

	public Collection<Holder<Item>> getHolders() {
		if (cachedResult == null || cachedResult.context() != context) {
			// results are cached depending on the current tag context
			cachedResult = new ContextualResult(context, Sets.newLinkedHashSet(context.getTag(tag)));
		}
		return cachedResult.holders();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.is(tag);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:tag");
		json.addProperty("tag", tag.location().toString());
		return json;
	}

	@Override
	public void kjs$gatherStacks(ItemStackSet set) {
		for (var holder : getHolders()) {
			set.addItem(holder.value());
		}
	}

	@Override
	public void kjs$gatherItemTypes(Set<Item> set) {
		for (var holder : getHolders()) {
			set.add(holder.value());
		}
	}

	@Override
	public ItemStack kjs$getFirst() {
		for (var holder : getHolders()) {
			return new ItemStack(holder);
		}

		return ItemStack.EMPTY;
	}
}
