package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends EventJS
{
	@Ignore
	public final IForgeRegistry<Block> registry;

	public BlockRegistryEventJS(IForgeRegistry<Block> r)
	{
		registry = r;
	}

	public void register(String id, Block block)
	{
		registry.register(block.setRegistryName(UtilsJS.getID(KubeJS.appendModId(id))));
	}

	public BlockBuilder create(String name)
	{
		return new BlockBuilder(name, p -> registry.register(new BlockJS(p).setRegistryName(p.id)));
	}
}