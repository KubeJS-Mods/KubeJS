package dev.latvian.kubejs.integration.packmode;

import com.teamacronymcoders.packmode.api.PackModeAPI;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;

import java.util.List;

/**
 * @author LatvianModder
 */
@DisplayName("Pack Mode Integration")
public class PackModeWrapper
{
	public String getMode()
	{
		return PackModeAPI.getInstance().getPackMode();
	}

	public void setMode(@P("packmode") String packmode)
	{
		PackModeAPI.getInstance().setPackMode(packmode);
	}

	public List<String> getList()
	{
		return PackModeAPI.getInstance().getValidPackModes();
	}

	public boolean isValid(@P("packmode") String packmode)
	{
		return PackModeAPI.getInstance().isValidPackMode(packmode);
	}
}