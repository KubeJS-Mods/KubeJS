package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.errorprone.annotations.DoNotCall;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import static dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper.EMPTY_INGREDIENT;

@Info("Various SizedIngredient related helper methods")
public interface SizedIngredientWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(SizedIngredient.class);

	@DoNotCall
	@Deprecated(forRemoval = true)
	@Info("Empty ingredients are no longer supported. Do not call!")
	static Object getEmpty(Context cx) {
		return EMPTY_INGREDIENT.getOrThrow(str -> new KubeRuntimeException(str).source(SourceLine.of(cx)));
	}

	@Info("An ingredient that matches everything")
	static SizedIngredient getAll(Context cx) {
		return new SizedIngredient(IngredientWrapper.getAll(cx), 1);
	}

	@Info("Returns a sized ingredient of the input")
	static SizedIngredient of(SizedIngredient ingredient) {
		return ingredient;
	}

	@Info("Returns a sized ingredient of the input")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return new SizedIngredient(ingredient, count);
	}

	static SizedIngredient ofTag(Context cx, TagKey<Item> tag, int count) {
		HolderSet<Item> set = RegistryAccessContainer.of(cx).getOrThrow(tag);
		return new SizedIngredient(Ingredient.of(set), count);
	}

	@HideFromJS
	@Nullable
	private static SizedIngredient wrapTrivial(Context cx, @Nullable Object from) {
		return switch (from) {
			case SizedIngredient s -> s;
			case Ingredient ingredient -> ingredient.kjs$asStack();
			case ItemStack stack -> Ingredient.of(stack.getItem()).kjs$asStack().ingredient().kjs$withCount(stack.getCount());
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