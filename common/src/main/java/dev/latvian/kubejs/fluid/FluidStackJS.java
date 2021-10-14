package dev.latvian.kubejs.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.ChangeListener;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public abstract class FluidStackJS implements WrappedJS, Copyable, ChangeListener<MapJS> {
	public static FluidStackJS of(@Nullable Object o) {
		if (o == null) {
			return EmptyFluidStackJS.INSTANCE;
		} else if (o instanceof FluidStackJS) {
			return (FluidStackJS) o;
		} else if (o instanceof FluidStack) {
			return new BoundFluidStackJS((FluidStack) o);
		} else if (o instanceof Fluid) {
			UnboundFluidStackJS f = new UnboundFluidStackJS(Registries.getId((Fluid) o, Registry.FLUID_REGISTRY));
			return f.isEmpty() ? EmptyFluidStackJS.INSTANCE : f;
		} else if (o instanceof JsonElement) {
			return fromJson((JsonElement) o);
		} else if (o instanceof CharSequence || o instanceof ResourceLocation) {
			String s = o.toString();

			if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
				return EmptyFluidStackJS.INSTANCE;
			}

			String[] s1 = s.split(" ", 2);
			return new UnboundFluidStackJS(new ResourceLocation(s1[0])).withAmount(UtilsJS.parseLong(s1.length == 2 ? s1[1] : "", FluidStack.bucketAmount()));
		}

		MapJS map = MapJS.of(o);

		if (map != null && map.containsKey("fluid")) {
			FluidStackJS stack = new UnboundFluidStackJS(new ResourceLocation(map.get("fluid").toString()));

			if (map.get("amount") instanceof Number) {
				stack.setAmount(((Number) map.get("amount")).longValue());
			}

			if (map.containsKey("nbt")) {
				stack.setNbt(map.get("nbt"));
			}

			return stack;
		}

		return EmptyFluidStackJS.INSTANCE;
	}

	public static FluidStackJS of(@Nullable Object o, @Nullable Object amountOrNBT) {
		FluidStackJS stack = of(o);
		Object n = UtilsJS.wrap(amountOrNBT, JSObjectType.ANY);

		if (n instanceof Number) {
			stack.setAmount(((Number) n).longValue());
		} else if (n instanceof MapJS) {
			stack.setNbt(n);
		}

		return stack;
	}

	public static FluidStackJS of(@Nullable Object o, long amount, @Nullable Object nbt) {
		FluidStackJS stack = of(o);
		stack.setAmount(amount);
		stack.setNbt(nbt);
		return stack;
	}

	public static FluidStackJS fromJson(JsonElement e) {
		if (!e.isJsonObject()) {
			return of(e.getAsString());
		}

		JsonObject json = e.getAsJsonObject();

		FluidStackJS fluid = of(json.get("fluid").getAsString());

		if (fluid.isEmpty()) {
			throw new RecipeExceptionJS(json + " is not a valid fluid!");
		}

		long amount = FluidStack.bucketAmount();
		Object nbt = null;

		if (json.has("amount")) {
			amount = json.get("amount").getAsInt();
		} else if (json.has("count")) {
			amount = json.get("count").getAsInt();
		}

		if (json.has("nbt")) {
			if (json.get("nbt").isJsonObject()) {
				nbt = MapJS.of(json.get("nbt"));
			} else {
				try {
					nbt = TagParser.parseTag(json.get("nbt").getAsString());
				} catch (CommandSyntaxException ex) {
					return EmptyFluidStackJS.INSTANCE;
				}
			}
		}

		return FluidStackJS.of(fluid, amount, nbt);
	}

	public abstract String getId();

	public Collection<ResourceLocation> getTags() {
		return Tags.byFluid(getFluid());
	}

	public boolean hasTag(ResourceLocation tag) {
		return Tags.fluids().getTagOrEmpty(tag).contains(getFluid());
	}

	public Fluid getFluid() {
		Fluid f = KubeJSRegistries.fluids().get(new ResourceLocation(getId()));
		return f == null ? Fluids.EMPTY : f;
	}

	public abstract FluidStack getFluidStack();

	public boolean isEmpty() {
		return getAmount() <= 0;
	}

	public abstract long getAmount();

	public abstract void setAmount(long amount);

	public final FluidStackJS withAmount(long amount) {
		if (amount <= 0) {
			return EmptyFluidStackJS.INSTANCE;
		}

		FluidStackJS fs = copy();
		fs.setAmount(amount);
		return fs;
	}

	@Deprecated
	public final FluidStackJS amount(long amount) {
		return withAmount(amount);
	}

	@Nullable
	public abstract MapJS getNbt();

	public abstract void setNbt(@Nullable Object nbt);

	public final FluidStackJS withNBT(@Nullable Object nbt) {
		FluidStackJS fs = copy();
		fs.setNbt(nbt);
		return fs;
	}

	@Deprecated
	public final FluidStackJS nbt(@Nullable Object nbt) {
		return withNBT(nbt);
	}

	@Override
	public abstract FluidStackJS copy();

	@Override
	public int hashCode() {
		return Objects.hash(getFluid(), getNbt());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharSequence) {
			return getId().equals(o.toString());
		}

		FluidStackJS f = FluidStackJS.of(o);

		if (f.isEmpty()) {
			return false;
		}

		return getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
	}

	public boolean strongEquals(Object o) {
		FluidStackJS f = of(o);

		if (f.isEmpty()) {
			return false;
		}

		return getAmount() == f.getAmount() && getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
	}

	public String toString() {
		long amount = getAmount();
		MapJS nbt = getNbt();

		StringBuilder builder = new StringBuilder();
		builder.append("Fluid.of('");
		builder.append(getId());

		if (amount != FluidStack.bucketAmount()) {
			builder.append(", ");
			builder.append(amount);
		}

		if (nbt != null) {
			builder.append(", ");
			nbt.appendString(builder);
		}

		builder.append("')");
		return builder.toString();
	}

	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.addProperty("fluid", getId());

		if (getAmount() != FluidStack.bucketAmount()) {
			o.addProperty("amount", getAmount());
		}

		if (getNbt() != null) {
			o.add("nbt", getNbt().toJson());
		}

		return o;
	}

	public CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		getFluidStack().write(tag);
		return tag;
	}

	@Override
	public void onChanged(MapJS o) {
	}
}