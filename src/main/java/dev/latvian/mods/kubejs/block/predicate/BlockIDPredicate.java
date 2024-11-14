package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockIDPredicate implements BlockPredicate {
	public record PropertyObject(Property<?> property, Object value) {
	}

	private final ResourceLocation id;
	private final Map<String, String> properties;
	private Block cachedBlock;
	private List<PropertyObject> cachedProperties;

	public BlockIDPredicate(ResourceLocation i) {
		id = i;
		properties = new HashMap<>();
	}

	@Override
	public String toString() {
		if (properties.isEmpty()) {
			return id.toString();
		}

		var sb = new StringBuilder(id.toString());
		sb.append('[');

		var first = true;

		for (var entry : properties.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}

			sb.append(entry.getKey());
			sb.append('=');
			sb.append(entry.getValue());
		}

		sb.append(']');
		return sb.toString();
	}

	public BlockIDPredicate with(String key, String value) {
		properties.put(key, value);
		cachedBlock = null;
		cachedProperties = null;
		return this;
	}

	private Block getBlock() {
		if (cachedBlock == null) {
			cachedBlock = BuiltInRegistries.BLOCK.get(id);

			if (cachedBlock == null) {
				cachedBlock = Blocks.AIR;
			}
		}

		return cachedBlock;
	}

	public List<PropertyObject> getBlockProperties() {
		if (cachedProperties == null) {
			cachedProperties = new LinkedList<>();

			Map<String, Property<?>> map = new HashMap<>();

			for (var property : getBlock().defaultBlockState().getProperties()) {
				map.put(property.getName(), property);
			}

			for (var entry : properties.entrySet()) {
				var property = map.get(entry.getKey());

				if (property != null) {
					Optional<?> o = property.getValue(entry.getValue());

					if (o.isPresent()) {
						var po = new PropertyObject(property, o.get());
						cachedProperties.add(po);
					}
				}
			}
		}

		return cachedProperties;
	}

	public BlockState getBlockState() {
		var state = getBlock().defaultBlockState();

		for (var object : getBlockProperties()) {
			state = state.setValue(object.property, Cast.to(object.value));
		}

		return state;
	}

	@Override
	public boolean check(LevelBlock b) {
		return getBlock() != Blocks.AIR && checkState(b.getBlockState());
	}

	public boolean checkState(BlockState state) {
		if (state.getBlock() != getBlock()) {
			return false;
		}

		if (properties.isEmpty()) {
			return true;
		}

		for (var object : getBlockProperties()) {
			if (!state.getValue(object.property).equals(object.value)) {
				return false;
			}
		}

		return true;
	}
}
