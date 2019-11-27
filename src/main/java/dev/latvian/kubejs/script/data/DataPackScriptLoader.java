package dev.latvian.kubejs.script.data;

import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class DataPackScriptLoader extends ReloadListener<ScriptManager>
{
	public final ScriptManager manager;

	public DataPackScriptLoader(ScriptManager m)
	{
		manager = m;
	}

	@Override
	protected ScriptManager prepare(IResourceManager resourceManager, IProfiler profiler)
	{
		manager.unload();

		Set<String> namespaces = new LinkedHashSet<>(resourceManager.getResourceNamespaces());

		for (String namespace : namespaces)
		{
			try (InputStreamReader reader = new InputStreamReader(resourceManager.getResource(new ResourceLocation(namespace, "kubejs/scripts.json")).getInputStream()))
			{
				ScriptPack pack = new ScriptPack(manager, new ScriptPackInfo(namespace, reader, "kubejs/"));

				for (ScriptFileInfo fileInfo : pack.info.scripts)
				{
					pack.scripts.add(new ScriptFile(pack, fileInfo, info -> new InputStreamReader(resourceManager.getResource(info.location).getInputStream())));
				}

				manager.packs.put(pack.info.namespace, pack);
			}
			catch (Exception ex)
			{
			}
		}

		return manager;
	}

	@Override
	protected void apply(ScriptManager manager, IResourceManager resourceManager, IProfiler profiler)
	{
		manager.load();
	}
}