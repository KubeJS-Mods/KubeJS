package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public interface NumberComponent<S, T extends Number> extends RecipeComponent<T> {
	IntRange INT = new IntRange(null, Integer.MIN_VALUE, Integer.MAX_VALUE, Codec.INT);
	LongRange LONG = new LongRange(null, Long.MIN_VALUE, Long.MAX_VALUE, Codec.LONG);
	FloatRange FLOAT = new FloatRange(null, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Codec.FLOAT);
	DoubleRange DOUBLE = new DoubleRange(null, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Codec.DOUBLE);

	static IntRange intRange(int min, int max) {
		return min == Integer.MIN_VALUE && max == Integer.MAX_VALUE ? INT : IntRange.of(null, min, max);
	}

	static LongRange longRange(long min, long max) {
		return min == Long.MIN_VALUE && max == Long.MAX_VALUE ? LONG : LongRange.of(null, min, max);
	}

	static FloatRange floatRange(float min, float max) {
		return min == Float.NEGATIVE_INFINITY && max == Float.POSITIVE_INFINITY ? FLOAT : FloatRange.of(null, min, max);
	}

	static DoubleRange doubleRange(double min, double max) {
		return min == Double.NEGATIVE_INFINITY && max == Double.POSITIVE_INFINITY ? DOUBLE : DoubleRange.of(null, min, max);
	}

	RecipeComponentType<?> INT_TYPE = RecipeComponentType.<IntRange>dynamic(KubeJS.id("int"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(IntRange::min),
		Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(IntRange::max)
	).apply(instance, NumberComponent::intRange)));

	RecipeComponentType<?> LONG_TYPE = RecipeComponentType.<LongRange>dynamic(KubeJS.id("long"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.LONG.optionalFieldOf("min", Long.MIN_VALUE).forGetter(LongRange::min),
		Codec.LONG.optionalFieldOf("max", Long.MAX_VALUE).forGetter(LongRange::max)
	).apply(instance, NumberComponent::longRange)));

	RecipeComponentType<?> FLOAT_TYPE = RecipeComponentType.<FloatRange>dynamic(KubeJS.id("float"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("min", Float.NEGATIVE_INFINITY).forGetter(FloatRange::min),
		Codec.FLOAT.optionalFieldOf("max", Float.POSITIVE_INFINITY).forGetter(FloatRange::max)
	).apply(instance, NumberComponent::floatRange)));

	RecipeComponentType<?> DOUBLE_TYPE = RecipeComponentType.<DoubleRange>dynamic(KubeJS.id("double"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.optionalFieldOf("min", Double.NEGATIVE_INFINITY).forGetter(DoubleRange::min),
		Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(DoubleRange::max)
	).apply(instance, NumberComponent::doubleRange)));

	RecipeComponentType<Integer> NON_NEGATIVE_INT = RecipeComponentType.unit(KubeJS.id("non_negative_int"), type -> new IntRange(type, 0, Integer.MAX_VALUE, KubeJSCodecs.NON_NEGATIVE_INT));
	RecipeComponentType<Integer> POSITIVE_INT = RecipeComponentType.unit(KubeJS.id("positive_int"), type -> new IntRange(type, 1, Integer.MAX_VALUE, KubeJSCodecs.POSITIVE_INT));
	RecipeComponentType<Long> NON_NEGATIVE_LONG = RecipeComponentType.unit(KubeJS.id("non_negative_long"), type -> new LongRange(type, 0L, Long.MAX_VALUE, KubeJSCodecs.NON_NEGATIVE_LONG));
	RecipeComponentType<Long> POSITIVE_LONG = RecipeComponentType.unit(KubeJS.id("positive_long"), type -> new LongRange(type, 1L, Long.MAX_VALUE, KubeJSCodecs.POSITIVE_LONG));
	RecipeComponentType<Float> NON_NEGATIVE_FLOAT = RecipeComponentType.unit(KubeJS.id("non_negative_float"), type -> new FloatRange(type, 0F, Float.POSITIVE_INFINITY, KubeJSCodecs.NON_NEGATIVE_FLOAT));
	RecipeComponentType<Float> POSITIVE_FLOAT = RecipeComponentType.unit(KubeJS.id("positive_float"), type -> new FloatRange(type, Float.MIN_VALUE, Float.POSITIVE_INFINITY, KubeJSCodecs.POSITIVE_FLOAT));
	RecipeComponentType<Double> NON_NEGATIVE_DOUBLE = RecipeComponentType.unit(KubeJS.id("non_negative_double"), type -> new DoubleRange(type, 0D, Double.POSITIVE_INFINITY, KubeJSCodecs.NON_NEGATIVE_DOUBLE));
	RecipeComponentType<Double> POSITIVE_DOUBLE = RecipeComponentType.unit(KubeJS.id("positive_double"), type -> new DoubleRange(type, Double.MIN_VALUE, Double.POSITIVE_INFINITY, KubeJSCodecs.POSITIVE_DOUBLE));

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

	default String toString(@Nullable RecipeComponentType<?> typeOverride, String name, T min, T max) {
		if (typeOverride != null) {
			return typeOverride.toString();
		}

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

	record IntRange(@Nullable RecipeComponentType<?> typeOverride, Integer min, Integer max, Codec<Integer> codec) implements NumberComponent<IntRange, Integer> {
		public static IntRange of(@Nullable RecipeComponentType<?> typeOverride, Integer min, Integer max) {
			return new IntRange(typeOverride, min, max, Codec.intRange(min, max));
		}

		@Override
		public RecipeComponentType<?> type() {
			return typeOverride == null ? INT_TYPE : typeOverride;
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
			return of(null, min, max);
		}

		@Override
		public String toString() {
			return toString(typeOverride, "int", Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
	}

	record LongRange(@Nullable RecipeComponentType<?> typeOverride, Long min, Long max, Codec<Long> codec) implements NumberComponent<LongRange, Long> {
		public static LongRange of(@Nullable RecipeComponentType<?> typeOverride, Long min, Long max) {
			var checker = Codec.checkRange(min, max);
			return new LongRange(typeOverride, min, max, Codec.LONG.flatXmap(checker, checker));
		}

		@Override
		public RecipeComponentType<?> type() {
			return typeOverride == null ? LONG_TYPE : typeOverride;
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
			return of(null, min, max);
		}

		@Override
		public String toString() {
			return toString(typeOverride, "long", Long.MIN_VALUE, Long.MAX_VALUE);
		}
	}

	record FloatRange(@Nullable RecipeComponentType<?> typeOverride, Float min, Float max, Codec<Float> codec) implements NumberComponent<FloatRange, Float> {
		public static FloatRange of(@Nullable RecipeComponentType<?> typeOverride, Float min, Float max) {
			return new FloatRange(typeOverride, min, max, Codec.floatRange(min, max));
		}

		@Override
		public RecipeComponentType<?> type() {
			return typeOverride == null ? FLOAT_TYPE : typeOverride;
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
			return of(null, min, max);
		}

		@Override
		public String toString() {
			return toString(typeOverride, "float", Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		}
	}

	record DoubleRange(@Nullable RecipeComponentType<?> typeOverride, Double min, Double max, Codec<Double> codec) implements NumberComponent<DoubleRange, Double> {
		public static DoubleRange of(@Nullable RecipeComponentType<?> typeOverride, Double min, Double max) {
			return new DoubleRange(typeOverride, min, max, Codec.doubleRange(min, max));
		}

		@Override
		public RecipeComponentType<?> type() {
			return typeOverride == null ? DOUBLE_TYPE : typeOverride;
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
			return of(null, min, max);
		}

		@Override
		public String toString() {
			return toString(typeOverride, "double", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
	}
}
