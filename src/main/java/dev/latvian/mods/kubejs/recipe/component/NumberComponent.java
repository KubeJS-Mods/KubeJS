package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

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

	ResourceKey<RecipeComponentType<?>> INT_TYPE = RecipeComponentType.builtin("int");
	MapCodec<IntRange> INT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(IntRange::min),
		Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(IntRange::max)
	).apply(instance, NumberComponent::intRange));

	ResourceKey<RecipeComponentType<?>> LONG_TYPE = RecipeComponentType.builtin("long");
	MapCodec<LongRange> LONG_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.LONG.optionalFieldOf("min", Long.MIN_VALUE).forGetter(LongRange::min),
		Codec.LONG.optionalFieldOf("max", Long.MAX_VALUE).forGetter(LongRange::max)
	).apply(instance, NumberComponent::longRange));

	ResourceKey<RecipeComponentType<?>> FLOAT_TYPE = RecipeComponentType.builtin("float");
	MapCodec<FloatRange> FLOAT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("min", Float.NEGATIVE_INFINITY).forGetter(FloatRange::min),
		Codec.FLOAT.optionalFieldOf("max", Float.POSITIVE_INFINITY).forGetter(FloatRange::max)
	).apply(instance, NumberComponent::floatRange));

	ResourceKey<RecipeComponentType<?>> DOUBLE_TYPE = RecipeComponentType.builtin("double");
	MapCodec<DoubleRange> DOUBLE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.optionalFieldOf("min", Double.NEGATIVE_INFINITY).forGetter(DoubleRange::min),
		Codec.DOUBLE.optionalFieldOf("max", Double.POSITIVE_INFINITY).forGetter(DoubleRange::max)
	).apply(instance, NumberComponent::doubleRange));

	IntRange NON_NEGATIVE_INT = new IntRange(
		RecipeComponentType.builtin("non_negative_int"),
		0, Integer.MAX_VALUE, KubeJSCodecs.NON_NEGATIVE_INT
	);
	IntRange POSITIVE_INT = new IntRange(
		RecipeComponentType.builtin("positive_int"),
		1, Integer.MAX_VALUE, KubeJSCodecs.POSITIVE_INT
	);
	LongRange NON_NEGATIVE_LONG = new LongRange(
		RecipeComponentType.builtin("non_negative_long"),
		0L, Long.MAX_VALUE, KubeJSCodecs.NON_NEGATIVE_LONG
	);
	LongRange POSITIVE_LONG = new LongRange(
		RecipeComponentType.builtin("positive_long"),
		1L, Long.MAX_VALUE, KubeJSCodecs.POSITIVE_LONG
	);
	FloatRange NON_NEGATIVE_FLOAT = new FloatRange(
		RecipeComponentType.builtin("non_negative_float"),
		0F, Float.POSITIVE_INFINITY, KubeJSCodecs.NON_NEGATIVE_FLOAT
	);
	FloatRange POSITIVE_FLOAT = new FloatRange(
		RecipeComponentType.builtin("positive_float"),
		Float.MIN_VALUE, Float.POSITIVE_INFINITY, KubeJSCodecs.POSITIVE_FLOAT
	);
	DoubleRange NON_NEGATIVE_DOUBLE = new DoubleRange(
		RecipeComponentType.builtin("non_negative_double"),
		0D, Double.POSITIVE_INFINITY, KubeJSCodecs.NON_NEGATIVE_DOUBLE
	);
	DoubleRange POSITIVE_DOUBLE = new DoubleRange(
		RecipeComponentType.builtin("positive_double"),
		Double.MIN_VALUE, Double.POSITIVE_INFINITY, KubeJSCodecs.POSITIVE_DOUBLE
	);

	@Contract("null -> fail")
	private static Number numberOf(@Nullable Object from) {
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
	default boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
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

	default String toString(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, String name, T min, T max) {
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

	record IntRange(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Integer min, Integer max, Codec<Integer> codec) implements NumberComponent<IntRange, Integer> {
		public static IntRange of(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Integer min, Integer max) {
			return new IntRange(typeOverride, min, max, Codec.intRange(min, max));
		}

		@Override
		public ResourceKey<RecipeComponentType<?>> type() {
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
		public Integer wrap(RecipeScriptContext cx, @Nullable Object from) {
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

	record LongRange(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Long min, Long max, Codec<Long> codec) implements NumberComponent<LongRange, Long> {
		public static LongRange of(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Long min, Long max) {
			var checker = Codec.checkRange(min, max);
			return new LongRange(typeOverride, min, max, Codec.LONG.flatXmap(checker, checker));
		}

		@Override
		public ResourceKey<RecipeComponentType<?>> type() {
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
		public Long wrap(RecipeScriptContext cx, @Nullable Object from) {
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

	record FloatRange(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Float min, Float max, Codec<Float> codec) implements NumberComponent<FloatRange, Float> {
		public static FloatRange of(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Float min, Float max) {
			return new FloatRange(typeOverride, min, max, Codec.floatRange(min, max));
		}

		@Override
		public ResourceKey<RecipeComponentType<?>> type() {
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
		public Float wrap(RecipeScriptContext cx, @Nullable Object from) {
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

	record DoubleRange(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Double min, Double max, Codec<Double> codec) implements NumberComponent<DoubleRange, Double> {
		public static DoubleRange of(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, Double min, Double max) {
			return new DoubleRange(typeOverride, min, max, Codec.doubleRange(min, max));
		}

		@Override
		public ResourceKey<RecipeComponentType<?>> type() {
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
		public Double wrap(RecipeScriptContext cx, @Nullable Object from) {
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
