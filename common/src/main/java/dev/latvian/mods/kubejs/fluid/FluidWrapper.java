package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.nbt.CompoundTag;
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

	public static FluidStackJS of(FluidStackJS o) {
		return FluidStackJS.of(o);
	}

	public static FluidStackJS of(FluidStackJS o, int amount) {
		o.setAmount(amount);
		return o;
	}

	public static FluidStackJS of(FluidStackJS o, CompoundTag nbt) {
		o.setNbt(nbt);
		return o;
	}

	public static FluidStackJS of(FluidStackJS o, int amount, CompoundTag nbt) {
		o.setAmount(amount);
		o.setNbt(nbt);
		return o;
	}

	public static FluidStackJS water() {
		return new UnboundFluidStackJS(WATER_ID);
	}

	public static FluidStackJS lava() {
		return new UnboundFluidStackJS(LAVA_ID);
	}

	public static FluidStackJS water(int amount) {
		FluidStackJS fs = new UnboundFluidStackJS(WATER_ID);
		fs.setAmount(amount);
		return fs;
	}

	public static FluidStackJS lava(int amount) {
		FluidStackJS fs = new UnboundFluidStackJS(LAVA_ID);
		fs.setAmount(amount);
		return fs;
	}

	public static Fluid getType(ResourceLocation id) {
		return KubeJSRegistries.fluids().get(id);
	}

	public static List<String> getTypes() {
		List<String> types = new ArrayList<>();

		for (var id : KubeJSRegistries.fluids().getIds()) {
			types.add(id.toString());
		}

		return types;
	}

	public static FluidStackJS getEmpty() {
		return EmptyFluidStackJS.INSTANCE;
	}

	public static boolean exists(ResourceLocation id) {
		return KubeJSRegistries.fluids().contains(id);
	}

	public static ResourceLocation getId(Fluid fluid) {
		return KubeJSRegistries.fluids().getId(fluid);
	}
}