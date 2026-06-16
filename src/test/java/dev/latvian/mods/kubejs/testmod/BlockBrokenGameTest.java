package dev.latvian.mods.kubejs.testmod;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

/// Has an empty-handed player break a dirt block and asserts the {@code block_broken.js} server
/// script reacted to {@code BlockEvents.broken} by calling {@code TestRuntime.pass}.
public class BlockBrokenGameTest extends GameTestInstance {
	public BlockBrokenGameTest(TestData<Holder<TestEnvironmentDefinition<?>>> info) {
		super(info);
	}

	@Override
	public void run(GameTestHelper helper) {
		TestRuntime.reset();

		var dirtPos = new BlockPos(1, 2, 1);
		helper.setBlock(dirtPos, Blocks.DIRT);

		// A fake player has no real connection, so its empty-handed break runs the normal
		// player break path without KubeJS' join-time sync (which fails over a test connection).
		FakePlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
		player.gameMode.destroyBlock(helper.absolutePos(dirtPos));

		helper.succeedWhen(() -> helper.assertTrue(TestRuntime.passed("block.break.dirt"), "script did not report block.break.dirt"));
	}

	@Override
	public MapCodec<? extends GameTestInstance> codec() {
		return MapCodec.unit(this);
	}

	@Override
	protected MutableComponent typeDescription() {
		return Component.literal("KubeJS BlockEvents.broken test");
	}
}
