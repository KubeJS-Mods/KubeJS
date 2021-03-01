package dev.latvian.kubejs.fluid.forge;

import dev.latvian.kubejs.fluid.FluidBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class KubeJSFluidEventHandlerImpl {
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		if (source) {
			return new ForgeFlowingFluid.Source(createProperties(builder));
		} else {
			return new ForgeFlowingFluid.Flowing(createProperties(builder));
		}
	}

	public static ForgeFlowingFluid.Properties createProperties(FluidBuilder fluidBuilder) {
		if (fluidBuilder.extraPlatformInfo != null) {
			return (ForgeFlowingFluid.Properties) fluidBuilder.extraPlatformInfo;
		}
		FluidAttributes.Builder builder = FluidAttributes.builder(
				new ResourceLocation(fluidBuilder.stillTexture),
				new ResourceLocation(fluidBuilder.flowingTexture))
				.translationKey("fluid." + fluidBuilder.id.getNamespace() + "." + fluidBuilder.id.getPath())
				.color(fluidBuilder.color)
				.rarity(fluidBuilder.rarity.rarity)
				.density(fluidBuilder.density)
				.viscosity(fluidBuilder.viscosity)
				.luminosity(fluidBuilder.luminosity)
				.temperature(fluidBuilder.temperature);

		if (fluidBuilder.isGaseous) {
			builder.gaseous();
		}

		ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(() -> fluidBuilder.stillFluid, () -> fluidBuilder.flowingFluid, builder).bucket(() -> fluidBuilder.bucketItem).block(() -> fluidBuilder.block);
		fluidBuilder.extraPlatformInfo = properties;
		return properties;
	}
}
