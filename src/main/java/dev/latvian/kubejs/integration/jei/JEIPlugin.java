package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin
{
	public IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(KubeJS.MOD_ID, "jei");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime r)
	{
		runtime = r;
		DefaultBindings.GLOBAL.put("jeiRuntime", runtime);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration)
	{
		new AddJEISubtypesEventJS(registration).post(ScriptType.CLIENT, JEIIntegration.JEI_SUBTYPES);
	}
}