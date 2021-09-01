package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.UnboundItemStackJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IngredientJS extends JsonSerializable, WrappedJS, Copyable {
	static IngredientJS of(@Nullable Object o) {
		if (o instanceof Wrapper) {
			o = ((Wrapper) o).unwrap();
		}

		if (o == null) {
			return EmptyItemStackJS.INSTANCE;
		} else if (o instanceof IngredientJS) {
			return (IngredientJS) o;
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			Pattern reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegexIngredientJS(reg);
			}

			return EmptyItemStackJS.INSTANCE;
		} else if (o instanceof JsonElement) {
			return ingredientFromRecipeJson((JsonElement) o);
		} else if (o instanceof CharSequence) {
			String s = o.toString();
			int count = 1;
			int spaceIndex = s.indexOf(' ');

			if (spaceIndex >= 2 && s.indexOf('x') == spaceIndex - 1) {
				count = Integer.parseInt(s.substring(0, spaceIndex - 1));
				s = s.substring(spaceIndex + 1);
			}

			if (RecipeJS.itemErrors && count <= 0) {
				throw new RecipeExceptionJS("Invalid count!").error();
			}

			if (s.equals("*")) {
				return MatchAllIngredientJS.INSTANCE.withCount(count);
			} else if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
				return EmptyItemStackJS.INSTANCE;
			} else if (s.startsWith("#")) {
				return TagIngredientJS.createTag(s.substring(1)).withCount(count);
			} else if (s.startsWith("@")) {
				return new ModIngredientJS(s.substring(1)).withCount(count);
			} else if (s.startsWith("%")) {
				CreativeModeTab group = ItemStackJS.findGroup(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return EmptyItemStackJS.INSTANCE;
				}

				return new GroupIngredientJS(group).withCount(count);
			}

			Pattern reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegexIngredientJS(reg).withCount(count);
			}

			return new UnboundItemStackJS(new ResourceLocation(s)).withCount(count);
		} else if (o instanceof Ingredient) {
			if (((Ingredient) o).isEmpty()) {
				return EmptyItemStackJS.INSTANCE;
			}

			List<IngredientJS> in = new ArrayList<>();

			for (ItemStack stack : ((Ingredient) o).getItems()) {
				if (!stack.isEmpty()) {
					in.add(ItemStackJS.of(stack));
				}
			}

			return of(in);
		}

		List<Object> list = ListJS.of(o);

		if (list != null) {
			MatchAnyIngredientJS l = new MatchAnyIngredientJS();

			for (Object o1 : list) {
				IngredientJS ingredient = of(o1);

				if (ingredient != EmptyItemStackJS.INSTANCE) {
					l.ingredients.add(ingredient);
				}
			}

			return l.ingredients.isEmpty() ? EmptyItemStackJS.INSTANCE : l;
		}

		MapJS map = MapJS.of(o);

		if (map != null) {
			IngredientJS in = EmptyItemStackJS.INSTANCE;
			boolean val = map.containsKey("value");

			if (map.containsKey("type")) {
				JsonObject json = map.toJson();

				try {
					Ingredient ingredient = getCustomIngredient(json);
					return new CustomIngredient(ingredient, json);
				} catch (Exception ex) {
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + json.get("type") + ") from " + json + ": " + ex).fallback();
				}
			} else if (val || map.containsKey("ingredient")) {
				in = of(val ? map.get("value") : map.get("ingredient"));
			} else if (map.containsKey("tag")) {
				in = TagIngredientJS.createTag(map.get("tag").toString());
			} else if (map.containsKey("item")) {
				in = ItemStackJS.of(map);
			}

			if (map.containsKey("count")) {
				in = in.withCount(UtilsJS.parseInt(map.get("count"), 1));
			} else if (map.containsKey("amount")) {
				in = in.withCount(UtilsJS.parseInt(map.get("amount"), 1));

				if (in instanceof IngredientStackJS) {
					((IngredientStackJS) in).countKey = "amount";
				}
			}

			if (val && in instanceof IngredientStackJS) {
				((IngredientStackJS) in).ingredientKey = "value";
			}

			return in;
		}

		return ItemStackJS.of(o);
	}

	@ExpectPlatform
	static Ingredient getCustomIngredient(JsonObject object) {
		throw new AssertionError();
	}

	static IngredientJS ingredientFromRecipeJson(JsonElement json) {
		if (json.isJsonArray()) {
			MatchAnyIngredientJS any = new MatchAnyIngredientJS();

			for (JsonElement e : json.getAsJsonArray()) {
				any.ingredients.add(ingredientFromRecipeJson(e));
			}

			return any;
		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			JsonObject o = json.getAsJsonObject();
			IngredientJS in = EmptyItemStackJS.INSTANCE;
			boolean val = o.has("value");

			if (o.has("type")) {
				try {
					Ingredient ingredient = getCustomIngredient(o);
					return new CustomIngredient(ingredient, o);
				} catch (Exception ex) {
					throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o.get("type") + ") from " + o + ": " + ex);
				}
			} else if (val || o.has("ingredient")) {
				in = ingredientFromRecipeJson(val ? o.get("value") : o.get("ingredient"));
			} else if (o.has("tag")) {
				in = TagIngredientJS.createTag(o.get("tag").getAsString());
			} else if (o.has("item")) {
				in = ItemStackJS.of(o.get("item").getAsString());
			}

			if (o.has("count")) {
				in = in.withCount(o.get("count").getAsInt());
			} else if (o.has("amount")) {
				in = in.withCount(o.get("amount").getAsInt());

				if (in instanceof IngredientStackJS) {
					((IngredientStackJS) in).countKey = "amount";
				}
			}

			if (val && in instanceof IngredientStackJS) {
				((IngredientStackJS) in).ingredientKey = "value";
			}

			return in;
		}

		return EmptyItemStackJS.INSTANCE;
	}

	boolean test(ItemStackJS stack);

	default boolean testVanilla(ItemStack stack) {
		return test(new BoundItemStackJS(stack));
	}

	default boolean testVanillaItem(Item item) {
		return test(new UnboundItemStackJS(KubeJSRegistries.items().getId(item)));
	}

	default Predicate<ItemStack> getVanillaPredicate() {
		return new VanillaPredicate(this);
	}

	default boolean isEmpty() {
		return getFirst().isEmpty();
	}

	default boolean isInvalidRecipeIngredient() {
		return false;
	}

	default Set<ItemStackJS> getStacks() {
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ItemStackJS.getList()) {
			if (test(stack)) {
				set.add(stack.copy());
			}
		}

		return set;
	}

	default Set<Item> getVanillaItems() {
		Set<Item> set = new LinkedHashSet<>();

		for (Item item : KubeJSRegistries.items()) {
			if (item != Items.AIR && testVanillaItem(item)) {
				set.add(item);
			}
		}

		return set;
	}

	default Set<String> getItemIds() {
		Set<String> ids = new LinkedHashSet<>();

		for (Item item : getVanillaItems()) {
			ResourceLocation id = KubeJSRegistries.items().getId(item);

			if (id != null) {
				ids.add(id.toString());
			}
		}

		return ids;
	}

	default IngredientJS filter(IngredientJS filter) {
		return new FilteredIngredientJS(this, filter);
	}

	default IngredientJS not() {
		return new NotIngredientJS(this);
	}

	default ItemStackJS getFirst() {
		for (ItemStackJS stack : getStacks()) {
			if (!stack.isEmpty()) {
				return stack.withCount(getCount());
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	default IngredientJS withCount(int count) {
		if (count <= 0) {
			return EmptyItemStackJS.INSTANCE;
		}

		return count == 1 ? copy() : new IngredientStackJS(copy(), count);
	}

	default IngredientJS x(int c) {
		return withCount(c);
	}

	@Override
	default IngredientJS copy() {
		return this;
	}

	default int getCount() {
		return 1;
	}

	@Override
	default JsonElement toJson() {
		Set<ItemStackJS> set = getStacks();

		if (set.size() == 1) {
			return set.iterator().next().toJson();
		}

		JsonArray array = new JsonArray();

		for (ItemStackJS stackJS : set) {
			array.add(stackJS.toJson());
		}

		return array;
	}

	default boolean anyStackMatches(IngredientJS ingredient) {
		for (ItemStackJS stack : getStacks()) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	default IngredientStackJS asIngredientStack() {
		return new IngredientStackJS(withCount(1), getCount());
	}

	default List<IngredientJS> unwrapStackIngredient() {
		int count = getCount();

		if (count <= 0) {
			return Collections.singletonList(withCount(1));
		}

		List<IngredientJS> list = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			list.add(withCount(1));
		}

		return list;
	}

	default Ingredient createVanillaIngredient() {
		return Ingredient.of(getStacks().stream().map(ItemStackJS::getItemStack));
	}
}