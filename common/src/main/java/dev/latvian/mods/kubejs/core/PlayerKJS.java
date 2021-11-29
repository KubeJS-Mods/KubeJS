package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.stages.Stages;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public interface PlayerKJS {
	@Nullable
	Stages getStagesRawKJS();

	void setStagesKJS(Stages p);

	Stages getStagesKJS();
}
