package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ClientLevelKJS;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ClientLevelKJS {
}
