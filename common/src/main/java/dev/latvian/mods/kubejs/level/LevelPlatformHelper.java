package dev.latvian.mods.kubejs.level;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LevelPlatformHelper {
	@ExpectPlatform
	public static InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		throw new AssertionError();
	}

	@ExpectPlatform
	@Nullable
	public static BiomeFilter ofStringAdditional(String s) {
		throw new AssertionError();
	}

	@ExpectPlatform
	@Nullable
	public static BiomeFilter ofMapAdditional(Map<String, Object> map) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean areCapsCompatible(ItemStack a, ItemStack b) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static ItemStack getContainerItem(ItemStack stack) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static double getReachDistance(LivingEntity livingEntity) {
		throw new AssertionError();
	}
}
