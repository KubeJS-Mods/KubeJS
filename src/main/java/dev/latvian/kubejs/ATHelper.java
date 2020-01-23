package dev.latvian.kubejs;

import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 * <p>
 * TODO: Change to AccessTransformers
 */
@SuppressWarnings("ConstantConditions")
public class ATHelper
{
	public static List<IFutureReloadListener> getReloadListeners(SimpleReloadableResourceManager manager)
	{
		return ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, manager, "field_199015_d");
	}

	public static List<IFutureReloadListener> getInitTaskQueue(SimpleReloadableResourceManager manager)
	{
		return ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, manager, "field_219539_d");
	}

	public static Map<String, FallbackResourceManager> getNamespaceResourceManagers(SimpleReloadableResourceManager manager)
	{
		return ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, manager, "field_199014_c");
	}
}