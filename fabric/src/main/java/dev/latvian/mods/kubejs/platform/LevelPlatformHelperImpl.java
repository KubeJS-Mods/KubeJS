package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.level.LevelPlatformHelper;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.fabric.BiomeTagFilter;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class LevelPlatformHelperImpl implements LevelPlatformHelper {
	public InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		return null;
	}

	public BiomeFilter ofStringAdditional(String s) {
		if (s.charAt(0) == '#') {
			var id = UtilsJS.getMCID(s.substring(1));
			return new BiomeTagFilter(TagKey.create(Registry.BIOME_REGISTRY, id));
		}
		return null;
	}

	public BiomeFilter ofMapAdditional(Map<String, Object> map) {
		if (map.containsKey("tag")) {
			var tag = UtilsJS.getMCID(map.get("tag").toString());
			return new BiomeTagFilter(TagKey.create(Registry.BIOME_REGISTRY, tag));
		}
		return null;
	}

	public boolean areCapsCompatible(ItemStack a, ItemStack b) {
		return true;
	}

	public double getReachDistance(LivingEntity livingEntity) {
		return 5;
	}
}
