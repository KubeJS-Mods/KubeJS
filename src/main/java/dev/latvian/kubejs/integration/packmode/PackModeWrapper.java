package dev.latvian.kubejs.integration.packmode;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import io.sommers.packmode.api.PackModeAPI;

import java.util.List;

/**
 * @author LatvianModder
 */
@DisplayName("Pack Mode Integration")
public class PackModeWrapper
{
	public String getMode()
	{
		return PackModeAPI.getInstance().getCurrentPackMode();
	}

	public String getActualMode()
	{
		return PackModeAPI.getInstance().getNextRestartPackMode();
	}

	public void setMode(@P("packmode") String packmode)
	{
		PackModeAPI.getInstance().setNextRestartPackMode(packmode);
	}

	public List<String> getList()
	{
		return PackModeAPI.getInstance().getPackModes();
	}

	public boolean isValid(@P("packmode") String packmode)
	{
		return PackModeAPI.getInstance().isValidPackMode(packmode);
	}
}