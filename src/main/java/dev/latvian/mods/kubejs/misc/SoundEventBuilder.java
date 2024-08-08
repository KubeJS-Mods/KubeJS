package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Consumer;

@ReturnsSelf
public class SoundEventBuilder extends BuilderBase<SoundEvent> {

	public transient Consumer<SoundsGenerator.SoundGen> assetGen;

	public SoundEventBuilder(ResourceLocation i) {
		super(i);
		assetGen = gen -> gen.sound(id.toString()).subtitle(id.toLanguageKey("sound"));
	}

	public SoundEventBuilder sounds(Consumer<SoundsGenerator.SoundGen> gen) {
		assetGen = gen;
		return this;
	}

	@Override
	public SoundEvent createObject() {
		return SoundEvent.createVariableRangeEvent(id);
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		generator.sounds(id.getNamespace(), g -> g.addSound(id.getPath(), assetGen));
	}
}
