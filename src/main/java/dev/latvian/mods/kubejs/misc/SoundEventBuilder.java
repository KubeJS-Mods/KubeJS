package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {
	public SoundEventBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public SoundEvent createObject() {
		return SoundEvent.createVariableRangeEvent(id);
	}
}
