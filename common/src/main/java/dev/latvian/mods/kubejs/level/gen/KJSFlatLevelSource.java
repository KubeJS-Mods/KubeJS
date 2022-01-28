package dev.latvian.mods.kubejs.level.gen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class KJSFlatLevelSource extends FlatLevelSource {
	public static final Codec<KJSFlatLevelSource> CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings")
			.xmap(KJSFlatLevelSource::new, KJSFlatLevelSource::settings).codec();

	public KJSFlatLevelSource(FlatLevelGeneratorSettings settings) {
		super(settings);
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	// TODO: (Maybe) add our own settings to make this
	//  and other hardcoded-ish stuff more configurable.
	public int getMinY() {
		return -64;
	}

}