package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.item.ItemHandler;
import dev.latvian.mods.kubejs.level.LevelPlatformHelper;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.forge.BiomeDictionaryFilter;
import dev.latvian.mods.kubejs.level.gen.forge.BiomeDictionaryWrapper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Map;

public class LevelPlatformHelperImpl implements LevelPlatformHelper {
	public InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		var handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);

		if (handler != null) {
			if (handler instanceof IItemHandlerModifiable modifiableHandler) {
				return new InventoryJS(new ItemHandler.Mutable() {
					@Override
					public void setStackInSlot(int i, @Nonnull ItemStack itemStack) {
						modifiableHandler.setStackInSlot(i, itemStack);
					}

					@Override
					public int getSlots() {
						return handler.getSlots();
					}

					@Override
					public ItemStack getStackInSlot(int i) {
						return handler.getStackInSlot(i);
					}

					@Override
					public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
						return handler.insertItem(i, itemStack, b);
					}

					@Override
					public ItemStack extractItem(int i, int i1, boolean b) {
						return handler.extractItem(i, i1, b);
					}

					@Override
					public int getSlotLimit(int i) {
						return handler.getSlotLimit(i);
					}

					@Override
					public boolean isItemValid(int i, ItemStack itemStack) {
						return handler.isItemValid(i, itemStack);
					}
				});
			}

			return new InventoryJS(new ItemHandler() {
				@Override
				public int getSlots() {
					return handler.getSlots();
				}

				@Override
				public ItemStack getStackInSlot(int i) {
					return handler.getStackInSlot(i);
				}

				@Override
				public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
					return handler.insertItem(i, itemStack, b);
				}

				@Override
				public ItemStack extractItem(int i, int i1, boolean b) {
					return handler.extractItem(i, i1, b);
				}

				@Override
				public int getSlotLimit(int i) {
					return handler.getSlotLimit(i);
				}

				@Override
				public boolean isItemValid(int i, ItemStack itemStack) {
					return handler.isItemValid(i, itemStack);
				}
			});
		}

		return null;
	}

	public BiomeFilter ofStringAdditional(String s) {
		return switch (s.charAt(0)) {
			case '#' -> {
				ConsoleJS.STARTUP.error("Biome Tag filters are currently not supported on Forge!");
				// TODO: Biome Tags (needs MinecraftForge/MinecraftForge#8251?)
				yield null;
			}
			case '$' -> new BiomeDictionaryFilter(BiomeDictionaryWrapper.getBiomeType(s.substring(1)));
			default -> null;
		};
	}

	public BiomeFilter ofMapAdditional(Map<String, Object> map) {
		if (map.containsKey("biome_type")) {
			var type = BiomeDictionaryWrapper.getBiomeType(map.get("biome_type"));
			return new BiomeDictionaryFilter(type);
		}
		return null;
	}

	public boolean areCapsCompatible(ItemStack a, ItemStack b) {
		return a.areCapsCompatible(b);
	}

	public ItemStack getContainerItem(ItemStack stack) {
		return stack.getContainerItem();
	}

	public double getReachDistance(LivingEntity livingEntity) {
		return livingEntity.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
	}
}
