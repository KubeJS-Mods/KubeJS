package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.docs.MinecraftClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FluidWrapper {
	public static final ResourceLocation WATER_ID = new ResourceLocation("minecraft:water");
	public static final ResourceLocation LAVA_ID = new ResourceLocation("minecraft:lava");

	public FluidStackJS of(ResourceLocation o) {
		return FluidStackJS.of(o);
	}

	public FluidStackJS of(ResourceLocation o, Object amountOrNBT) {
		return FluidStackJS.of(o, amountOrNBT);
	}

	public FluidStackJS of(ResourceLocation o, int amount, Object nbt) {
		return FluidStackJS.of(o, amount, nbt);
	}

	public FluidStackJS water() {
		return new UnboundFluidStackJS(WATER_ID);
	}

	public FluidStackJS lava() {
		return new UnboundFluidStackJS(LAVA_ID);
	}

	public FluidStackJS water(int amount) {
		FluidStackJS fs = new UnboundFluidStackJS(WATER_ID);
		fs.withAmount(amount);
		return fs;
	}

	public FluidStackJS lava(int amount) {
		FluidStackJS fs = new UnboundFluidStackJS(LAVA_ID);
		fs.withAmount(amount);
		return fs;
	}

	@MinecraftClass
	public Fluid getType(ResourceLocation id) {
		return KubeJSRegistries.fluids().get(id);
	}

	public List<String> getTypes() {
		List<String> types = new ArrayList<>();

		for (ResourceLocation id : KubeJSRegistries.fluids().getIds()) {
			types.add(id.toString());
		}

		return types;
	}

	public FluidStackJS getEmpty() {
		return EmptyFluidStackJS.INSTANCE;
	}

	public boolean exists(ResourceLocation id) {
		return KubeJSRegistries.fluids().contains(id);
	}
}