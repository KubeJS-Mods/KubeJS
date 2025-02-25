package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
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

	RecipeComponentType<Integer> INT_TYPE = RecipeComponentType.dynamic(KubeJS.id("int"), RecordCodecBuilder.<IntRange>mapCodec(instance -> instance.group(
		Codec.INT.optionalFieldOf("min", 0).forGetter(IntRange::min),
		Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(IntRange::max)
	).apply(instance, NumberComponent::intRange)));

	RecipeComponentType<Long> LONG_TYPE = RecipeComponentType.dynamic(KubeJS.id("long"), RecordCodecBuilder.<LongRange>mapCodec(instance -> instance.group(
		Codec.LONG.optionalFieldOf("min", 0L).forGetter(LongRange::min),
		Codec.LONG.optionalFieldOf("max", Long.MAX_VALUE).forGetter(LongRange::max)
	).apply(instance, NumberComponent::longRange)));

	RecipeComponentType<Float> FLOAT_TYPE = RecipeComponentType.dynamic(KubeJS.id("float"), RecordCodecBuilder.<FloatRange>mapCodec(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("min", 0F).forGetter(FloatRange::min),
		Codec.FLOAT.optionalFieldOf("max", Float.POSITIVE_INFINITY).forGetter(FloatRange::max)
	).apply(instance, NumberComponent::floatRange)));

	RecipeComponentType<Double> DOUBLE_TYPE = RecipeComponentType.dynamic(KubeJS.id("double"), RecordCodecBuilder.<DoubleRange>mapCodec(instance -> instance.group(
		Codec.DOUBLE.optionalFieldOf("min", 0D).forGetter(DoubleRange::min),
		Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(DoubleRange::max)
	).apply(instance, NumberComponent::doubleRange)));

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
		public RecipeComponentType<?> type() {
			return INT_TYPE;
		}

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
		public RecipeComponentType<?> type() {
			return LONG_TYPE;
		}

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
		public RecipeComponentType<?> type() {
			return FLOAT_TYPE;
		}

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
		public RecipeComponentType<?> type() {
			return DOUBLE_TYPE;
		}

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
