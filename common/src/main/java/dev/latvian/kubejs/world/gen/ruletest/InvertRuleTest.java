package dev.latvian.kubejs.world.gen.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.kubejs.world.gen.ruletest.type.KubeJSRuleTests;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class InvertRuleTest extends RuleTest {

	/*
	public static final Codec<InvertRuleTest> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					RuleTest.CODEC
							.fieldOf("invert")
							.forGetter((o) -> o.invert)
			).apply(instance, InvertRuleTest::new));
	*/

	public static final Codec<InvertRuleTest> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					RuleTest.CODEC
							.fieldOf("invert")
							.forGetter((o) -> o.invert)
			).apply(instance, InvertRuleTest::new));

	public final RuleTest invert;

	public InvertRuleTest(RuleTest t) {
		invert = t;
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return !invert.test(blockState, random);
	}

	@Override
	protected RuleTestType<InvertRuleTest> getType() {
		return KubeJSRuleTests.INVERT;
	}
}
