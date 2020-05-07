package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;

/**
 * @author LatvianModder
 */
public interface EarlyLoaderGUIKJS
{
	default float[] getNewMemoryColorKJS(float[] color)
	{
		return ClientProperties.get().fmlMemoryColor == null ? color : ClientProperties.get().fmlMemoryColor;
	}

	default float[] getNewLogColorKJS(float[] color)
	{
		return ClientProperties.get().fmlLogColor == null ? color : ClientProperties.get().fmlLogColor;
	}
}
