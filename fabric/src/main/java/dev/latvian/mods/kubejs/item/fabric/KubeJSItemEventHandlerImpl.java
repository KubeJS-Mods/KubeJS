package dev.latvian.mods.kubejs.item.fabric;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.world.item.BucketItem;

public class KubeJSItemEventHandlerImpl {
	public static BucketItem buildBucket(FluidBuilder builder) {
		return new BucketItemJS(builder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder properties;

		public BucketItemJS(FluidBuilder b) {
			super(b.stillFluid, new Properties().stacksTo(1).tab(KubeJS.tab));
			properties = b;
		}
	}
}