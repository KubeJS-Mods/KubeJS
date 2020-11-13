package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.util.BuilderBase;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class FluidBuilder extends BuilderBase
{
	public String stillTexture;
	public String flowingTexture;
	public int color = 0xFFFFFFFF;
	public int luminosity = 0;
	public int density = 1000;
	public int temperature = 300;
	public int viscosity = 1000;
	public boolean isGaseous;
	public Rarity rarity = Rarity.COMMON;
	public Object extraPlatformInfo;

	public FlowingFluid stillFluid;
	public FlowingFluid flowingFluid;
	public BucketItem bucketItem;
	public LiquidBlock block;

	public FluidBuilder(String i)
	{
		super(i);
		textureThin(0xFF0000);
	}

	@Override
	public String getType()
	{
		return "fluid";
	}

	public FluidBuilder color(int c)
	{
		color = c;

		if ((color & 0xFFFFFF) == color)
		{
			color |= 0xFF000000;
		}

		return this;
	}

	public FluidBuilder textureStill(@ID String id)
	{
		stillTexture = UtilsJS.getID(id);
		return this;
	}

	public FluidBuilder textureFlowing(@ID String id)
	{
		flowingTexture = UtilsJS.getID(id);
		return this;
	}

	public FluidBuilder textureThick(int color)
	{
		return textureStill(KubeJS.MOD_ID + ":fluid/fluid_thick").textureFlowing(KubeJS.MOD_ID + ":fluid/fluid_thick_flow").color(color);
	}

	public FluidBuilder textureThin(int color)
	{
		return textureStill(KubeJS.MOD_ID + ":fluid/fluid_thin").textureFlowing(KubeJS.MOD_ID + ":fluid/fluid_thin_flow").color(color);
	}

	public FluidBuilder luminosity(int luminosity)
	{
		this.luminosity = luminosity;
		return this;
	}

	public FluidBuilder density(int density)
	{
		this.density = density;
		return this;
	}

	public FluidBuilder temperature(int temperature)
	{
		this.temperature = temperature;
		return this;
	}

	public FluidBuilder viscosity(int viscosity)
	{
		this.viscosity = viscosity;
		return this;
	}

	public FluidBuilder gaseous()
	{
		isGaseous = true;
		return this;
	}

	public FluidBuilder rarity(Rarity rarity)
	{
		this.rarity = rarity;
		return this;
	}
}