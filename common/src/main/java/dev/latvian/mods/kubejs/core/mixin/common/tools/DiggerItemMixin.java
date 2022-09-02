package dev.latvian.mods.kubejs.core.mixin.common.tools;

import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.core.ModifiableItemKJS;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DiggerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin implements ModifiableItemKJS {
	@Override
	@Accessor("defaultModifiers")
	public abstract Multimap<Attribute, AttributeModifier> kjs$getAttributeMap();

	@Override
	@Accessor("defaultModifiers")
	@Mutable
	public abstract void kjs$setAttributeMap(Multimap<Attribute, AttributeModifier> attributes);
}
