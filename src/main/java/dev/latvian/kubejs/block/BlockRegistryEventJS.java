package dev.latvian.kubejs.block;

import dev.latvian.kubejs.util.RegistryEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends RegistryEventJS<Block>
{
	BlockRegistryEventJS(IForgeRegistry<Block> r)
	{
		super(r);
	}

	public BlockProperties newBlock(String name)
	{
		return new BlockProperties(name, p -> {
			BlockJS block = new BlockJS(p);
			block.setRegistryName(UtilsJS.INSTANCE.idMC(p.id));
			registry.register(block);
		});
	}
}