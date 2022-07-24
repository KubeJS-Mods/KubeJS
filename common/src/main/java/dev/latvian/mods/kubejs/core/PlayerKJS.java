package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface PlayerKJS extends LivingEntityKJS {
	@Nullable
	Stages kjs$getStagesRaw();

	void kjs$setStages(Stages p);

	Stages kjs$getStages();
}
