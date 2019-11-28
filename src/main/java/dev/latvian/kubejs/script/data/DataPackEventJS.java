package dev.latvian.kubejs.script.data;

import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;

/**
 * @author LatvianModder
 */
public class DataPackEventJS extends ServerEventJS
{
	private final VirtualKubeJSDataPack virtualDataPack;

	public DataPackEventJS(VirtualKubeJSDataPack d)
	{
		virtualDataPack = d;
	}

	public void add(Object id, String content)
	{
		virtualDataPack.addData(UtilsJS.getID(id), content);
	}

	public void addJson(Object id, Object json)
	{
		add(id, JsonUtilsJS.toString(JsonUtilsJS.of(json)));
	}
}