package dev.latvian.mods.kubejs.holder;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;

import java.util.function.Function;

public interface KubeJSHolderSet<T> extends ICustomHolderSet<T> {
	String kjs$toIngredientString(Function<Holder<T>, String> elementCodec);
}
