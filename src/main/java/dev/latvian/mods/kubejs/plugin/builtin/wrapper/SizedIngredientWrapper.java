package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

@Info("Various SizedIngredient related helper methods")
public interface SizedIngredientWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(SizedIngredient.class);

	@Info("A completely empty ingredient that will only match air")
	SizedIngredient empty = new SizedIngredient(Ingredient.EMPTY, 1);
	@Info("An ingredient that matches everything")
	SizedIngredient all = new SizedIngredient(IngredientWrapper.all, 1);

	@Info("Returns a sized ingredient of the input")
	static SizedIngredient of(SizedIngredient ingredient) {
		return ingredient;
	}

	@Info("Returns a sized ingredient of the input")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return new SizedIngredient(ingredient, count);
	}

	static SizedIngredient ofTag(TagKey<Item> tag, int count) {
		return SizedIngredient.of(tag, count);
	}

	@HideFromJS
	static SizedIngredient wrap(Context cx, Object from) {
		if (from instanceof SizedIngredient s) {
			return s;
		} else if (from instanceof Ingredient ingredient) {
			return ingredient.kjs$asStack();
		} else if (from instanceof ItemStack stack) {
			return Ingredient.of(stack.kjs$withCount(1)).kjs$withCount(stack.getCount());
		} else if (from instanceof ItemLike item) {
			return Ingredient.of(item).kjs$asStack();
		} else if (from instanceof CharSequence) {
			try {
				return read(cx, new StringReader(from.toString()));
			} catch (Exception ex) {
				ConsoleJS.getCurrent(cx).error("Failed to read sized ingredient from '" + from, ex);
				return empty;
			}
		}

		return IngredientWrapper.wrap(cx, from).kjs$asStack();
	}

	@HideFromJS
	static SizedIngredient read(Context cx, StringReader reader) throws CommandSyntaxException {
		int count = 1;

		if (StringReader.isAllowedNumber(reader.peek())) {
			count = Mth.ceil(reader.readDouble());
			reader.skipWhitespace();
			reader.expect('x');
			reader.skipWhitespace();

			if (count < 1) {
				throw new IllegalArgumentException("SizedIngredient count smaller than 1 is not allowed!");
			}
		}

		return IngredientWrapper.read(cx, reader).kjs$withCount(count);
	}
}