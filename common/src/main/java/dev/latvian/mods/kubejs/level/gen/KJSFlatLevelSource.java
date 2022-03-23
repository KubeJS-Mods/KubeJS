package dev.latvian.mods.kubejs.level.gen;

// TODO: readd
/*public class KJSFlatLevelSource extends FlatLevelSource {
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

}*/