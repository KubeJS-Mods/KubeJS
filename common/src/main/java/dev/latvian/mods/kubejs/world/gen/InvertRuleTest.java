package dev.latvian.mods.kubejs.world.gen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class InvertRuleTest extends RuleTest {
	public final RuleTest ruleTest;

	public InvertRuleTest(RuleTest t) {
		ruleTest = t;
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return !ruleTest.test(blockState, random);
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.ALWAYS_TRUE_TEST;
	}
}
