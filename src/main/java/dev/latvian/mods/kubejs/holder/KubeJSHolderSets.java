package dev.latvian.mods.kubejs.holder;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;

public interface KubeJSHolderSets {
	DeferredRegister<HolderSetType> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.HOLDER_SET_TYPES, KubeJS.MOD_ID);

	Holder<HolderSetType> REGEX = REGISTRY.register("regex", () -> RegExHolderSet::codec);
	Holder<HolderSetType> NAMESPACE = REGISTRY.register("namespace", () -> NamespaceHolderSet::codec);
}
