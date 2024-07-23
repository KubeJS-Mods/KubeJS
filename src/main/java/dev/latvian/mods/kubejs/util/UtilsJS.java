package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.item.ItemModificationKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.type.TypeUtils;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UtilsJS {
	public static final RandomSource RANDOM = RandomSource.create();
	public static final ResourceLocation AIR_LOCATION = ResourceLocation.parse("minecraft:air");
	public static final Pattern SNAKE_CASE_SPLIT = Pattern.compile("[:_/]");
	public static final Set<String> ALWAYS_LOWER_CASE = new HashSet<>(Arrays.asList("a", "an", "the", "of", "on", "in", "and", "or", "but", "for"));
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static final Predicate<Object> ALWAYS_TRUE = o -> true;

	private static final Map<String, EntitySelector> ENTITY_SELECTOR_CACHE = new HashMap<>();
	private static final EntitySelector ALL_ENTITIES_SELECTOR = new EntitySelector(EntitySelector.INFINITE, true, false, List.of(), MinMaxBounds.Doubles.ANY, Function.identity(), null, EntitySelectorParser.ORDER_RANDOM, false, null, null, null, true);

	@FunctionalInterface
	public interface TryIO {
		void run() throws IOException;
	}

	public static void tryIO(TryIO tryIO) {
		try {
			tryIO.run();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void queueIO(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// TODO: Remove this garbage
	@Nullable
	public static Object wrap(@Nullable Object o, JSObjectType type) {
		//Primitives and already normalized objects
		if (o == null || o instanceof WrappedJS || o instanceof Number || o instanceof Character || o instanceof String || o instanceof Enum || o.getClass().isPrimitive() && !o.getClass().isArray()) {
			return o;
		} else if (o instanceof CharSequence || o instanceof ResourceLocation) {
			return o.toString();
		} else if (o instanceof EndTag || o instanceof JsonNull) {
			return null;
		} else if (o instanceof Wrapper w) {
			return wrap(w.unwrap(), type);
		} else if (o instanceof NumericTag tag) {
			return tag.getAsNumber();
		} else if (o instanceof StringTag tag) {
			return tag.getAsString();
		} else if (o instanceof Tag) {
			return o;
		}
		// Maps
		else if (o instanceof Map) {
			return o;
		}
		// Lists, Collections, Iterables, GSON Arrays
		else if (o instanceof Iterable<?> itr) {
			if (!type.checkList()) {
				return null;
			}

			var list = new ArrayList<>();

			for (var o1 : itr) {
				list.add(o1);
			}

			return list;
		}
		// Arrays (and primitive arrays are a pain)
		else if (o.getClass().isArray()) {
			if (type.checkList()) {
				return ListJS.ofArray(o);
			} else {
				return null;
			}
		}
		// GSON Primitives
		else if (o instanceof JsonPrimitive json) {
			return JsonIO.toPrimitive(json);
		}
		// GSON Objects
		else if (o instanceof JsonObject json) {
			if (!type.checkMap()) {
				return null;
			}

			var map = new HashMap<String, Object>(json.size());

			for (var entry : json.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}

			return map;
		}

		return o;
	}

	public static int parseInt(@Nullable Object object, int def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.intValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Integer.parseInt(s);
		} catch (Exception ex) {
			return def;
		}
	}

	public static long parseLong(@Nullable Object object, long def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.longValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Long.parseLong(s);
		} catch (Exception ex) {
			return def;
		}
	}

	public static double parseDouble(@Nullable Object object, double def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.doubleValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Double.parseDouble(String.valueOf(object));
		} catch (Exception ex) {
			return def;
		}
	}

	public static <T> Predicate<T> onMatchDo(Predicate<T> predicate, Consumer<T> onMatch) {
		return t -> {
			var match = predicate.test(t);
			if (match) {
				onMatch.accept(t);
			}
			return match;
		};
	}

	// TODO: We could probably make these generic for RegistryObjectBuilderTypes,
	//  so maybe look into that to allow people to modify builtin fluids, etc. as well.
	public static void postModificationEvents() {
		BlockEvents.MODIFICATION.post(ScriptType.STARTUP, new BlockModificationKubeEvent());
		ItemEvents.MODIFICATION.post(ScriptType.STARTUP, new ItemModificationKubeEvent());
	}

	public static String toMappedTypeString(Type type) {
		if (type instanceof Class<?> clz) {
			var mapped = clz.getName();
			int ld = mapped.lastIndexOf('.');
			return ld == -1 ? mapped : mapped.substring(ld + 1);
		} else if (type instanceof ParameterizedType paramType) {
			var sb = new StringBuilder();

			var owner = paramType.getOwnerType();
			if (owner != null) {
				sb.append(toMappedTypeString(owner));
				sb.append('.');
			}

			sb.append(toMappedTypeString(TypeUtils.getRawType(paramType)));

			var args = paramType.getActualTypeArguments();
			if (args.length > 0) {
				sb.append('<');
				for (var i = 0; i < args.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(toMappedTypeString(args[i]));
				}
				sb.append('>');
			}

			return sb.toString();
		} else if (type instanceof GenericArrayType arrType) {
			return toMappedTypeString(arrType.getGenericComponentType()) + "[]";
		} else if (type instanceof TypeVariable<?> typeVar) {
			var sb = new StringBuilder(typeVar.getName());
			var bounds = typeVar.getBounds();

			if (bounds.length > 0 && !(bounds.length == 1 && Object.class.equals(bounds[0]))) {
				sb.append(" extends ");
				for (var i = 0; i < bounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(bounds[i]));
				}
			}

			return sb.toString();
		} else if (type instanceof WildcardType wildcard) {
			var sb = new StringBuilder().append("?");
			var lowerBounds = wildcard.getLowerBounds();
			var upperBounds = wildcard.getUpperBounds();

			if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
				sb.append(" super ");
				for (var i = 0; i < lowerBounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(lowerBounds[i]));
				}
			} else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
				sb.append(" extends ");
				for (var i = 0; i < upperBounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(upperBounds[i]));
				}
			}

			return sb.toString();
		}

		var className = type == null ? "null" : type.getClass().getName();
		throw new IllegalArgumentException("Expected a Class, ParameterizedType, GenericArrayType, TypeVariable or WildcardType, but <" + type + "> is of type " + className);
	}

	public static String snakeCaseToCamelCase(String string) {
		if (string == null || string.isEmpty()) {
			return string;
		}

		var s = SNAKE_CASE_SPLIT.split(string, 0);

		var sb = new StringBuilder();
		var first = true;

		for (var value : s) {
			if (!value.isEmpty()) {
				if (first) {
					first = false;
					sb.append(value);
				} else {
					sb.append(Character.toUpperCase(value.charAt(0)));
					sb.append(value, 1, value.length());
				}
			}
		}

		return sb.toString();
	}

	public static String snakeCaseToTitleCase(String string) {
		StringJoiner joiner = new StringJoiner(" ");
		String[] split = string.split("_");
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			String titleCase = toTitleCase(s, i == 0);
			joiner.add(titleCase);
		}
		return joiner.toString();
	}

	public static String toTitleCase(String s) {
		return toTitleCase(s, false);
	}

	public static String toTitleCase(String s, boolean ignoreSpecial) {
		if (s.isEmpty()) {
			return "";
		} else if (!ignoreSpecial && ALWAYS_LOWER_CASE.contains(s)) {
			return s;
		} else if (s.length() == 1) {
			return s.toUpperCase();
		}

		char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String stripIdForEvent(ResourceLocation id) {
		return stripEventName(id.toString());
	}

	public static String getUniqueId(JsonElement json) {
		return getUniqueId(json, Function.identity());
	}

	public static <T> String getUniqueId(T input, Function<T, JsonElement> toJson) {
		return JsonIO.getJsonHashString(toJson.apply(input));
	}

	public static String stripEventName(String s) {
		return s.replaceAll("[/:]", ".").replace('-', '_');
	}

	public static EntitySelector entitySelector(@Nullable Object o) {
		if (o == null) {
			return ALL_ENTITIES_SELECTOR;
		} else if (o instanceof EntitySelector s) {
			return s;
		}

		String s = o.toString();

		if (s.isBlank()) {
			return ALL_ENTITIES_SELECTOR;
		}

		var sel = ENTITY_SELECTOR_CACHE.get(s);

		if (sel == null) {
			sel = ALL_ENTITIES_SELECTOR;

			try {
				sel = new EntitySelectorParser(new StringReader(s), true).parse();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		ENTITY_SELECTOR_CACHE.put(s, sel);
		return sel;
	}

	@Nullable
	public static CreativeModeTab findCreativeTab(ResourceLocation id) {
		return BuiltInRegistries.CREATIVE_MODE_TAB.get(id);
	}

	public static <T> T makeFunctionProxy(Context cx, TypeInfo targetClass, BaseFunction function) {
		return Cast.to(cx.createInterfaceAdapter(targetClass, function));
	}
}