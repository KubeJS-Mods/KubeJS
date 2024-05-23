package dev.latvian.mods.kubejs.core;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

@FunctionalInterface
public interface LazyComponentKJS extends Supplier<Component> {
	@Override
	Component get();
}