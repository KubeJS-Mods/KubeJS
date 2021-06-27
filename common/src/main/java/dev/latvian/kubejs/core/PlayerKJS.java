package dev.latvian.kubejs.core;

import dev.latvian.kubejs.stages.Stages;
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
