package dev.latvian.mods.kubejs.testmod.level;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "kubejs.level.event")
public class LevelEventTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "level_tick", description = "KubeJS LevelEvents.tick fires while the level ticks")
	static void levelTick(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("level.tick"))
			.thenIdle(2)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("level.tick"), "script did not report level.tick"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(value = "5x5x5", floor = true)
	@TestHolder(value = "level_explosion", description = "KubeJS LevelEvents.beforeExplosion/afterExplosion fire when a level explodes")
	static void levelExplosion(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("level.beforeExplosion", "level.afterExplosion"))
			.thenExecute(() -> helper.getLevel().explode(null, null, null, helper.absoluteVec(POS.getCenter()), 3.0F, false, Level.ExplosionInteraction.BLOCK))
			.thenIdle(2)
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("level.beforeExplosion"), "script did not assert on level.beforeExplosion"))
			.thenExecute(() -> TestRuntime.verify("level.beforeExplosion"))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("level.afterExplosion"), "script did not assert on level.afterExplosion"))
			.thenExecute(() -> TestRuntime.verify("level.afterExplosion"))
			.thenSucceed());
	}
}
