package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

	RecipeComponent<Integer> INT = intRange(0, Integer.MAX_VALUE);
	RecipeComponent<Long> LONG = longRange(0L, Long.MAX_VALUE);
	RecipeComponent<Float> FLOAT = floatRange(0F, Float.POSITIVE_INFINITY);
	RecipeComponent<Double> DOUBLE = doubleRange(0D, Double.POSITIVE_INFINITY);

	RecipeComponent<Integer> ANY_INT = intRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
	RecipeComponent<Long> ANY_LONG = longRange(Long.MIN_VALUE, Long.MAX_VALUE);
	RecipeComponent<Float> ANY_FLOAT = floatRange(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	RecipeComponent<Double> ANY_DOUBLE = doubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

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

	record IntRange(int min, int max) implements NumberComponent<Integer> {
		@Override
		public JsonObject description() {
			var obj = new JsonObject();
			obj.addProperty("type", componentType());
			obj.addProperty("number_type", "int");
			obj.addProperty("min", min);
			obj.addProperty("max", max);
			return obj;
		}

		@Override
		public JsonPrimitive write(Integer value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Integer read(Object from) {
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
		public JsonObject description() {
			var obj = new JsonObject();
			obj.addProperty("type", componentType());
			obj.addProperty("number_type", "long");
			obj.addProperty("min", min);
			obj.addProperty("max", max);
			return obj;
		}

		@Override
		public JsonPrimitive write(Long value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Long read(Object from) {
			return Mth.clamp(NumberComponent.numberOf(from).longValue(), min, max);
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
		public JsonObject description() {
			var obj = new JsonObject();
			obj.addProperty("type", componentType());
			obj.addProperty("number_type", "float");
			obj.addProperty("min", min);
			obj.addProperty("max", max);
			return obj;
		}

		@Override
		public JsonPrimitive write(Float value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Float read(Object from) {
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
		public JsonObject description() {
			var obj = new JsonObject();
			obj.addProperty("type", componentType());
			obj.addProperty("number_type", "double");
			obj.addProperty("min", min);
			obj.addProperty("max", max);
			return obj;
		}

		@Override
		public JsonPrimitive write(Double value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Double read(Object from) {
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
