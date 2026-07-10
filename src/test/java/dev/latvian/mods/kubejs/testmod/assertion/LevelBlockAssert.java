package dev.latvian.mods.kubejs.testmod.assertion;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import org.assertj.core.api.AbstractAssert;

/// AssertJ entry point for asserting on the [LevelBlock] a block event exposes as {@code event.block},
/// e.g. {@code TestRuntime.assertThat(event.block).hasId('minecraft:dirt')}.
public class LevelBlockAssert extends AbstractAssert<LevelBlockAssert, LevelBlock> {
	public LevelBlockAssert(LevelBlock actual) {
		super(actual, LevelBlockAssert.class);
	}

	public LevelBlockAssert hasId(String expected) {
		isNotNull();
		var id = BuiltInRegistries.BLOCK.getKey(actual.kjs$getBlock()).toString();

		if (!id.equals(expected)) {
			failWithMessage("Expected block id <%s> but was <%s>", expected, id);
		}

		return this;
	}

	public LevelBlockAssert hasProperty(String key, String value) {
		isNotNull();
		var actualValue = actual.getProperties().get(key);

		if (!value.equals(actualValue)) {
			failWithMessage("Expected block property <%s>=<%s> but was <%s>", key, value, actualValue);
		}

		return this;
	}
}
