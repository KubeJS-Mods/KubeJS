package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.PlayerKJS;
import dev.latvian.kubejs.stages.Stages;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(value = Player.class, priority = 1001)
public abstract class PlayerMixin implements PlayerKJS {
	private Stages stagesKJS;

	@Override
	@Nullable
	public Stages getStagesRawKJS() {
		return stagesKJS;
	}

	@Override
	@HideFromJS
	public void setStagesKJS(Stages p) {
		stagesKJS = p;
	}

	@Override
	@RemapForJS("getStages")
	public Stages getStagesKJS() {
		if (stagesKJS != null) {
			return stagesKJS;
		}

		return Stages.get((Player) (Object) this);
	}
}
