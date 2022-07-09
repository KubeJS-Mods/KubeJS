package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.DummyFluidItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipePlatformHelper;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.minecraft.resources.ResourceLocation;
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
		if (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR) {
			return ItemStackJS.EMPTY;
		} else if (o instanceof IngredientJS ingr) {
			return ingr;
		} else if (o instanceof FluidStackJS fluid) {
			return new DummyFluidItemStackJS(fluid);
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = UtilsJS.parseRegex(o);

			if (reg != null) {
				return new RegexIngredientJS(reg);
			}

			return ItemStackJS.EMPTY;
		} else if (o instanceof JsonElement json) {
			return ingredientFromRecipeJson(json);
		} else if (o instanceof CharSequence) {
			var s = o.toString();
			var count = 1;
			var spaceIndex = s.indexOf(' ');

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
				return ItemStackJS.EMPTY;
			} else if (s.startsWith("#")) {
				return TagIngredientJS.createTag(s.substring(1)).withCount(count);
			} else if (s.startsWith("@")) {
				return new ModIngredientJS(s.substring(1)).withCount(count);
			} else if (s.startsWith("%")) {
				var group = ItemStackJS.findGroup(s.substring(1));

				if (group == null) {
					if (RecipeJS.itemErrors) {
						throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
					}

					return ItemStackJS.EMPTY;
				}

				return new GroupIngredientJS(group).withCount(count);
			}

			var reg = UtilsJS.parseRegex(s);

			if (reg != null) {
				return new RegexIngredientJS(reg).withCount(count);
			}

			var item = KubeJSRegistries.items().get(new ResourceLocation(s));

			if (item == Items.AIR) {
				return ItemStackJS.EMPTY;
			}

			return new ItemStackJS(new ItemStack(item, count));
		} else if (o instanceof Ingredient ingr) {
			if (ingr.isEmpty()) {
				return ItemStackJS.EMPTY;
			}

			List<IngredientJS> in = new ArrayList<>();

			for (var stack : ((IngredientKJS) o).getItemsKJS()) {
				if (!stack.isEmpty()) {
					in.add(ItemStackJS.of(stack));
				}
			}

			return of(in);
		}

		List<?> list = ListJS.of(o);

		if (list != null) {
			var l = new MatchAnyIngredientJS();

			for (var o1 : list) {
				var ingredient = of(o1);

				if (ingredient != ItemStackJS.EMPTY) {
					l.ingredients.add(ingredient);
				}
			}

			return l.ingredients.isEmpty() ? ItemStackJS.EMPTY : l;
		}

		var map = MapJS.of(o);

		if (map != null) {
			IngredientJS in = ItemStackJS.EMPTY;
			var val = map.containsKey("value");

			if (map.containsKey("type")) {
				if ("forge:nbt".equals(map.get("type"))) {
					in = ItemStackJS.of(map.get("item")).withNBT(NBTUtils.toTagCompound(map.get("nbt")));
				} else {
					var json = MapJS.json(o);

					if (json == null) {
						throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o + " is not a json object").fallback();
					}

					try {
						var ingredient = RecipePlatformHelper.get().getCustomIngredient(json);
						return new CustomIngredient(ingredient, json);
					} catch (Exception ex) {
						throw new RecipeExceptionJS("Failed to parse custom ingredient (" + json.get("type") + ") from " + json + ": " + ex).fallback();
					}
				}
			} else if (val || map.containsKey("ingredient")) {
				in = of(val ? map.get("value") : map.get("ingredient"));
			} else if (map.containsKey("tag")) {
				in = TagIngredientJS.createTag(map.get("tag").toString());
			} else if (map.containsKey("item")) {
				in = ItemStackJS.of(map).removeNBT();
			} else if (map.containsKey("fluid")) {
				return new DummyFluidItemStackJS(FluidStackJS.of(map));
			}

			if (map.containsKey("count")) {
				in = in.withCount(UtilsJS.parseInt(map.get("count"), 1));
			} else if (map.containsKey("amount")) {
				in = in.withCount(UtilsJS.parseInt(map.get("amount"), 1));

				if (in instanceof IngredientStackJS is) {
					is.countKey = "amount";
				}
			}

			if (val && in instanceof IngredientStackJS is) {
				is.ingredientKey = "value";
			}

			return in;
		}

		return ItemStackJS.of(o);
	}

	static IngredientJS ingredientFromRecipeJson(JsonElement json) {
		if (json.isJsonArray()) {
			var any = new MatchAnyIngredientJS();

			for (var e : json.getAsJsonArray()) {
				any.ingredients.add(ingredientFromRecipeJson(e));
			}

			return any;
		} else if (json.isJsonPrimitive()) {
			return of(json.getAsString());
		} else if (json.isJsonObject()) {
			var o = json.getAsJsonObject();
			IngredientJS in = ItemStackJS.EMPTY;
			var val = o.has("value");

			if (o.has("type")) {
				if ("forge:nbt".equals(o.get("type").getAsString())) {
					in = ItemStackJS.of(o.get("item").getAsString()).withNBT(NBTUtils.toTagCompound(o.get("nbt")));
				} else {
					try {
						var ingredient = RecipePlatformHelper.get().getCustomIngredient(o);
						return new CustomIngredient(ingredient, o);
					} catch (Exception ex) {
						throw new RecipeExceptionJS("Failed to parse custom ingredient (" + o.get("type") + ") from " + o + ": " + ex);
					}
				}
			} else if (val || o.has("ingredient")) {
				in = ingredientFromRecipeJson(val ? o.get("value") : o.get("ingredient"));
			} else if (o.has("tag")) {
				in = TagIngredientJS.createTag(o.get("tag").getAsString());
			} else if (o.has("item")) {
				in = ItemStackJS.of(o.get("item").getAsString()).removeNBT();
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

		return ItemStackJS.EMPTY;
	}

	boolean test(ItemStackJS stack);

	default boolean testVanilla(ItemStack stack) {
		return test(new ItemStackJS(stack));
	}

	default boolean testVanillaItem(Item item) {
		return test(new ItemStackJS(new ItemStack(item)));
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

		for (var stack : ItemStackJS.getList()) {
			if (test(stack)) {
				set.add(stack.copy());
			}
		}

		return set;
	}

	default Set<Item> getVanillaItems() {
		Set<Item> set = new LinkedHashSet<>();

		for (var item : KubeJSRegistries.items()) {
			if (item != Items.AIR && testVanillaItem(item)) {
				set.add(item);
			}
		}

		return set;
	}

	default Set<String> getItemIds() {
		Set<String> ids = new LinkedHashSet<>();

		for (var item : getVanillaItems()) {
			var id = KubeJSRegistries.items().getId(item);

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
		for (var stack : getStacks()) {
			if (!stack.isEmpty()) {
				return stack.withCount(getCount());
			}
		}

		return ItemStackJS.EMPTY;
	}

	default IngredientJS withCount(int count) {
		if (count <= 0) {
			return ItemStackJS.EMPTY;
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
		var set = getStacks();

		if (set.size() == 1) {
			return set.iterator().next().toJson();
		}

		var array = new JsonArray();

		for (var stackJS : set) {
			array.add(stackJS.toJson());
		}

		return array;
	}

	default boolean anyStackMatches(IngredientJS ingredient) {
		for (var stack : getStacks()) {
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
		var count = getCount();

		if (count <= 0) {
			return Collections.singletonList(withCount(1));
		}

		List<IngredientJS> list = new ArrayList<>();

		for (var i = 0; i < count; i++) {
			list.add(withCount(1));
		}

		return list;
	}

	default Ingredient createVanillaIngredient() {
		return Ingredient.of(getStacks().stream().map(ItemStackJS::getItemStack));
	}
}