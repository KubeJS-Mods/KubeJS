package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.TieredItemKJS;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(TieredItem.class)
public abstract class TieredItemMixin implements TieredItemKJS {
	@Override
	@Accessor("tier")
	public abstract Tier getTierKJS();

	@Override
	@Accessor("tier")
	@Mutable
	public abstract void setTierKJS(Tier tier);
}
