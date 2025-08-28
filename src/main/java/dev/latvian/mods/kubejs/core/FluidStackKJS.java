package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.component.MutableDataComponentHolderFunctions;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.kubejs.web.RelativeURL;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Map;

public interface FluidStackKJS extends
	Replaceable,
	SpecialEquality,
	WithCodec,
	FluidLike,
	FluidMatch,
	MutableDataComponentHolderFunctions,
	RegistryObjectKJS<Fluid> {
	default FluidStack kjs$self() {
		return (FluidStack) (Object) this;
	}

	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		if (o instanceof CharSequence) {
			return kjs$getId().equals(ID.string(o.toString()));
		} else if (o instanceof ResourceLocation) {
			return kjs$getIdLocation().equals(o);
		} else if (o instanceof FluidStack s) {
			return kjs$equalsIgnoringCount(s);
		}

		return kjs$equalsIgnoringCount(FluidWrapper.wrap(RegistryAccessContainer.of(cx), o));
	}

	default boolean kjs$equalsIgnoringCount(FluidStack stack) {
		var self = kjs$self();

		if (self == stack) {
			return true;
		} else if (self.isEmpty()) {
			return stack.isEmpty();
		}

		return FluidStack.isSameFluidSameComponents(self, stack);
	}

	@Override
	default ResourceKey<Registry<Fluid>> kjs$getRegistryId() {
		return Registries.FLUID;
	}

	@Override
	default Registry<Fluid> kjs$getRegistry() {
		return BuiltInRegistries.FLUID;
	}

	@Override
	default ResourceLocation kjs$getIdLocation() {
		return kjs$self().getFluid().kjs$getIdLocation();
	}

	@Override
	default Holder<Fluid> kjs$asHolder() {
		return kjs$self().getFluid().kjs$asHolder();
	}

	@Override
	default ResourceKey<Fluid> kjs$getKey() {
		return kjs$self().getFluid().kjs$getKey();
	}

	@Override
	default String kjs$getId() {
		return kjs$self().getFluid().kjs$getId();
	}

	@Override
	default String kjs$getMod() {
		return kjs$self().getFluid().kjs$getMod();
	}

	@Override
	default int kjs$getAmount() {
		return kjs$self().getAmount();
	}

	@Override
	default boolean kjs$isEmpty() {
		return kjs$self().isEmpty();
	}

	@Override
	default Fluid kjs$getFluid() {
		return kjs$self().getFluid();
	}

	@Override
	default FluidLike kjs$copy(int amount) {
		return (FluidLike) (Object) kjs$self().copyWithAmount(amount);
	}

	@Override
	default Codec<?> getCodec(Context cx) {
		return FluidStack.CODEC;
	}

	@Override
	default Object replaceThisWith(RecipeScriptContext cx, Object with) {
		var t = kjs$self();
		var r = FluidWrapper.wrap(cx.registries(), with);

		if (!FluidStack.isSameFluidSameComponents(t, r)) {
			r.setAmount(t.getAmount());
			return r;
		}

		return this;
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidStack s, boolean exact) {
		return kjs$self().getFluid() == s.getFluid();
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidIngredient ingredient, boolean exact) {
		return ingredient.test(kjs$self());
	}

	default RelativeURL kjs$getWebIconURL(DynamicOps<Tag> ops, int size) {
		var url = "/img/" + size + "/fluid/" + ID.url(kjs$getIdLocation());
		var c = DataComponentWrapper.patchToString(new StringBuilder(), ops, DataComponentWrapper.visualPatch(kjs$self().getComponentsPatch())).toString();
		return new RelativeURL(url, c.equals("[]") ? Map.of() : Map.of("components", c.substring(1, c.length() - 1)));
	}
}
