package dev.latvian.mods.kubejs.level.gen.ruletest;

import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public interface KubeJSRuleTests {
	RuleTestType<InvertRuleTest> INVERT = RuleTestType.register("kubejs:invert", InvertRuleTest.CODEC);
	RuleTestType<AlwaysFalseRuleTest> ALWAYS_FALSE = RuleTestType.register("kubejs:always_false", AlwaysFalseRuleTest.CODEC);
	RuleTestType<AllMatchRuleTest> ALL_MATCH = RuleTestType.register("kubejs:all_match", AllMatchRuleTest.CODEC);
	RuleTestType<AnyMatchRuleTest> ANY_MATCH = RuleTestType.register("kubejs:any_match", AnyMatchRuleTest.CODEC);

	static void init() {
	}
}
