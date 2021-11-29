package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.LevelKJS;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelKJS {
}
