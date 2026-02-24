package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Weapon;

public class SwordItemBuilder extends HandheldItemBuilder {
	public static final Identifier[] SWORD_TAGS = {
		ItemTags.SWORDS.location()
	};

	public static final Identifier SWORD_MODEL = Identifier.withDefaultNamespace("item/iron_sword");

	public SwordItemBuilder(Identifier i) {
		super(i, 3F, -2.4F);
		parentModel = SWORD_MODEL;
		tag(SWORD_TAGS);
	}


	@Override
	public Item createObject() {
		var props = createItemProperties();
		var material = toolTier.build();
		itemAttributeModifiers = createToolAttributes(material, attackDamageBaseline, speedBaseline);
		return new Item(props
			.component(DataComponents.WEAPON, new Weapon((int) attackDamageBaseline, disableBlockingForSeconds))
		);
	}
}
