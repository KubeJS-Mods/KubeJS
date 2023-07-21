package dev.latvian.mods.kubejs.level.gen.ruletest;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.ArrayList;
import java.util.List;

public class AnyMatchRuleTest extends RuleTest {

	public static final Codec<AnyMatchRuleTest> CODEC = RuleTest.CODEC
		.listOf()
		.fieldOf("rules")
		.xmap(AnyMatchRuleTest::new, (t) -> t.rules)
		.codec();

	public final List<RuleTest> rules;

	public AnyMatchRuleTest() {
		this(new ArrayList<>());
	}

	public AnyMatchRuleTest(List<RuleTest> rules) {
		this.rules = rules;
	}

	@Override
	public boolean test(BlockState blockState, RandomSource random) {
		for (var test : rules) {
			if (test.test(blockState, random)) {
				return true;
			}
		}

		return rules.isEmpty();
	}

	@Override
	protected RuleTestType<?> getType() {
		return KubeJSRuleTests.ANY_MATCH;
	}
}
