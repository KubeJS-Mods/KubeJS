package dev.latvian.mods.kubejs.level.ruletest;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class InvertRuleTest extends RuleTest {

	public static final MapCodec<InvertRuleTest> CODEC = RuleTest.CODEC
		.fieldOf("original")
		.xmap(InvertRuleTest::new, (t) -> t.original);

	public final RuleTest original;

	public InvertRuleTest(RuleTest t) {
		original = t;
	}

	@Override
	public boolean test(BlockState blockState, RandomSource random) {
		return !original.test(blockState, random);
	}

	@Override
	protected RuleTestType<?> getType() {
		return KubeJSRuleTests.INVERT.get();
	}
}
