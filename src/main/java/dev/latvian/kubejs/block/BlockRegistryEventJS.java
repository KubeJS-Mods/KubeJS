package dev.latvian.kubejs.block;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends EventJS
{
	private final IForgeRegistry<Block> registry;

	BlockRegistryEventJS(IForgeRegistry<Block> r)
	{
		registry = r;
	}

	public BlockBuilder create(String name)
	{
		return new BlockBuilder(name, p -> {
			BlockJS block = new BlockJS(p);
			block.setRegistryName(p.id.mc());
			registry.register(block);
		});
	}
}