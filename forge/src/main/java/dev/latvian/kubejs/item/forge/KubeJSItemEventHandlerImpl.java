package dev.latvian.kubejs.item.forge;

import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;

public class KubeJSItemEventHandlerImpl
{
	public static ItemJS buildItem(ItemBuilder builder)
	{
		return new ItemJS(builder);
	}

	public static BucketItem buildBucket(FluidBuilder builder)
	{
		return new BucketItemJS(builder);
	}

	public static class BucketItemJS extends BucketItem
	{
		public final FluidBuilder properties;

		public BucketItemJS(FluidBuilder b)
		{
			super(() -> b.stillFluid, new Properties().stacksTo(1).tab(ItemGroup.TAB_MISC));
			properties = b;
		}
	}
}
