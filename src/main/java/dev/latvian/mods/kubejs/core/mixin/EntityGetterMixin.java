package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EntityGetterKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(EntityGetter.class)
@RemapPrefixForJS("kjs$")
public interface EntityGetterMixin extends EntityGetterKJS {
	@Shadow
	@HideFromJS
	List<? extends Player> players();
}
