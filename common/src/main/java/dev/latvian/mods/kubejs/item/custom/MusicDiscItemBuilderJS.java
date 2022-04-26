package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

/**
 * @author LatvianModder
 */
public class MusicDiscItemBuilderJS extends ItemBuilder {
	public transient int analogOutput;
	public transient SoundEvent song;

	public MusicDiscItemBuilderJS(ResourceLocation i) {
		super(i);
		analogOutput = 1;
		song = SoundEvents.MUSIC_DISC_11;
		maxStackSize(1);
		rarity(Rarity.RARE);
	}

	public MusicDiscItemBuilderJS analogOutput(int o) {
		analogOutput = o;
		return this;
	}

	public MusicDiscItemBuilderJS song(SoundEvent s) {
		song = s;
		return this;
	}

	@Override
	public Item createObject() {
		return new RecordItem(analogOutput, song, createItemProperties());
	}
}