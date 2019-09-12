package dev.latvian.kubejs.integration.packmode;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import io.sommers.packmode.api.PackModeAPI;

import java.util.List;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "Pack Mode Integration")
public class PackModeWrapper
{
	@DocMethod
	public String getMode()
	{
		return PackModeAPI.getInstance().getCurrentPackMode();
	}

	@DocMethod
	public String getActualMode()
	{
		return PackModeAPI.getInstance().getNextRestartPackMode();
	}

	@DocMethod(params = @Param("packmode"))
	public void setMode(String packmode)
	{
		PackModeAPI.getInstance().setNextRestartPackMode(packmode);
	}

	@DocMethod
	public List<String> getList()
	{
		return PackModeAPI.getInstance().getPackModes();
	}

	@DocMethod(params = @Param("packmode"))
	public boolean isValid(String packmode)
	{
		return PackModeAPI.getInstance().isValidPackMode(packmode);
	}
}