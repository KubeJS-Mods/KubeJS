package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.util.StringReaderFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.util.Mth;

public interface NumberComponent<S, T extends Number> extends RecipeComponent<T> {
	IntRange INT = new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE, Codec.INT);
	LongRange LONG = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE, Codec.LONG);
	FloatRange FLOAT = new FloatRange(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Codec.FLOAT);
	DoubleRange DOUBLE = new DoubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Codec.DOUBLE);

	static IntRange intRange(int min, int max) {
		return INT.range(min, max);
	}

	static LongRange longRange(long min, long max) {
		return LONG.range(min, max);
	}

	static FloatRange floatRange(float min, float max) {
		return FLOAT.range(min, max);
	}

	static DoubleRange doubleRange(double min, double max) {
		return DOUBLE.range(min, max);
	}

	static <T extends Number> RecipeComponentFactory createFactory(T zero, NumberComponent<?, T> range, StringReaderFunction<T> numFunc) {
		return (registries, storage, reader) -> {
			reader.skipWhitespace();

			if (!reader.canRead() || reader.peek() != '<') {
				return range;
			}

			reader.skip();
			reader.skipWhitespace();

			T num1;

			if (reader.peek() == 'm') {
				if (!reader.readUnquotedString().equals("min")) {
					throw new IllegalStateException("Expected 'min'!");
				}

				num1 = range.min();
			} else {
				num1 = numFunc.read(reader);
			}

			reader.skipWhitespace();
			T num2 = null;

			if (reader.peek() == ',') {
				reader.skip();
				reader.skipWhitespace();

				if (reader.peek() == 'm') {
					if (!reader.readUnquotedString().equals("max")) {
						throw new IllegalStateException("Expected 'max'!");
					}

					num2 = range.max();
				} else {
					num2 = numFunc.read(reader);
				}

				reader.skipWhitespace();
			}

			reader.expect('>');

			if (num2 == null) {
				return range.range(zero, num1);
			} else if (num1.equals(range.min()) && num2.equals(range.max())) {
				return range;
			} else {
				return range.range(num1, num2);
			}
		};
	}

	RecipeComponentFactory INT_FACTORY = createFactory(0, INT, StringReader::readInt);
	RecipeComponentFactory LONG_FACTORY = createFactory(0L, LONG, StringReader::readLong);
	RecipeComponentFactory FLOAT_FACTORY = createFactory(0F, FLOAT, StringReader::readFloat);
	RecipeComponentFactory DOUBLE_FACTORY = createFactory(0D, DOUBLE, StringReader::readDouble);

	private static Number numberOf(Object from) {
		if (from instanceof Number n) {
			return n;
		} else if (from instanceof JsonPrimitive json) {
			return json.getAsNumber();
		} else if (from instanceof CharSequence) {
			return Double.parseDouble(from.toString());
		}

		throw new IllegalStateException("Expected a number!");
	}

	@Override
	default TypeInfo typeInfo() {
		return TypeInfo.NUMBER;
	}

	@Override
	default boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	T min();

	T max();

	NumberComponent<S, T> range(T min, T max);

	default NumberComponent<S, T> min(T min) {
		return range(min, max());
	}

	default NumberComponent<S, T> max(T max) {
		return range(min(), max);
	}

	default String toString(String name, T min, T max) {
		var mn = min();
		var mx = max();

		if (min.equals(mn) && max.equals(mx)) {
			return name;
		} else if (min.equals(mn)) {
			return name + "<min," + mx + ">";
		} else if (max.equals(mx)) {
			return name + "<" + mn + ",max>";
		} else {
			return name + "<" + mn + "," + mx + ">";
		}
	}

	record IntRange(Integer min, Integer max, Codec<Integer> codec) implements NumberComponent<IntRange, Integer> {
		@Override
		public Codec<Integer> codec() {
			return codec;
		}

		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.INT;
		}

		@Override
		public Integer wrap(Context cx, KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).intValue(), min, max);
		}

		@Override
		public IntRange range(Integer min, Integer max) {
			return new IntRange(min, max, Codec.intRange(min, max));
		}

		@Override
		public String toString() {
			return toString("int", Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
	}

	record LongRange(Long min, Long max, Codec<Long> codec) implements NumberComponent<LongRange, Long> {
		@Override
		public Codec<Long> codec() {
			return codec;
		}

		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.LONG;
		}

		@Override
		public Long wrap(Context cx, KubeRecipe recipe, Object from) {
			long val = NumberComponent.numberOf(from).longValue();
			return (val < min) ? min : Math.min(val, max);
		}

		@Override
		public LongRange range(Long min, Long max) {
			var checker = Codec.checkRange(min, max);
			return new LongRange(min, max, Codec.LONG.flatXmap(checker, checker));
		}

		@Override
		public String toString() {
			return toString("long", Long.MIN_VALUE, Long.MAX_VALUE);
		}
	}

	record FloatRange(Float min, Float max, Codec<Float> codec) implements NumberComponent<FloatRange, Float> {
		@Override
		public Codec<Float> codec() {
			return codec;
		}

		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.FLOAT;
		}

		@Override
		public Float wrap(Context cx, KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).floatValue(), min, max);
		}

		@Override
		public FloatRange range(Float min, Float max) {
			return new FloatRange(min, max, Codec.floatRange(min, max));
		}

		@Override
		public String toString() {
			return toString("float", Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		}
	}

	record DoubleRange(Double min, Double max, Codec<Double> codec) implements NumberComponent<DoubleRange, Double> {
		@Override
		public Codec<Double> codec() {
			return codec;
		}

		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.DOUBLE;
		}

		@Override
		public Double wrap(Context cx, KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).doubleValue(), min, max);
		}

		@Override
		public DoubleRange range(Double min, Double max) {
			return new DoubleRange(min, max, Codec.doubleRange(min, max));
		}

		@Override
		public String toString() {
			return toString("double", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
	}
}
