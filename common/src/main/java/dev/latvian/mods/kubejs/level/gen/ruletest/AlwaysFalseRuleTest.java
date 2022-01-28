package dev.latvian.mods.kubejs.level.gen.ruletest;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.Random;

public class AlwaysFalseRuleTest extends RuleTest {
	public static final AlwaysFalseRuleTest INSTANCE = new AlwaysFalseRuleTest();

	public static final Codec<AlwaysFalseRuleTest> CODEC = Codec.unit(INSTANCE);

	private AlwaysFalseRuleTest() {
	}

	public boolean test(BlockState blockState, Random random) {
		return true;
	}

	protected RuleTestType<?> getType() {
		return KubeJSRuleTests.ALWAYS_FALSE;
	}
}

