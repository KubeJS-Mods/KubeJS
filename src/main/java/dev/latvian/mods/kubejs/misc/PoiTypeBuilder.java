package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

@ReturnsSelf
public class PoiTypeBuilder extends BuilderBase<PoiType> {
	public transient Set<BlockState> blockStates;
	public transient int maxTickets;
	public transient int validRange;

	public PoiTypeBuilder(ResourceLocation i) {
		super(i);
		blockStates = Set.of();
		maxTickets = 1;
		validRange = 1;
	}

	@Override
	public PoiType createObject() {
		return new PoiType(blockStates, maxTickets, validRange);
	}

	public PoiTypeBuilder blocks(BlockState[] r) {
		blockStates = Set.of(r);
		return this;
	}

	public PoiTypeBuilder block(Block r) {
		blockStates = Set.copyOf(r.getStateDefinition().getPossibleStates());
		return this;
	}

	public PoiTypeBuilder maxTickets(int i) {
		maxTickets = i;
		return this;
	}

	public PoiTypeBuilder validRange(int i) {
		validRange = i;
		return this;
	}
}
