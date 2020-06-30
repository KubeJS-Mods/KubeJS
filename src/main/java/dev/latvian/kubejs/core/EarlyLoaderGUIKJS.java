package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;

/**
 * @author LatvianModder
 */
public interface EarlyLoaderGUIKJS
{
	default float[] getMemoryColorKJS(float[] color)
	{
		return ClientProperties.get().overrideColors ? ClientProperties.get().fmlMemoryColor3f : color;
	}

	default float[] getLogColorKJS(float[] color)
	{
		return ClientProperties.get().overrideColors ? ClientProperties.get().fmlLogColor3f : color;
	}

	default float getBackgroundColorKJS(float c, int index)
	{
		return ClientProperties.get().overrideColors ? ClientProperties.get().backgroundColor3f[index] : c;
	}
}
