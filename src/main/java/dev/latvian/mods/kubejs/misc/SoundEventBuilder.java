package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {
	public SoundEventBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.SOUND_EVENT;
	}

	@Override
	public SoundEvent createObject() {
		return SoundEvent.createVariableRangeEvent(id);
	}
}
