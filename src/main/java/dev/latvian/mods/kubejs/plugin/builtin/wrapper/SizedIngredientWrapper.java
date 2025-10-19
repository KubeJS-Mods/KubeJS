package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
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
	private static SizedIngredient wrapTrivial(Context cx, Object from) {
		return switch (from) {
			case SizedIngredient s -> s;
			case Ingredient ingredient -> ingredient.kjs$asStack();
			case ItemStack stack -> Ingredient.of(stack.kjs$withCount(1)).kjs$withCount(stack.getCount());
			case ItemLike item -> Ingredient.of(item).kjs$asStack();
			case null, default -> null;
		};

	}

	@HideFromJS
	static DataResult<SizedIngredient> wrapResult(Context cx, Object from) {
		var trivial = wrapTrivial(cx, from);
		if (trivial != null) {
			return DataResult.success(trivial);
		}

		if (from instanceof CharSequence) {
			try {
				return read(cx, new StringReader(from.toString()));
			} catch (Exception ex) {
				return DataResult.error(() -> "Error parsing sized ingredient: " + ex);
			}
		}

		return IngredientWrapper.wrapResult(cx, from).map(IngredientKJS::kjs$asStack);
	}

	@HideFromJS
	static SizedIngredient wrap(Context cx, Object from) {
		var trivial = wrapTrivial(cx, from);
		if (trivial != null) {
			return trivial;
		}

		return wrapResult(cx, from)
			.getOrThrow(error -> new KubeRuntimeException("Failed to read ingredient from %s: %s".formatted(from, error))
				.source(SourceLine.of(cx)));
	}

	@HideFromJS
	static DataResult<SizedIngredient> read(Context cx, StringReader reader) throws CommandSyntaxException {
		int count;

		if (StringReader.isAllowedNumber(reader.peek())) {
			count = Mth.ceil(reader.readDouble());

			reader.skipWhitespace();
			reader.expect('x');
			reader.skipWhitespace();

			if (count < 1) {
				return DataResult.error(() -> "SizedIngredient count smaller than 1 is not allowed!");
			}
		} else {
			count = 1;
		}

		return IngredientWrapper.read(cx, reader).map(ingredient -> ingredient.kjs$withCount(count));
	}
}