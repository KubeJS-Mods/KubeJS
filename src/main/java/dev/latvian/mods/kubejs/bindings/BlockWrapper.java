package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.Util;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Info("Various block related helper functions")
public class BlockWrapper {
	public static final TypeInfo TYPE_INFO = TypeInfo.of(Block.class);
	public static final TypeInfo STATE_TYPE_INFO = TypeInfo.of(BlockState.class);
	private static Collection<BlockState> ALL_STATE_CACHE = null;

	public static BlockIDPredicate id(ResourceLocation id) {
		return new BlockIDPredicate(id);
	}

	public static BlockIDPredicate id(ResourceLocation id, Map<String, Object> properties) {
		var b = id(id);

		for (var entry : properties.entrySet()) {
			b = b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public static BlockEntityPredicate entity(ResourceLocation id) {
		return new BlockEntityPredicate(id);
	}

	public static BlockPredicate custom(BlockPredicate predicate) {
		return predicate;
	}

	private static Map<String, Direction> facingMap;

	@Info("Get a map of direction name to Direction. Functionally identical to Direction.ALL")
	public static Map<String, Direction> getFacing() {
		if (facingMap == null) {
			facingMap = new HashMap<>(6);

			for (var facing : Direction.values()) {
				facingMap.put(facing.getSerializedName(), facing);
			}
		}

		return facingMap;
	}

	@Info("Gets a Block from a block id")
	public static Block getBlock(ResourceLocation id) {
		return RegistryInfo.BLOCK.getValue(id);
	}

	@Info("Gets a blocks id from the Block")
	@Nullable
	public static ResourceLocation getId(Block block) {
		return RegistryInfo.BLOCK.getId(block);
	}

	@Info("Gets a list of the classname of all registered blocks")
	public static List<String> getTypeList() {
		var list = new ArrayList<String>();

		for (var block : RegistryInfo.BLOCK.entrySet()) {
			list.add(block.getKey().location().toString());
		}

		return list;
	}

	@Info("Gets a list of all blocks with tags")
	public static List<ResourceLocation> getTaggedIds(ResourceLocation tag) {
		return Util.make(new LinkedList<>(), list -> {
			for (var holder : BuiltInRegistries.BLOCK.getTagOrEmpty(Tags.block(tag))) {
				holder.unwrapKey().map(ResourceKey::location).ifPresent(list::add);
			}
		});
	}

	public static Collection<BlockState> getAllBlockStates() {
		if (ALL_STATE_CACHE != null) {
			return ALL_STATE_CACHE;
		}

		var states = new HashSet<BlockState>();
		for (var block : BuiltInRegistries.BLOCK) {
			states.addAll(block.getStateDefinition().getPossibleStates());
		}

		ALL_STATE_CACHE = Collections.unmodifiableCollection(states);
		return ALL_STATE_CACHE;
	}

	public static BlockState parseBlockState(String string) {
		if (string.isEmpty()) {
			return Blocks.AIR.defaultBlockState();
		}

		try {
			return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), string, false).blockState();
		} catch (Exception ex) {
			return Blocks.AIR.defaultBlockState();
		}
	}

	public static BlockSetType setTypeOf(Context cx, Object from, TypeInfo target) {
		return switch (from) {
			case null -> null;
			case BlockSetType type -> type;
			case CharSequence charSequence -> {
				var str = charSequence.toString();

				for (var type : BlockSetType.values().toList()) {
					if (type.name().equalsIgnoreCase(str)) {
						yield type;
					}
				}

				yield null;
			}
			default -> (BlockSetType) ((RecordTypeInfo) target).wrap(cx, from, target);
		};
	}

	@Info("Parses a block state from the input string. May throw for invalid inputs!")
	static BlockState parseBlockState(Object o) {
		if (o instanceof BlockState bs) {
			return bs;
		}
		return o == null ? Blocks.AIR.defaultBlockState() : BlockWrapper.parseBlockState(o.toString());
	}
}