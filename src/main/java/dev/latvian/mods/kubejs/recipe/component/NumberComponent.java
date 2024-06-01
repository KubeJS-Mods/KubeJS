package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.util.Mth;

public interface NumberComponent<T extends Number> extends RecipeComponent<T> {
	static IntRange intRange(int min, int max) {
		return new IntRange(min, max);
	}

	static LongRange longRange(long min, long max) {
		return new LongRange(min, max);
	}

	static FloatRange floatRange(float min, float max) {
		return new FloatRange(min, max);
	}

	static DoubleRange doubleRange(double min, double max) {
		return new DoubleRange(min, max);
	}

	IntRange INT = intRange(0, Integer.MAX_VALUE);
	LongRange LONG = longRange(0L, Long.MAX_VALUE);
	FloatRange FLOAT = floatRange(0F, Float.POSITIVE_INFINITY);
	DoubleRange DOUBLE = doubleRange(0D, Double.POSITIVE_INFINITY);

	IntRange ANY_INT = intRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
	LongRange ANY_LONG = longRange(Long.MIN_VALUE, Long.MAX_VALUE);
	FloatRange ANY_FLOAT = floatRange(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	DoubleRange ANY_DOUBLE = doubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	DynamicRecipeComponent DYNAMIC_INT = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("min", TypeInfo.INT, true),
		new JSObjectTypeInfo.Field("max", TypeInfo.INT, true)
	), (cx, scope, args) -> {
			var min = ScriptRuntime.toInt32(cx, Wrapper.unwrapped(args.getOrDefault("min", 0)));
			var max = ScriptRuntime.toInt32(cx, Wrapper.unwrapped(args.getOrDefault("max", Integer.MAX_VALUE)));
			return NumberComponent.intRange(min, max);
		});

	DynamicRecipeComponent DYNAMIC_LONG = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("min", TypeInfo.LONG, true),
		new JSObjectTypeInfo.Field("max", TypeInfo.LONG, true)
	), (cx, scope, args) -> {
			var min = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("min", 0)));
			var max = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("max", Long.MAX_VALUE)));
			return NumberComponent.longRange((long) min, (long) max);
		});

	DynamicRecipeComponent DYNAMIC_FLOAT = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("min", TypeInfo.FLOAT, true),
		new JSObjectTypeInfo.Field("max", TypeInfo.FLOAT, true)
	), (cx, scope, args) -> {
			var min = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("min", 0F)));
			var max = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("max", Float.MAX_VALUE)));
			return NumberComponent.floatRange((float) min, (float) max);
		});

	DynamicRecipeComponent DYNAMIC_DOUBLE = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("min", TypeInfo.DOUBLE, true),
		new JSObjectTypeInfo.Field("max", TypeInfo.DOUBLE, true)
	), (cx, scope, args) -> {
			var min = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("min", 0)));
			var max = ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("max", Double.MAX_VALUE)));
			return NumberComponent.doubleRange(min, max);
		});

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
	default String componentType() {
		return "number";
	}

	@Override
	default TypeInfo typeInfo() {
		return TypeInfo.NUMBER;
	}

	@Override
	default boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	record IntRange(int min, int max) implements NumberComponent<Integer> {
		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.INT;
		}

		@Override
		public JsonPrimitive write(KubeRecipe recipe, Integer value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Integer read(KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).intValue(), min, max);
		}

		public IntRange min(int min) {
			return new IntRange(min, max);
		}

		public IntRange max(int max) {
			return new IntRange(min, max);
		}

		@Override
		public String toString() {
			return "int";
		}
	}

	record LongRange(long min, long max) implements NumberComponent<Long> {
		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.LONG;
		}

		@Override
		public JsonPrimitive write(KubeRecipe recipe, Long value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Long read(KubeRecipe recipe, Object from) {
			long val = NumberComponent.numberOf(from).longValue();
			return (val < min) ? min : Math.min(val, max);
		}

		public LongRange min(long min) {
			return new LongRange(min, max);
		}

		public LongRange max(long max) {
			return new LongRange(min, max);
		}

		@Override
		public String toString() {
			return "long";
		}
	}

	record FloatRange(float min, float max) implements NumberComponent<Float> {
		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.FLOAT;
		}

		@Override
		public JsonPrimitive write(KubeRecipe recipe, Float value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Float read(KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).floatValue(), min, max);
		}

		public FloatRange min(float min) {
			return new FloatRange(min, max);
		}

		public FloatRange max(float max) {
			return new FloatRange(min, max);
		}

		@Override
		public String toString() {
			return "float";
		}
	}

	record DoubleRange(double min, double max) implements NumberComponent<Double> {
		@Override
		public TypeInfo typeInfo() {
			return TypeInfo.DOUBLE;
		}

		@Override
		public JsonPrimitive write(KubeRecipe recipe, Double value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Double read(KubeRecipe recipe, Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).doubleValue(), min, max);
		}

		public DoubleRange min(double min) {
			return new DoubleRange(min, max);
		}

		public DoubleRange max(double max) {
			return new DoubleRange(min, max);
		}

		@Override
		public String toString() {
			return "double";
		}
	}
}
