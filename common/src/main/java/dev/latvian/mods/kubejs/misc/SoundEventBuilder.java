package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {
	public SoundEventBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryObjectBuilderTypes<SoundEvent> getRegistryType() {
		return RegistryObjectBuilderTypes.SOUND_EVENT;
	}

	@Override
	public SoundEvent createObject() {
		return new SoundEvent(id);
	}
}
