package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.client.SoundGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Consumer;

public class SoundEventBuilder extends BuilderBase<SoundEvent> {

	public transient Consumer<SoundGenerator.SoundEntry> gen;

	public SoundEventBuilder(ResourceLocation i) {
		super(i);
		gen = e -> e.sounds(id);
	}

	public SoundEventBuilder sound(Consumer<SoundGenerator.SoundEntry> consumer) {
		gen = consumer;
		return this;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.SOUND_EVENT;
	}

	@Override
	public SoundEvent createObject() {
		return SoundEvent.createVariableRangeEvent(id);
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.sounds(id.getNamespace(), g -> g.addSound(id.getPath(), gen));
	}
}
