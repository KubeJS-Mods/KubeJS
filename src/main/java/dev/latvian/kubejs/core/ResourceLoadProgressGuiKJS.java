package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientWrapper;

/**
 * @author LatvianModder
 */
public interface ResourceLoadProgressGuiKJS
{
	default int getNewBackgroundColorKJS(int color)
	{
		return (color & 0xFF000000) | (ClientWrapper.backgroundColor & 0xFFFFFF);
	}

	default int getNewBarColorKJS(int color)
	{
		return (color & 0xFF000000) | (ClientWrapper.barColor & 0xFFFFFF);
	}

	default int getNewBarBackgroundColorKJS(int color)
	{
		return (color & 0xFF000000) | (ClientWrapper.barBackgroundColor & 0xFFFFFF);
	}

	default int getNewBarBorderColorKJS(int color)
	{
		return (color & 0xFF000000) | (ClientWrapper.barBorderColor & 0xFFFFFF);
	}
}
