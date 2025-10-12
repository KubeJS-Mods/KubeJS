package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.holder.HolderWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record RegistryComponent<T>(Registry<T> registry, @Nullable RegistryType<T> regType, Codec<Holder<T>> codec, TypeInfo typeInfo) implements RecipeComponent<Holder<T>> {
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<RegistryComponent<?>>dynamic(KubeJS.id("registry_element"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(c -> c.registry.key())
	).apply(instance, key -> new RegistryComponent<>(ctx.registries(), key))));

	@SuppressWarnings({"unchecked", "rawtypes"})
	public RegistryComponent(RegistryAccessContainer registries, @Nullable RegistryType<T> regType, ResourceKey key) {
		this(
			(Registry) registries.access().registry(key).orElseThrow(),
			regType,
			RegistryFixedCodec.create(key),
			regType == null || regType.type() == TypeInfo.STRING ? TypeInfo.STRING : TypeInfo.STRING.or(regType.type())
		);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public RegistryComponent(RegistryAccessContainer registries, ResourceKey key) {
		this(registries, (RegistryType) RegistryType.ofKey(key), key);
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Holder<T> wrap(RecipeScriptContext cx, Object from) {
		if (registry == BuiltInRegistries.ITEM) {
			if (from instanceof ItemStack is) {
				return (Holder<T>) is.getItem().builtInRegistryHolder();
			} else if (from instanceof Item item) {
				return (Holder<T>) item.builtInRegistryHolder();
			} else {
				return (Holder<T>) ItemWrapper.wrap(cx.cx(), from).getItemHolder();
			}
		} else if (registry == BuiltInRegistries.FLUID) {
			if (from instanceof FluidStack fs) {
				return (Holder<T>) fs.getFluid().builtInRegistryHolder();
			} else if (from instanceof Fluid fluid) {
				return (Holder<T>) fluid.builtInRegistryHolder();
			} else {
				return (Holder<T>) FluidWrapper.wrap(cx.registries(), from).getFluidHolder();
			}
		} else if (regType != null) {
			return (Holder<T>) HolderWrapper.wrap((KubeJSContext) cx.cx(), from, regType.type());
		} else if (from instanceof ResourceKey<?> key) {
			return registry.getHolderOrThrow((ResourceKey) key);
		} else if (from instanceof CharSequence || from instanceof ResourceLocation) {
			return registry.getHolderOrThrow(ResourceKey.create(registry.key(), ID.mc(from.toString())));
		} else {
			throw new IllegalStateException("Missing key in " + registry.key() + ": " + from);
		}
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, Object from) {
		return (regType != null && regType.baseClass().isInstance(from)) || (from instanceof CharSequence && ID.mc(from.toString()) != null) || (from instanceof JsonPrimitive json && json.isString() && ID.mc(json.getAsString()) != null);
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Holder<T> value) {
		var id = value.getKey();

		if (id != null) {
			builder.append(id.location());
		}
	}

	@Override
	public String toString() {
		return "registry_element<" + ID.reduce(registry.key().location()) + ">";
	}
}
