package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.UtilsJS;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UtilsJSTest {
	@SuppressWarnings("unused")
	private static final class Fields {
		List<String> stringList;
		Map<String, Integer> stringIntMap;
		List<String>[] stringListArray;
		List<? extends Number> extendsNumber;
		List<? super Integer> superInteger;
		List<?> unbounded;
	}

	@SuppressWarnings("unused")
	private static final class Simple<T> {
	}

	@SuppressWarnings("unused")
	private static final class BoundedSingle<T extends Number> {
	}

	@SuppressWarnings("unused")
	private static final class BoundedMulti<T extends Number & Comparable<Integer>> {
	}

	private static Type fieldType(String name) throws NoSuchFieldException {
		return Fields.class.getDeclaredField(name).getGenericType();
	}

	@Test
	void classRendersSimpleName() {
		assertThat(UtilsJS.toMappedTypeString(String.class)).isEqualTo("String");
		assertThat(UtilsJS.toMappedTypeString(int.class)).isEqualTo("int");
	}

	@Test
	void parameterizedTypeRendersGenerics() throws NoSuchFieldException {
		assertThat(UtilsJS.toMappedTypeString(fieldType("stringList"))).isEqualTo("List<String>");
		assertThat(UtilsJS.toMappedTypeString(fieldType("stringIntMap"))).isEqualTo("Map<String, Integer>");
	}

	@Test
	void genericArrayTypeAppendsBrackets() throws NoSuchFieldException {
		assertThat(UtilsJS.toMappedTypeString(fieldType("stringListArray"))).isEqualTo("List<String>[]");
	}

	@Test
	void wildcardTypesRenderBounds() throws NoSuchFieldException {
		assertThat(UtilsJS.toMappedTypeString(fieldType("extendsNumber"))).isEqualTo("List<? extends Number>");
		assertThat(UtilsJS.toMappedTypeString(fieldType("superInteger"))).isEqualTo("List<? super Integer>");
		assertThat(UtilsJS.toMappedTypeString(fieldType("unbounded"))).isEqualTo("List<?>");
	}

	@Test
	void typeVariableRendersNameAndBounds() {
		assertThat(UtilsJS.toMappedTypeString(Simple.class.getTypeParameters()[0])).isEqualTo("T");
		assertThat(UtilsJS.toMappedTypeString(BoundedSingle.class.getTypeParameters()[0])).isEqualTo("T extends Number");
		assertThat(UtilsJS.toMappedTypeString(BoundedMulti.class.getTypeParameters()[0])).isEqualTo("T extends Number & Comparable<Integer>");
	}

	@Test
	void nullTypeThrows() {
		assertThatThrownBy(() -> UtilsJS.toMappedTypeString(null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void onMatchDoRunsConsumerOnlyOnMatch() {
		var seen = new ArrayList<Integer>();
		var predicate = UtilsJS.onMatchDo((Integer i) -> i > 2, seen::add);

		assertThat(predicate.test(5)).isTrue();
		assertThat(predicate.test(1)).isFalse();
		assertThat(seen).containsExactly(5);
	}
}
