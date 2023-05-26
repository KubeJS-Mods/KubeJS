package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface ListJS {
	@Nullable
	static List<?> of(@Nullable Object o) {
		if (o instanceof List<?> l) {
			return l;
		} else if (o instanceof Iterable<?> itr) {
			var list = new ArrayList<>(itr instanceof Collection<?> c ? c.size() : 4);

			for (Object o1 : itr) {
				list.add(o1);
			}

			return list;
		}

		return ofArray(o);
	}

	static List<?> orEmpty(@Nullable Object o) {
		var l = of(o);
		return l == null ? Collections.emptyList() : l;
	}

	static List<?> orSelf(@Nullable Object o) {
		var l = of(o);

		if (l != null) {
			return l;
		}

		var list = new ArrayList<>(1);

		if (o != null) {
			list.add(o);
		}

		return list;
	}

	@Nullable
	static List<?> ofArray(@Nullable Object array) {
		if (array instanceof Object[]) {
			return new ArrayList<>(Arrays.asList((Object[]) array));
		} else if (array instanceof int[]) {
			return ListJS.of((int[]) array);
		} else if (array instanceof byte[]) {
			return ListJS.of((byte[]) array);
		} else if (array instanceof short[]) {
			return ListJS.of((short[]) array);
		} else if (array instanceof long[]) {
			return ListJS.of((long[]) array);
		} else if (array instanceof float[]) {
			return ListJS.of((float[]) array);
		} else if (array instanceof double[]) {
			return ListJS.of((double[]) array);
		} else if (array instanceof char[]) {
			return ListJS.of((char[]) array);
		} else if (array != null && array.getClass().isArray()) {
			var length = Array.getLength(array);
			var list = new ArrayList<>(length);

			for (var i = 0; i < length; i++) {
				list.add(Array.get(array, i));
			}

			return list;
		}

		return null;
	}

	static List<Byte> of(byte[] array) {
		var list = new ArrayList<Byte>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Short> of(short[] array) {
		var list = new ArrayList<Short>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Integer> of(int[] array) {
		var list = new ArrayList<Integer>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Long> of(long[] array) {
		var list = new ArrayList<Long>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Float> of(float[] array) {
		var list = new ArrayList<Float>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Double> of(double[] array) {
		var list = new ArrayList<Double>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	static List<Character> of(char[] array) {
		var list = new ArrayList<Character>(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	@Nullable
	static Set<?> ofSet(@Nullable Object o) {
		if (o instanceof Set<?> s) {
			return s;
		} else if (o instanceof Collection<?> c) {
			return new LinkedHashSet<>(c);
		} else if (o instanceof Iterable<?> itr) {
			var set = new HashSet<>();

			for (Object o1 : itr) {
				set.add(o1);
			}

			return set;
		}

		var list = of(o);
		return list == null ? null : new LinkedHashSet<>(list);
	}

	@Nullable
	static JsonArray json(@Nullable Object array) {
		if (array instanceof JsonArray arr) {
			return arr;
		} else if (array instanceof CharSequence) {
			try {
				return JsonIO.GSON.fromJson(array.toString(), JsonArray.class);
			} catch (Exception ex) {
				return null;
			}
		} else if (array instanceof Iterable<?> itr) {
			JsonArray json = new JsonArray();

			for (Object o1 : itr) {
				json.add(JsonIO.of(o1));
			}

			return json;
		}

		return null;
	}
}