package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import net.minecraft.server.management.PlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin implements PlayerInteractionManagerKJS
{
	@Override
	@Accessor("isDestroyingBlock")
	public abstract boolean isDestroyingBlockKJS();
}