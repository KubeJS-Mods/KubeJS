package dev.latvian.kubejs.bindings;

import net.minecraft.world.item.Rarity;

/**
 * @author LatvianModder
 */
public class RarityWrapper
{
	public static final RarityWrapper COMMON = new RarityWrapper(Rarity.COMMON);
	public static final RarityWrapper UNCOMMON = new RarityWrapper(Rarity.UNCOMMON);
	public static final RarityWrapper RARE = new RarityWrapper(Rarity.RARE);
	public static final RarityWrapper EPIC = new RarityWrapper(Rarity.EPIC);

	public final Rarity rarity;

	private RarityWrapper(Rarity r)
	{
		rarity = r;
	}
}
