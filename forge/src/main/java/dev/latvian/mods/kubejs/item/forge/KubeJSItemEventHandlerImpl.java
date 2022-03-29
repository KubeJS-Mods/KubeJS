package dev.latvian.mods.kubejs.item.forge;

import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

public class KubeJSItemEventHandlerImpl {
	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		return new BucketItemJS(builder, itemBuilder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder fluidBuilder;

		public BucketItemJS(FluidBuilder b, FluidBucketItemBuilder itemBuilder) {
			super(b, itemBuilder.createItemProperties());
			fluidBuilder = b;
		}

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
			return new FluidBucketWrapper(stack);
		}
	}
}
