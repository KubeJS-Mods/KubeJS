package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public interface KubeJSFluidIngredients {
	DeferredRegister<FluidIngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.FLUID_INGREDIENT_TYPES, KubeJS.MOD_ID);

	Supplier<FluidIngredientType<?>> REGEX = REGISTRY.register("regex", () -> new FluidIngredientType<>(RegExFluidIngredient.CODEC, RegExFluidIngredient.STREAM_CODEC));
	Supplier<FluidIngredientType<?>> NAMESPACE = REGISTRY.register("namespace", () -> new FluidIngredientType<>(NamespaceFluidIngredient.CODEC, NamespaceFluidIngredient.STREAM_CODEC));
}
