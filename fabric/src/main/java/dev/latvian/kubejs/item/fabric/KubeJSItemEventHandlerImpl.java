package dev.latvian.kubejs.item.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.world.item.BucketItem;

public class KubeJSItemEventHandlerImpl {
	public static ItemJS buildItem(ItemBuilder builder) {
		return new FabricItemJS(builder);
	}

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