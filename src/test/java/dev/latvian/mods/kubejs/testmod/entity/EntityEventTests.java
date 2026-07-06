package dev.latvian.mods.kubejs.testmod.entity;

import dev.latvian.mods.kubejs.testmod.TestRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "kubejs.entity.event")
public class EntityEventTests {
	private static final BlockPos POS = new BlockPos(1, 2, 1);

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_spawned", description = "KubeJS EntityEvents.spawned fires when an entity is added to the level")
	static void entitySpawned(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.spawned"))
			.thenExecute(() -> helper.spawn(EntityType.PIG, POS))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.spawned"), "script did not report entity.spawned"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_before_hurt", description = "KubeJS EntityEvents.beforeHurt fires before an entity takes damage")
	static void entityBeforeHurt(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.beforeHurt"))
			.thenExecute(player -> {
				var level = (ServerLevel) player.level();
				var pig = helper.spawn(EntityType.PIG, POS);
				pig.hurtServer(level, level.damageSources().generic(), 1.0F);
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.beforeHurt"), "script did not report entity.beforeHurt"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_after_hurt", description = "KubeJS EntityEvents.afterHurt fires after an entity takes damage")
	static void entityAfterHurt(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.afterHurt"))
			.thenExecute(player -> {
				var level = (ServerLevel) player.level();
				var pig = helper.spawn(EntityType.PIG, POS);
				pig.hurtServer(level, level.damageSources().generic(), 1.0F);
			})
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.afterHurt"), "script did not report entity.afterHurt"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_death", description = "KubeJS EntityEvents.death fires when an entity dies")
	static void entityDeath(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.death"))
			.thenExecute(player -> helper.spawn(EntityType.PIG, POS).kill((ServerLevel) player.level()))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.death"), "script did not report entity.death"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_death_asserts", description = "KubeJS EntityEvents.death exposes the dead entity and damage source to script assertions")
	static void entityDeathAsserts(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.death.assert"))
			.thenExecute(player -> helper.spawn(EntityType.PIG, POS).kill((ServerLevel) player.level()))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.death.assert"), "script did not assert on entity.death"))
			.thenExecute(() -> TestRuntime.verify("entity.death.assert"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_drops", description = "KubeJS EntityEvents.drops fires when an entity drops loot on death")
	static void entityDrops(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.drops"))
			.thenExecute(player -> helper.spawn(EntityType.PIG, POS).kill((ServerLevel) player.level()))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.drops"), "script did not report entity.drops"))
			.thenSucceed());
	}

	@GameTest
	@EmptyTemplate(floor = true)
	@TestHolder(value = "entity_check_spawn", description = "KubeJS EntityEvents.checkSpawn fires when a mob is finalized during spawning")
	static void entityCheckSpawn(final DynamicTest test) {
		test.onGameTest(helper -> helper.startSequence(() -> helper.makeTickingMockServerPlayerInCorner(GameType.SURVIVAL))
			.thenExecute(() -> TestRuntime.clear("entity.checkSpawn"))
			.thenExecute(player -> EntityType.ZOMBIE.spawn((ServerLevel) player.level(), helper.absolutePos(POS), EntitySpawnReason.SPAWNER))
			.thenWaitUntil(() -> helper.assertTrue(TestRuntime.passed("entity.checkSpawn"), "script did not report entity.checkSpawn"))
			.thenSucceed());
	}
}
