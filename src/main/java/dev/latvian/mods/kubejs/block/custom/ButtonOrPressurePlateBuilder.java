package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public interface ButtonOrPressurePlateBuilder {
	@ReturnsSelf
	BlockBuilder behaviour(BlockSetType behaviour);

	@ReturnsSelf
	BlockBuilder ticksToStayPressed(TickDuration ticks);
}
