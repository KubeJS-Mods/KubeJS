package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.TinyMap;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TinyMapTest {
	@Test
	void roundTripsThroughMap() {
		var source = Map.of("a", 1, "b", 2);
		assertThat(TinyMap.ofMap(source).toMap()).isEqualTo(source);
	}

	@Test
	void emptyMapIsEmpty() {
		assertThat(TinyMap.ofMap(Map.<String, Integer>of()).isEmpty()).isTrue();
	}

	@Test
	void copyConstructorPreservesEntries() {
		var original = TinyMap.ofMap(Map.of("a", 1, "b", 2));
		var copy = new TinyMap<>(original);
		assertThat(copy.toMap()).isEqualTo(original.toMap());
	}
}
