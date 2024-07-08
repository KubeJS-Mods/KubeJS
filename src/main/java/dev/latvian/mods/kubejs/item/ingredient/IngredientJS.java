package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.DataComponentWrapper;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.ingredient.TagIngredient;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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

public interface IngredientJS {
	TypeInfo TYPE_INFO = TypeInfo.of(Ingredient.class);

	static Ingredient wrap(RegistryAccessContainer registries, @Nullable Object o) {
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
			return ofJson(registries, json);
		} else if (o instanceof CharSequence) {
			return ofString(registries, o.toString());
		}

		List<?> list = ListJS.of(o);

		if (list != null) {
			var inList = new ArrayList<Ingredient>(list.size());

			for (var o1 : list) {
				var ingredient = wrap(registries, o1);

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

		var map = MapJS.of(o);

		if (map != null) {
			return Ingredient.CODEC.decode(JavaOps.INSTANCE, map).result().map(Pair::getFirst).orElse(Ingredient.EMPTY);
		}

		return ItemStackJS.wrap(registries, o).kjs$asIngredient();
	}

	static Ingredient ofString(RegistryAccessContainer registries, String s) {
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

	static Ingredient ofJson(RegistryAccessContainer registries, JsonElement json) {
		if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
			return Ingredient.EMPTY;
		} else if (json.isJsonPrimitive()) {
			return wrap(registries, json.getAsString());
		} else {
			return Ingredient.CODEC.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst).orElseThrow();
		}
	}

	static boolean isIngredientLike(Object from) {
		return from instanceof Ingredient || from instanceof SizedIngredient || from instanceof ItemStack;
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
				yield new TagIngredient(registries.cachedItemTags, ItemTags.create(ResourceLocation.read(reader))).toVanilla();
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
}