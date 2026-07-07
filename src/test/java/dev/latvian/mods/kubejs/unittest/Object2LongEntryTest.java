package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.Object2LongEntry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Object2LongEntryTest {
	@Test
	void higherValueSortsFirst() {
		var high = new Object2LongEntry("a", 5L);
		var low = new Object2LongEntry("b", 3L);
		assertThat(high).isLessThan(low);
		assertThat(low).isGreaterThan(high);
	}

	@Test
	void equalValuesTieByCaseInsensitiveKey() {
		var apple = new Object2LongEntry("apple", 5L);
		var banana = new Object2LongEntry("Banana", 5L);
		assertThat(apple).isLessThan(banana);
	}
}
