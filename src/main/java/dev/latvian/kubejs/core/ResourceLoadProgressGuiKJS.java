package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;

/**
 * @author LatvianModder
 */
public interface ResourceLoadProgressGuiKJS
{
	default int getBackgroundColorKJS(int color)
	{
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().backgroundColor) : color;
	}

	default int getBarColorKJS(int color)
	{
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().barColor) : color;
	}

	default int getBarBorderColorKJS(int color)
	{
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().barBorderColor) : color;
	}
}
