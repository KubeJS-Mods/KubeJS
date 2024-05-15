package dev.latvian.mods.kubejs.level.ruletest;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class AlwaysFalseRuleTest extends RuleTest {
	public static final AlwaysFalseRuleTest INSTANCE = new AlwaysFalseRuleTest();

	public static final MapCodec<AlwaysFalseRuleTest> CODEC = MapCodec.unit(INSTANCE);

	private AlwaysFalseRuleTest() {
	}

	public boolean test(BlockState blockState, RandomSource random) {
		return true;
	}

	protected RuleTestType<?> getType() {
		return KubeJSRuleTests.ALWAYS_FALSE;
	}
}

