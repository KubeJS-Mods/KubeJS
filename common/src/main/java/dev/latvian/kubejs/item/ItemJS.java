package dev.latvian.kubejs.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.kubejs.text.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item
{
	public final ItemBuilder properties;
	private final ImmutableMultimap<Attribute, AttributeModifier> attributes;
	private ItemStack containerItem;

	public ItemJS(ItemBuilder p)
	{
		super(p.createItemProperties());
		properties = p;
		Float attackDamage = p.getAttackDamage();
		Float attackSpeed = p.getAttackSpeed();

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		if (attackDamage != null)
		{
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage.doubleValue(), AttributeModifier.Operation.ADDITION));
		}
		if (attackSpeed != null)
		{
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed.doubleValue(), AttributeModifier.Operation.ADDITION));
		}
		this.attributes = builder.build();
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return properties.glow || super.isFoil(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		for (Text text : properties.tooltip)
		{
			tooltip.add(text.component());
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot)
	{
		return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
	}
}