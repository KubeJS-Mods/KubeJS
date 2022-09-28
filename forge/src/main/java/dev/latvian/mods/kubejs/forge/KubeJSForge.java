package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.block.entity.BasicBlockEntity;
import dev.latvian.mods.kubejs.block.entity.ablities.BlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.EnergyBlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.FluidBlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.ItemBlockAbility;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.platform.ingredient.IngredientPlatformHelperImpl;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		EventBuses.registerModEventBus(KubeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::initRegistries);
		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::livingDrops);
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, KubeJSForge::onAttachCapabilities);

		if (!CommonProperties.get().serverOnly) {
			ForgeMod.enableMilkFluid();
			IngredientPlatformHelperImpl.register();
		}

	}

	private static void initRegistries(RegisterEvent event) {
		KubeJSRegistries.init(event.getRegistryKey());
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		KubeJS.instance.loadComplete();
	}

	private static void itemDestroyed(PlayerDestroyItemEvent event) {
		if (event.getEntity() instanceof ServerPlayer) {
			ForgeKubeJSEvents.ITEM_DESTROYED.post(ItemWrapper.getId(event.getOriginal().getItem()), new ItemDestroyedEventJS(event));
		}
	}

	private static void livingDrops(LivingDropsEvent event) {
		if (event.getEntity().level.isClientSide()) {
			return;
		}

		var e = new LivingEntityDropsEventJS(event);

		if (ForgeKubeJSEvents.ENTITY_DROPS.post(e.getEntity().getType(), e)) {
			event.setCanceled(true);
		} else if (e.eventDrops != null) {
			event.getDrops().clear();
			event.getDrops().addAll(e.eventDrops);
		}
	}

	public static class SimpleCapProvider implements ICapabilityProvider {
		public boolean lazyCapsInit = false;
		public Map<Capability<?>, LazyOptional<?>> capablities = new HashMap<>();
		public LazyOptional<?> lazyGetCap(Capability<?> cap) {
			if (!lazyCapsInit) {
				for (var ability : be.blockAbilities.values()) {
					if (ability instanceof ItemBlockAbility) {
						capablities.put(
								ForgeCapabilities.ITEM_HANDLER,
								LazyOptional.of(() -> new IItemHandler() {
									private final BlockAbility<ItemStack> abl = UtilsJS.cast(ability);
									private final List<String> indexedSlots = abl.getSlotMap().keySet().stream().toList();

									@Override
									public int getSlots() {
										return indexedSlots.size();
									}

									@Override
									public @NotNull ItemStack getStackInSlot(int i) {
										return abl.get(indexedSlots.get(i)).getRaw();
									}

									@Override
									public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
										var ret = abl.insert(indexedSlots.get(i), arg, bl);
										if (!bl) be.setChanged();
										return ret;
									}

									@Override
									public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
										var ret = abl.extract(indexedSlots.get(i), j, bl);
										if (!bl) be.setChanged();
										return ret;
									}

									@Override
									public int getSlotLimit(int i) {
										return (int) abl.getMax(indexedSlots.get(i));
									}

									@Override
									public boolean isItemValid(int i, @NotNull ItemStack arg) {
										return abl.canInsert(indexedSlots.get(i), arg);
									}
								})
						);
					} else if (ability instanceof FluidBlockAbility) {
						capablities.put(
								ForgeCapabilities.FLUID_HANDLER,
								LazyOptional.of(() -> new IFluidHandler() {
									private final BlockAbility<FluidStack> abl = UtilsJS.cast(ability);
									private final List<String> indexedSlots = abl.getSlotMap().keySet().stream().toList();

									@Override
									public int getTanks() {
										return indexedSlots.size();
									}

									@Override
									public @NotNull FluidStack getFluidInTank(int i) {
										return abl.get(indexedSlots.get(i)).getRaw();
									}

									@Override
									public int getTankCapacity(int i) {
										return (int) abl.getMax(indexedSlots.get(i));
									}

									@Override
									public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
										return abl.canInsert(indexedSlots.get(i), fluidStack);
									}

									@Override
									public int fill(FluidStack fluidStack, FluidAction fluidAction) {
										var ret = abl.insert(indexedSlots.get(0), fluidStack, fluidAction.simulate()).getAmount();
										if (!fluidAction.simulate()) be.setChanged();
										return ret;
									}

									@Override
									public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
										var slot = abl.get(indexedSlots.get(0));
										if (slot.compatible(fluidStack)) {
											var ret = abl.extract(indexedSlots.get(0), fluidStack.getAmount(), fluidAction.simulate()).getAmount();
											if (!fluidAction.simulate()) be.setChanged();
											return slot.withCount(ret).getRaw();
										}
										return slot.withCount(0).getRaw();
									}

									@Override
									public @NotNull FluidStack drain(int i, FluidAction fluidAction) {
										var ret = abl.extract(indexedSlots.get(0), i, fluidAction.simulate()).getAmount();
										if (!fluidAction.simulate()) be.setChanged();
										return abl.get(indexedSlots.get(0)).withCount(ret).getRaw();
									}
								})
						);
					} else if (ability instanceof EnergyBlockAbility) {
						capablities.put(
								ForgeCapabilities.FLUID_HANDLER,
								LazyOptional.of(() -> new IEnergyStorage() {
									private final BlockAbility<Integer> abl = UtilsJS.cast(ability);
									private final List<String> indexedSlots = abl.getSlotMap().keySet().stream().toList();

									@Override
									public int receiveEnergy(int i, boolean bl) {
										var ret = abl.grow(indexedSlots.get(0), i, bl);
										be.setChanged();
										return ret;
									}

									@Override
									public int extractEnergy(int i, boolean bl) {
										var ret = abl.shrink(indexedSlots.get(0), i, bl);
										be.setChanged();
										return ret;
									}

									@Override
									public int getEnergyStored() {
										return (int) abl.get(indexedSlots.get(0)).getCount();
									}

									@Override
									public int getMaxEnergyStored() {
										return (int) abl.getMax(indexedSlots.get(0));
									}

									@Override
									public boolean canExtract() {
										// TODO: output
										return true;
									}

									@Override
									public boolean canReceive() {
										return abl.canInsert(indexedSlots.get(0), 0);
									}
								})
						);
					}

				}
				lazyCapsInit = true;
			}
			return capablities.get(cap);
		}
		public BasicBlockEntity be;

		public SimpleCapProvider(BasicBlockEntity be) {
			this.be = be;
		}

		@Override
		public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
			var cap = lazyGetCap(capability);
			return cap != null ? cap.cast() : LazyOptional.empty();
		}
	}

	private static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
		if (event.getObject() instanceof BasicBlockEntity be) {
			event.addCapability(BlockEntityType.getKey(be.getType()), new SimpleCapProvider(be));
		}
	}
}
