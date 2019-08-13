package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.RegistryEventJS;
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

	public BlockJS register(String name, MaterialJS material)
	{
		Block block = setID(name, new Block(material.material));
		block.setTranslationKey(KubeJS.MOD_ID + "." + name);
		registry.register(block);
		return new BlockJS(block);
	}
}