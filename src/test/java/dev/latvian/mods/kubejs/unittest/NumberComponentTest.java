package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(BootstrapExtension.class)
public class NumberComponentTest {
	@Test
	void constantsCoverFullRange() {
		assertThat(NumberComponent.INT.min()).isEqualTo(Integer.MIN_VALUE);
		assertThat(NumberComponent.INT.max()).isEqualTo(Integer.MAX_VALUE);
		assertThat(NumberComponent.LONG.min()).isEqualTo(Long.MIN_VALUE);
		assertThat(NumberComponent.LONG.max()).isEqualTo(Long.MAX_VALUE);
		assertThat(NumberComponent.FLOAT.min()).isEqualTo(Float.NEGATIVE_INFINITY);
		assertThat(NumberComponent.FLOAT.max()).isEqualTo(Float.POSITIVE_INFINITY);
		assertThat(NumberComponent.DOUBLE.min()).isEqualTo(Double.NEGATIVE_INFINITY);
		assertThat(NumberComponent.DOUBLE.max()).isEqualTo(Double.POSITIVE_INFINITY);
	}

	@Test
	void rangeFactoriesReuseSharedConstantsForFullRange() {
		assertThat(NumberComponent.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE)).isSameAs(NumberComponent.INT);
		assertThat(NumberComponent.longRange(Long.MIN_VALUE, Long.MAX_VALUE)).isSameAs(NumberComponent.LONG);
		assertThat(NumberComponent.floatRange(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)).isSameAs(NumberComponent.FLOAT);
		assertThat(NumberComponent.doubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)).isSameAs(NumberComponent.DOUBLE);
	}

	@Test
	void intWrapClampsToBounds() {
		var range = NumberComponent.intRange(0, 10);
		assertThat(range.wrap(null, 5)).isEqualTo(5);
		assertThat(range.wrap(null, 20)).isEqualTo(10);
		assertThat(range.wrap(null, -3)).isEqualTo(0);
	}

	@Test
	void longWrapClampsToBounds() {
		var range = NumberComponent.longRange(0L, 100L);
		assertThat(range.wrap(null, 50L)).isEqualTo(50L);
		assertThat(range.wrap(null, 200L)).isEqualTo(100L);
		assertThat(range.wrap(null, -5L)).isEqualTo(0L);
	}

	@Test
	void floatWrapClampsToBounds() {
		var range = NumberComponent.floatRange(0F, 1F);
		assertThat(range.wrap(null, 0.5)).isEqualTo(0.5F);
		assertThat(range.wrap(null, 2.0)).isEqualTo(1.0F);
		assertThat(range.wrap(null, -1.0)).isEqualTo(0.0F);
	}

	@Test
	void doubleWrapClampsToBounds() {
		var range = NumberComponent.doubleRange(0.0, 1.0);
		assertThat(range.wrap(null, 0.5)).isEqualTo(0.5);
		assertThat(range.wrap(null, 2.0)).isEqualTo(1.0);
		assertThat(range.wrap(null, -1.0)).isEqualTo(0.0);
	}

	@Test
	void wrapParsesNumbersFromStringAndJson() {
		assertThat(NumberComponent.INT.wrap(null, "7")).isEqualTo(7);
		assertThat(NumberComponent.DOUBLE.wrap(null, "2.5")).isEqualTo(2.5);
		assertThat(NumberComponent.INT.wrap(null, new JsonPrimitive(42))).isEqualTo(42);
	}

	@Test
	void wrapRejectsNonNumericValues() {
		assertThatThrownBy(() -> NumberComponent.INT.wrap(null, new Object()))
			.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> NumberComponent.INT.wrap(null, null))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void toStringReflectsBounds() {
		assertThat(NumberComponent.INT.toString()).isEqualTo("int");
		assertThat(NumberComponent.LONG.toString()).isEqualTo("long");
		assertThat(NumberComponent.FLOAT.toString()).isEqualTo("float");
		assertThat(NumberComponent.DOUBLE.toString()).isEqualTo("double");
		assertThat(NumberComponent.intRange(0, 10).toString()).isEqualTo("int<0,10>");
		assertThat(NumberComponent.intRange(Integer.MIN_VALUE, 10).toString()).isEqualTo("int<min,10>");
		assertThat(NumberComponent.intRange(0, Integer.MAX_VALUE).toString()).isEqualTo("int<0,max>");
	}

	@Test
	void hasPriorityOnlyForNumbers() {
		assertThat(NumberComponent.INT.hasPriority(null, 5)).isTrue();
		assertThat(NumberComponent.INT.hasPriority(null, new JsonPrimitive(5))).isTrue();
		assertThat(NumberComponent.INT.hasPriority(null, "5")).isFalse();
		assertThat(NumberComponent.INT.hasPriority(null, new JsonPrimitive("x"))).isFalse();
	}

	@Test
	void typeInfoMatchesNumberKind() {
		assertThat(NumberComponent.INT.typeInfo()).isEqualTo(TypeInfo.INT);
		assertThat(NumberComponent.LONG.typeInfo()).isEqualTo(TypeInfo.LONG);
		assertThat(NumberComponent.FLOAT.typeInfo()).isEqualTo(TypeInfo.FLOAT);
		assertThat(NumberComponent.DOUBLE.typeInfo()).isEqualTo(TypeInfo.DOUBLE);
	}
}
