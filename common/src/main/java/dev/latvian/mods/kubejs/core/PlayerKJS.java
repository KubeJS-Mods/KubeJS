package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.stages.Stages;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public interface PlayerKJS {
	@Nullable
	Stages getStagesRawKJS();

	void kjs$setStages(Stages p);

	Stages kjs$getStages();
}
