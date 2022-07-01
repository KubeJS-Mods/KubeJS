package dev.latvian.mods.kubejs.level;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface LevelPlatformHelper {

	Supplier<LevelPlatformHelper> INSTANCE = Suppliers.memoize(() -> {
		var serviceLoader = ServiceLoader.load(LevelPlatformHelper.class);
		return serviceLoader.findFirst().orElseThrow(() -> new RuntimeException("Could not find platform implementation for LevelPlatformHelper!"));
	});

	static LevelPlatformHelper get() {
		return INSTANCE.get();
	}

	InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing);

	@Nullable
	BiomeFilter ofStringAdditional(String s);

	@Nullable
	BiomeFilter ofMapAdditional(Map<String, Object> map);

	boolean areCapsCompatible(ItemStack a, ItemStack b);

	double getReachDistance(LivingEntity livingEntity);
}
