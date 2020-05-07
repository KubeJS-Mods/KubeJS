package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;

/**
 * @author LatvianModder
 */
public interface ResourceLoadProgressGuiKJS
{
	default int getNewBackgroundColorKJS(int color)
	{
		return (color & 0xFF000000) | ClientProperties.get().backgroundColor;
	}

	default int getNewBarColorKJS(int color)
	{
		return (color & 0xFF000000) | ClientProperties.get().barColor;
	}

	default int getNewBarBackgroundColorKJS(int color)
	{
		return (color & 0xFF000000) | ClientProperties.get().barBackgroundColor;
	}

	default int getNewBarBorderColorKJS(int color)
	{
		return (color & 0xFF000000) | ClientProperties.get().barBorderColor;
	}
}
