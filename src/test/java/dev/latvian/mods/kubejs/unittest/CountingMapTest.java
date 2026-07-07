package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.CountingMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CountingMapTest {
	@Test
	void missingKeyCountsAsZero() {
		assertThat(new CountingMap().get("x")).isZero();
	}

	@Test
	void addAccumulates() {
		var map = new CountingMap();
		map.add("x", 3L);
		map.add("x", 2L);
		assertThat(map.get("x")).isEqualTo(5L);
	}

	@Test
	void settingToZeroRemovesKey() {
		var map = new CountingMap();
		map.set("x", 4L);
		map.set("x", 0L);
		assertThat(map.getSize()).isZero();
		assertThat(map.get("x")).isZero();
	}

	@Test
	void totalCountSumsValues() {
		var map = new CountingMap();
		map.set("a", 2L);
		map.set("b", 3L);
		assertThat(map.getSize()).isEqualTo(2);
		assertThat(map.getTotalCount()).isEqualTo(5L);
	}
}
