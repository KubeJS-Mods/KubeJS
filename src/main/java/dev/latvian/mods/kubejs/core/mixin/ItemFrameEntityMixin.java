package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ItemFrameEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemFrame.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemFrameEntityMixin implements ItemFrameEntityKJS {
}
