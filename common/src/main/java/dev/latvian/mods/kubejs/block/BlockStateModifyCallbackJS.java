package dev.latvian.mods.kubejs.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class BlockStateModifyCallbackJS {
	private BlockState state;
	public BlockStateModifyCallbackJS(BlockState state) {
		this.state = state;
	}

	public <T extends Comparable<T>> BlockStateModifyCallbackJS cycle(Property<T> property) {
		this.state = state.cycle(property);
		return this;
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

	public <T extends Comparable<T>> T get(Property<T> property) {
		return state.getValue(property);
	}

	public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> property) {
		return state.getOptionalValue(property);
	}

	public <T extends Comparable<T>, V extends T> BlockStateModifyCallbackJS setValue(Property<T> property, V comparable) {
		this.state = state.setValue(property, comparable);
		return this;
	}

	public BlockStateModifyCallbackJS set(BooleanProperty property, boolean value) {
		this.state = state.setValue(property, value);
		return this;
	}

	public BlockStateModifyCallbackJS set(IntegerProperty property, Integer value) {
		this.state = state.setValue(property, value);
		return this;
	}

	public <T extends Enum<T> & StringRepresentable> BlockStateModifyCallbackJS set(EnumProperty<T> property, T value) {
		this.state = state.setValue(property, value);
		return this;
	}

	public <T extends Enum<T> & StringRepresentable> BlockStateModifyCallbackJS set(EnumProperty<T> property, String value) {
		this.state = state.setValue(property, property.getValue(value).get());
		return this;
	}


	public BlockStateModifyCallbackJS populateNeighbours(Map<Map<Property<?>, Comparable<?>>, BlockState> map) {
		state.populateNeighbours(map);
		return this;
	}

	public ImmutableMap<Property<?>, Comparable<?>> getValues() {
		return state.getValues();
	}

	public BlockStateModifyCallbackJS rotate(Rotation rotation) {
		this.state = state.rotate(rotation);
		return this;
	}

	public BlockStateModifyCallbackJS mirror(Mirror mirror) {
		this.state = state.mirror(mirror);
		return this;
	}

	public BlockStateModifyCallbackJS updateShape(Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
		this.state = state.updateShape(direction, blockState, levelAccessor, blockPos, blockPos2);
		return this;
	}


}
