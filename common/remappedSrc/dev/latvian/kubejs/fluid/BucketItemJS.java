package dev.latvian.kubejs.fluid;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;

/**
 * @author LatvianModder
 */
public class BucketItemJS extends BucketItem
{
	public final FluidBuilder properties;

	public BucketItemJS(FluidBuilder b)
	{
		super(() -> b.stillFluid, new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
		properties = b;
	}
}