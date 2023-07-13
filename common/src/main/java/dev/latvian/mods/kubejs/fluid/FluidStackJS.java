package dev.latvian.mods.kubejs.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FluidStackJS implements WrappedJS, InputFluid, OutputFluid {
	public static FluidStackJS of(@Nullable Object o) {
		if (o == null) {
			return EmptyFluidStackJS.INSTANCE;
		} else if (o instanceof FluidStackJS js) {
			return js;
		} else if (o instanceof FluidStack fluidStack) {
			return new BoundFluidStackJS(fluidStack);
		} else if (o instanceof Fluid fluid) {
			var f = new UnboundFluidStackJS(Registries.getId(fluid, Registry.FLUID_REGISTRY));
			return f.kjs$isEmpty() ? EmptyFluidStackJS.INSTANCE : f;
		} else if (o instanceof JsonElement json) {
			return fromJson(json);
		} else if (o instanceof CharSequence || o instanceof ResourceLocation) {
			var s = o.toString();

			if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
				return EmptyFluidStackJS.INSTANCE;
			}

			var s1 = s.split(" ", 2);
			return new UnboundFluidStackJS(new ResourceLocation(s1[0])).withAmount(UtilsJS.parseLong(s1.length == 2 ? s1[1] : "", FluidStack.bucketAmount()));
		}

		var map = MapJS.of(o);

		if (map != null && map.containsKey("fluid")) {
			FluidStackJS stack = new UnboundFluidStackJS(new ResourceLocation(map.get("fluid").toString()));

			if (map.get("amount") instanceof Number num) {
				stack.setAmount(num.longValue());
			}

			if (map.containsKey("nbt")) {
				stack.setNbt(NBTUtils.toTagCompound(map.get("nbt")));
			}

			return stack;
		}

		return EmptyFluidStackJS.INSTANCE;
	}

	public static FluidStackJS of(@Nullable Object o, long amount, @Nullable CompoundTag nbt) {
		var stack = of(o);
		stack.setAmount(amount);
		stack.setNbt(nbt);
		return stack;
	}

	public static FluidStackJS fromJson(JsonElement e) {
		if (!e.isJsonObject()) {
			return of(e.getAsString());
		}

		var json = e.getAsJsonObject();

		var fluid = of(json.get("fluid").getAsString());

		if (fluid.kjs$isEmpty()) {
			throw new RecipeExceptionJS(json + " is not a valid fluid!");
		}

		var amount = FluidStack.bucketAmount();
		CompoundTag nbt = null;

		if (json.has("amount")) {
			amount = json.get("amount").getAsInt();
		} else if (json.has("count")) {
			amount = json.get("count").getAsInt();
		}

		if (json.has("nbt")) {
			if (json.get("nbt").isJsonObject()) {
				nbt = NBTUtils.toTagCompound(json.get("nbt"));
			} else {
				nbt = NBTUtils.toTagCompound(json.get("nbt").getAsString());
			}
		}

		return of(fluid, amount, nbt);
	}

	private double chance = Double.NaN;

	public abstract String getId();

	public Collection<ResourceLocation> getTags() {
		return Tags.byFluid(getFluid()).map(TagKey::location).collect(Collectors.toSet());
	}

	public boolean hasTag(ResourceLocation tag) {
		return TagContext.INSTANCE.getValue().contains(Tags.fluid(tag), getFluid());
	}

	public Fluid getFluid() {
		var f = KubeJSRegistries.fluids().get(new ResourceLocation(getId()));
		return f == null ? Fluids.EMPTY : f;
	}

	public abstract FluidStack getFluidStack();

	@Override
	public abstract long kjs$getAmount();

	public abstract void setAmount(long amount);

	public final FluidStackJS withAmount(long amount) {
		if (amount <= 0) {
			return EmptyFluidStackJS.INSTANCE;
		}

		var fs = copy();
		fs.setAmount(amount);
		return fs;
	}

	@Nullable
	public abstract CompoundTag getNbt();

	public abstract void setNbt(@Nullable CompoundTag nbt);

	public final FluidStackJS withNBT(@Nullable CompoundTag nbt) {
		var fs = copy();
		fs.setNbt(nbt);
		return fs;
	}

	public FluidStackJS copy() {
		return kjs$copy(kjs$getAmount());
	}

	@Override
	public abstract FluidStackJS kjs$copy(long amount);

	public boolean hasChance() {
		return !Double.isNaN(chance);
	}

	public void removeChance() {
		setChance(Double.NaN);
	}

	public void setChance(double c) {
		chance = c;
	}

	public double getChance() {
		return chance;
	}

	public final FluidStackJS withChance(double c) {
		if (Double.isNaN(chance) && Double.isNaN(c) || chance == c) {
			return this;
		}

		var is = copy();
		is.setChance(c);
		return is;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFluid(), getNbt());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(o.toString());
		}

		var f = FluidStackJS.of(o);

		if (f.kjs$isEmpty()) {
			return false;
		}

		return getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
	}

	public boolean strongEquals(Object o) {
		var f = of(o);

		if (f.kjs$isEmpty()) {
			return false;
		}

		return kjs$getAmount() == f.kjs$getAmount() && getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
	}

	public String toString() {
		var amount = kjs$getAmount();
		var nbt = getNbt();

		var builder = new StringBuilder();
		builder.append("Fluid.of('");
		builder.append(getId());

		if (amount != FluidStack.bucketAmount()) {
			builder.append(", ");
			builder.append(amount);
		}

		if (nbt != null) {
			builder.append(", ");
			NBTUtils.quoteAndEscapeForJS(builder, nbt.toString());
		}

		builder.append("')");

		if (hasChance()) {
			builder.append(".withChance(");
			builder.append(getChance());
			builder.append(')');
		}

		return builder.toString();
	}

	public JsonObject toJson() {
		var o = new JsonObject();
		o.addProperty("fluid", getId());
		o.addProperty("amount", kjs$getAmount());

		if (getNbt() != null) {
			o.add("nbt", MapJS.json(getNbt()));
		}

		if (hasChance()) {
			o.addProperty("chance", getChance());
		}

		return o;
	}

	public CompoundTag toNBT() {
		var tag = new CompoundTag();
		getFluidStack().write(tag);
		return tag;
	}

	@Override
	public boolean matches(FluidLike other) {
		return other instanceof FluidStackJS fs && getFluid() == fs.getFluid() && Objects.equals(getNbt(), fs.getNbt());
	}
}