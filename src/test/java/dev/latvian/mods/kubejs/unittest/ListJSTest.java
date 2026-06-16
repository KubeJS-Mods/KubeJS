package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.ListJS;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListJSTest {
	@Test
	void primitiveArrayBecomesBoxedList() {
		assertEquals(List.of(1, 2, 3), ListJS.of(new int[]{1, 2, 3}));
	}

	@Test
	void existingListIsReturnedAsIs() {
		var list = List.of("a", "b");
		assertEquals(list, ListJS.of(list));
	}

	@Test
	void orSelfWrapsScalar() {
		assertEquals(List.of("hello"), ListJS.orSelf("hello"));
	}

	@Test
	void orSelfOfNullIsEmpty() {
		assertTrue(ListJS.orSelf(null).isEmpty());
	}

	@Test
	void ofScalarIsNull() {
		assertNull(ListJS.of("hello"));
	}

	@Test
	void ofSetDeduplicates() {
		var set = ListJS.ofSet(Arrays.asList(1, 1, 2, 3, 3));
		assertEquals(3, set.size());
	}
}
