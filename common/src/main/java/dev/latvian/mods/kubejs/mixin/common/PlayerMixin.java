package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(value = Player.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class PlayerMixin implements PlayerKJS {
	private Stages kjs$stages;

	@Override
	@Nullable
	public Stages kjs$getStagesRaw() {
		return kjs$stages;
	}

	@Override
	@HideFromJS
	public void kjs$setStages(Stages p) {
		kjs$stages = p;
	}

	@Override
	public Stages kjs$getStages() {
		if (kjs$stages != null) {
			return kjs$stages;
		}

		return Stages.get((Player) (Object) this);
	}
}
