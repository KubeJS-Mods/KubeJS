package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import dev.latvian.mods.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockWrapper {
	public static Map<String, MaterialJS> getMaterial() {
		return MaterialListJS.INSTANCE.map;
	}

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

	public static Map<String, Direction> getFacing() {
		if (facingMap == null) {
			facingMap = new HashMap<>(6);

			for (var facing : Direction.values()) {
				facingMap.put(facing.getSerializedName(), facing);
			}
		}

		return facingMap;
	}

	public static Block getBlock(ResourceLocation id) {
		return KubeJSRegistries.blocks().get(id);
	}

	@Nullable
	public static ResourceLocation getId(Block block) {
		return KubeJSRegistries.blocks().getId(block);
	}

	public static List<String> getTypeList() {
		List<String> list = new ArrayList<>();

		for (var block : KubeJSRegistries.blocks().getIds()) {
			list.add(block.toString());
		}

		return list;
	}

	public static List<ResourceLocation> getTaggedIds(ResourceLocation tag) {
		return Util.make(new LinkedList<>(), list -> {
			for (var holder : Registry.BLOCK.getTagOrEmpty(Tags.block(tag))) {
				holder.unwrapKey().map(ResourceKey::location).ifPresent(list::add);
			}
		});
	}
}