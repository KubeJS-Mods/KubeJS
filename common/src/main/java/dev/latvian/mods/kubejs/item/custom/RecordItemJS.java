package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.KubeJSRegistries;
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
public class RecordItemJS extends RecordItem {
	public static class Builder extends ItemBuilder {
		public transient int analogOutput;
		public transient ResourceLocation song;
		public transient SoundEvent songSoundEvent;

		public Builder(ResourceLocation i) {
			super(i);
			analogOutput = 1;
			song = new ResourceLocation("minecraft:music_disc.11");
			maxStackSize(1);
			rarity(Rarity.RARE);
		}

		public Builder analogOutput(int o) {
			analogOutput = o;
			return this;
		}

		public Builder song(ResourceLocation s) {
			song = s;
			return this;
		}

		@Override
		public Item createObject() {
			return new RecordItemJS(this, analogOutput, SoundEvents.ITEM_PICKUP, createItemProperties());
		}
	}

	private final Builder builder;

	public RecordItemJS(Builder b, int analogOutput, SoundEvent song, Item.Properties properties) {
		super(analogOutput, song, properties);
		builder = b;
	}

	@Override
	public SoundEvent getSound() {
		if (builder.songSoundEvent == null) {
			builder.songSoundEvent = KubeJSRegistries.soundEvents().get(builder.song);

			if (builder.songSoundEvent == null || builder.songSoundEvent == SoundEvents.ITEM_PICKUP) {
				builder.songSoundEvent = SoundEvents.MUSIC_DISC_11;
			}
		}

		return builder.songSoundEvent;
	}
}