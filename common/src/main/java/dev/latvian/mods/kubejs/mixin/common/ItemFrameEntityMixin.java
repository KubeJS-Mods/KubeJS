package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ItemFrameEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(ItemFrame.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemFrameEntityMixin implements ItemFrameEntityKJS {
}
