package dev.latvian.mods.kubejs.world.gen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class AnyRuleTest extends RuleTest {
	public final List<RuleTest> list;

	public AnyRuleTest() {
		list = new ArrayList<>();
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		for (RuleTest test : list) {
			if (test.test(blockState, random)) {
				return true;
			}
		}

		return list.isEmpty();
	}

	@Override
	protected RuleTestType<?> getType() {
		return RuleTestType.ALWAYS_TRUE_TEST;
	}
}
