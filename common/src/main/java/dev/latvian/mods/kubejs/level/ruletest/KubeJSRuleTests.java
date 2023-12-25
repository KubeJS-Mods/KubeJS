package dev.latvian.mods.kubejs.level.ruletest;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public interface KubeJSRuleTests {
	DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create(KubeJS.MOD_ID, Registries.RULE_TEST);

	RuleTestType<InvertRuleTest> INVERT = register("invert", InvertRuleTest.CODEC);
	RuleTestType<AlwaysFalseRuleTest> ALWAYS_FALSE = register("always_false", AlwaysFalseRuleTest.CODEC);
	RuleTestType<AllMatchRuleTest> ALL_MATCH = register("all_match", AllMatchRuleTest.CODEC);
	RuleTestType<AnyMatchRuleTest> ANY_MATCH = register("any_match", AnyMatchRuleTest.CODEC);

	static <P extends RuleTest> RuleTestType<P> register(String id, Codec<P> codec) {
		var type = (RuleTestType<P>) () -> codec;
		RULE_TEST_TYPES.register(id, () -> type);
		return type;
	}

	static void init() {
		RULE_TEST_TYPES.register();
	}
}
