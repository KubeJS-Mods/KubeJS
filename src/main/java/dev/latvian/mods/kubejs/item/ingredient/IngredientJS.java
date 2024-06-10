package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public interface IngredientJS {
	TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);

	static Ingredient wrap(Context cx, @Nullable Object o) {
		while (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR || o == Ingredient.EMPTY) {
			return Ingredient.EMPTY;
		} else if (o instanceof IngredientSupplierKJS ingr) {
			return ingr.kjs$asIngredient();
		} else if (o instanceof TagKey<?> tag) {
			return Ingredient.of(TagKey.create(Registries.ITEM, tag.location()));
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = RegExpJS.wrap(o);

			if (reg != null) {
				return IngredientHelper.get().regex(reg);
			}

			return Ingredient.EMPTY;
		} else if (o instanceof JsonElement json) {
			return ofJson(cx, json);
		} else if (o instanceof CharSequence) {
			return parse(o.toString());
		}

		List<?> list = ListJS.of(o);

		if (list != null) {
			var inList = new ArrayList<Ingredient>(list.size());

			for (var o1 : list) {
				var ingredient = wrap(cx, o1);

				if (ingredient != Ingredient.EMPTY) {
					inList.add(ingredient);
				}
			}

			if (inList.isEmpty()) {
				return Ingredient.EMPTY;
			} else if (inList.size() == 1) {
				return inList.getFirst();
			} else {
				return IngredientHelper.get().or(inList.toArray(new Ingredient[0]));
			}
		}

		var map = MapJS.of(o);

		if (map != null) {
			return Ingredient.CODEC.decode(JavaOps.INSTANCE, map).result().map(Pair::getFirst).orElse(Ingredient.EMPTY);
		}

		return ItemStackJS.wrap(cx, o).kjs$asIngredient();
	}

	static Ingredient parse(String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
			return Ingredient.EMPTY;
		} else if (s.equals("*")) {
			return IngredientHelper.get().wildcard();
		} else if (s.startsWith("#")) {
			return IngredientHelper.get().tag(s.substring(1));
		} else if (s.startsWith("@")) {
			return IngredientHelper.get().mod(s.substring(1));
		} else if (s.startsWith("%")) {
			var group = UtilsJS.findCreativeTab(new ResourceLocation(s.substring(1)));

			if (group == null) {
				if (KubeRecipe.itemErrors) {
					throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
				}

				return Ingredient.EMPTY;
			}

			return IngredientHelper.get().creativeTab(group);
		}

		var reg = RegExpJS.wrap(s);

		if (reg != null) {
			return IngredientHelper.get().regex(reg);
		}

		var i = s.indexOf('[');

		if (i != -1) {
			KubeJS.LOGGER.warn("Ingredient with components: " + s);
			s = s.substring(0, i);
		}

		i = s.indexOf('{');

		if (i != -1) {
			KubeJS.LOGGER.warn("Ingredient with components: " + s);
			s = s.substring(0, i);
		}

		var item = RegistryInfo.ITEM.getValue(new ResourceLocation(s));

		if (item == null || item == Items.AIR) {
			return Ingredient.EMPTY;
		}

		return item.kjs$asIngredient();
	}

	static Ingredient ofJson(Context cx, JsonElement json) {
		if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
			return Ingredient.EMPTY;
		} else if (json.isJsonPrimitive()) {
			return wrap(cx, json.getAsString());
		} else {
			return Ingredient.CODEC.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst).orElseThrow();
		}
	}

	static boolean isIngredientLike(Object from) {
		return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
	}
}