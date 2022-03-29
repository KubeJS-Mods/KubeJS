package dev.latvian.mods.kubejs.item.fabric;

import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.world.item.BucketItem;

public class KubeJSItemEventHandlerImpl {
	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		return new BucketItemJS(builder, itemBuilder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder properties;

		public BucketItemJS(FluidBuilder b, FluidBucketItemBuilder itemBuilder) {
			super(b.get(), itemBuilder.createItemProperties());
			properties = b;
		}
	}
}