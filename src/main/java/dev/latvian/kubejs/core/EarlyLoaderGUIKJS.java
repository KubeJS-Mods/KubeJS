package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientWrapper;

/**
 * @author LatvianModder
 */
public interface EarlyLoaderGUIKJS
{
	default float[] getNewMemoryColorKJS(float[] color)
	{
		return ClientWrapper.fmlMemoryColor == null ? color : ClientWrapper.fmlMemoryColor;
	}

	default float[] getNewLogColorKJS(float[] color)
	{
		return ClientWrapper.fmlLogColor == null ? color : ClientWrapper.fmlLogColor;
	}
}
