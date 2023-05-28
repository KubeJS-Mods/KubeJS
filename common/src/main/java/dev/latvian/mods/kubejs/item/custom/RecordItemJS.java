package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

public class RecordItemJS extends RecordItem {
	public static class Builder extends ItemBuilder {
		public transient ResourceLocation song;
		public transient SoundEvent songSoundEvent;
		public transient int length;
		public transient int analogOutput;

		public Builder(ResourceLocation i) {
			super(i);
			song = new ResourceLocation("minecraft:music_disc.11");
			length = 71;
			analogOutput = 1;
			maxStackSize(1);
			rarity(Rarity.RARE);
		}

		public Builder song(ResourceLocation s, int seconds) {
			song = s;
			length = seconds;
			songSoundEvent = null;
			return this;
		}

		public Builder analogOutput(int o) {
			analogOutput = o;
			return this;
		}

		@Override
		public Item createObject() {
			return new RecordItemJS(this, analogOutput, SoundEvents.ITEM_PICKUP, createItemProperties());
		}

		public SoundEvent getSoundEvent() {
			if (songSoundEvent == null) {
				songSoundEvent = KubeJSRegistries.soundEvents().get(song);

				if (songSoundEvent == null || songSoundEvent == SoundEvents.ITEM_PICKUP) {
					songSoundEvent = SoundEvents.MUSIC_DISC_11;
				}
			}

			return songSoundEvent;
		}
	}

	private final Builder builder;

	public RecordItemJS(Builder b, int analogOutput, SoundEvent song, Item.Properties properties) {
		super(analogOutput, song, properties, b.length);
		builder = b;
	}

	@Override
	public SoundEvent getSound() {
		return builder.getSoundEvent();
	}

	@Override
	public int getLengthInTicks() {
		return builder.length * 20;
	}
}