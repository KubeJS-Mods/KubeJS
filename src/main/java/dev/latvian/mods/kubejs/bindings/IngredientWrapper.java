package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
	TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);

	@Info("A completely empty ingredient that will only match air")
	Ingredient none = Ingredient.EMPTY;

	@Info("An ingredient that matches everything")
	Ingredient all = WildcardIngredient.INSTANCE.toVanilla();

	@Info("Returns an ingredient of the input")
	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	@Info("Returns an ingredient of the input, with the specified count")
	static SizedIngredient of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	@HideFromJS
	static Ingredient wrap(Context cx, @Nullable Object o) {
		while (o instanceof Wrapper w) {
			o = w.unwrap();
		}

		if (o == null || o == ItemStack.EMPTY || o == Items.AIR || o == Ingredient.EMPTY) {
			return Ingredient.EMPTY;
		} else if (o instanceof IngredientSupplierKJS ingr) {
			return ingr.kjs$asIngredient();
		} else if (o instanceof TagKey<?> tag) {
			return Ingredient.of(ItemTags.create(tag.location()));
		} else if (o instanceof Pattern || o instanceof NativeRegExp) {
			var reg = RegExpKJS.wrap(o);

			if (reg != null) {
				return new RegExIngredient(reg).toVanilla();
			}

			return Ingredient.EMPTY;
		} else if (o instanceof JsonElement json) {
			return parseJson(cx, json);
		} else if (o instanceof CharSequence) {
			return parseString(RegistryAccessContainer.of(cx), o.toString());
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
				return CompoundIngredient.of(inList.toArray(new Ingredient[0]));
			}
		}

		var map = cx.optionalMapOf(o);

		if (map != null) {
			return Ingredient.CODEC.decode(JavaOps.INSTANCE, map).result().map(Pair::getFirst).orElse(Ingredient.EMPTY);
		}

		return ItemWrapper.wrap(cx, o).kjs$asIngredient();
	}

	static boolean isIngredientLike(Object from) {
		return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
	}

	static Ingredient parseJson(Context cx, JsonElement json) {
		if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
			return Ingredient.EMPTY;
		} else if (json.isJsonPrimitive()) {
			return wrap(cx, json.getAsString());
		} else {
			return Ingredient.CODEC.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst).orElseThrow();
		}
	}

	static Ingredient parseString(RegistryAccessContainer registries, String s) {
		if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
			return Ingredient.EMPTY;
		} else if (s.equals("*")) {
			return IngredientWrapper.all;
		} else {
			try {
				return read(registries, new StringReader(s));
			} catch (CommandSyntaxException e) {
				KubeJS.LOGGER.error("Failed to read ingredient from '" + s + "': " + e);
				return Ingredient.EMPTY;
			}
		}
	}

	static Ingredient read(RegistryAccessContainer registries, StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			return Ingredient.EMPTY;
		}

		return switch (reader.peek()) {
			case '-' -> {
				reader.skip();
				yield Ingredient.EMPTY;
			}
			case '*' -> {
				reader.skip();
				yield IngredientWrapper.all;
			}
			case '#' -> {
				reader.skip();
				// yield new TagIngredient(registries.cachedItemTags, ItemTags.create(ResourceLocation.read(reader))).toVanilla();
				yield Ingredient.of(ItemTags.create(ResourceLocation.read(reader)));
			}
			case '@' -> {
				reader.skip();
				yield new NamespaceIngredient(reader.readUnquotedString()).toVanilla();
			}
			case '%' -> {
				reader.skip();
				var id = ResourceLocation.read(reader);
				var group = UtilsJS.findCreativeTab(id);
				yield group == null ? Ingredient.EMPTY : new CreativeTabIngredient(group).toVanilla();
			}
			case '/' -> {
				var regex = RegExpKJS.read(reader);
				yield new RegExIngredient(regex).toVanilla();
			}
			case '[' -> {
				reader.skip();
				reader.skipWhitespace();

				if (!reader.canRead() || reader.peek() == ']') {
					yield Ingredient.EMPTY;
				}

				var ingredients = new ArrayList<Ingredient>(2);

				while (true) {
					ingredients.add(read(registries, reader));
					reader.skipWhitespace();

					if (reader.canRead() && reader.peek() == ',') {
						reader.skip();
						reader.skipWhitespace();
					} else if (!reader.canRead() || reader.peek() == ']') {
						break;
					}
				}

				reader.expect(']');
				reader.skipWhitespace();
				yield CompoundIngredient.of(ingredients.toArray(new Ingredient[0]));
			}
			default -> {
				var itemId = ResourceLocation.read(reader);
				var item = BuiltInRegistries.ITEM.get(itemId);

				var next = reader.canRead() ? reader.peek() : 0;

				if (next == '[' || next == '{') {
					var components = DataComponentWrapper.readPredicate(registries.nbt(), reader);

					if (components != DataComponentPredicate.EMPTY) {
						yield new DataComponentIngredient(HolderSet.direct(item.builtInRegistryHolder()), components, false).toVanilla();
					}
				}

				yield Ingredient.of(item);
			}
		};
	}

	@Info("""
		Checks if the passed in object is an Ingredient.
		Note that this does not mean it will not function as an Ingredient if passed to something that requests one.
		""")
	static boolean isIngredient(@Nullable Object o) {
		return o instanceof Ingredient;
	}

	static ItemStack first(Ingredient ingredient) {
		return ingredient.kjs$getFirst();
	}

	@Nullable
	static TagKey<Item> tagKeyOf(Ingredient in) {
		if (!in.isCustom() && in.getValues().length == 1 && in.getValues()[0] instanceof Ingredient.TagValue(TagKey<Item> tag)) {
			return tag;
		} else {
			return null;
		}
	}

	static boolean containsAnyTag(Ingredient in) {
		if (in.isCustom()) {
			return false;
		}

		for (var value : in.getValues()) {
			if (value instanceof Ingredient.TagValue) {
				return true;
			}
		}

		return false;
	}
}