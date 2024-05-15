package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ContainerKJS;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Container.class)
public interface ContainerMixin extends ContainerKJS {
}
