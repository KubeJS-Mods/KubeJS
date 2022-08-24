package dev.latvian.mods.kubejs.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class BlockStateModifyCallbackJS {
	private BlockState state;
	public BlockStateModifyCallbackJS(BlockState state) {
		this.state = state;
	}

	public <T extends Comparable<T>> BlockState cycle(Property<T> property) {
		return this.state = state.cycle(property);
	}

	public BlockState getState() {
		return state;
	}

	@Override
	public String toString() {
		return state.toString();
	}

	public Collection<Property<?>> getProperties() {
		return state.getProperties();
	}

	public <T extends Comparable<T>> boolean hasProperty(Property<T> property) {
		return state.hasProperty(property);
	}

	public <T extends Comparable<T>> T getValue(Property<T> property) {
		return state.getValue(property);
	}

	public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> property) {
		return state.getOptionalValue(property);
	}

	public <T extends Comparable<T>, V extends T> BlockState setValue(Property<T> property, V comparable) {
		return this.state = state.setValue(property, comparable);
	}

	public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, BlockState> map) {
		state.populateNeighbours(map);
	}

	public ImmutableMap<Property<?>, Comparable<?>> getValues() {
		return state.getValues();
	}

	public BlockState rotate(Rotation rotation) {
		return this.state = state.rotate(rotation);
	}

	public BlockState mirror(Mirror mirror) {
		return this.state = state.mirror(mirror);
	}

	public BlockState updateShape(Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
		return this.state = state.updateShape(direction, blockState, levelAccessor, blockPos, blockPos2);
	}
}
