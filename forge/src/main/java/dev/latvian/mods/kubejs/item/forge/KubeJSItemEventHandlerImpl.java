package dev.latvian.mods.kubejs.item.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

public class KubeJSItemEventHandlerImpl {
	public static BucketItem buildBucket(FluidBuilder builder) {
		return new BucketItemJS(builder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder properties;

		public BucketItemJS(FluidBuilder b) {
			super(() -> b.stillFluid, new Properties().stacksTo(1).tab(KubeJS.tab));
			properties = b;
		}

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
			return new FluidBucketWrapper(stack);
		}
	}
}
