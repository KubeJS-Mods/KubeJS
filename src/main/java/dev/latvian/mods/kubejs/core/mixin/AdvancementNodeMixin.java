package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.AdvancementNodeKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.advancements.AdvancementNode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AdvancementNode.class)
@RemapPrefixForJS("kjs$")
public abstract class AdvancementNodeMixin implements AdvancementNodeKJS {
}
