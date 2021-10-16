package dev.latvian.kubejs.world.gen;

public class FlatChunkGeneratorKJS {
}/*extends ChunkGenerator {
	public static final Codec<FlatChunkGeneratorKJS> CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatChunkGeneratorKJS::new, FlatChunkGeneratorKJS::settings).codec();

	private final FlatLevelGeneratorSettings settings;

	public FlatChunkGeneratorKJS(FlatLevelGeneratorSettings arg) {
		super(new FixedBiomeSource(arg.getBiome()), new FixedBiomeSource(arg.getBiome()), arg.structureSettings(), 0L);
		this.settings = arg;
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long l) {
		return this;
	}

	public FlatLevelGeneratorSettings settings() {
		return this.settings;
	}

	@Override
	public void buildSurfaceAndBedrock(WorldGenRegion arg, ChunkAccess arg2) {
	}

	@Override
	public int getSpawnHeight() {
		BlockState[] lvs = this.settings.getLayers();

		for (int i = 0; i < lvs.length; ++i) {
			BlockState lv = lvs[i] == null ? Blocks.AIR.defaultBlockState() : lvs[i];
			if (!Heightmap.Types.MOTION_BLOCKING.isOpaque().test(lv)) {
				return i - 1;
			}
		}

		return lvs.length;
	}

	@Override
	public void fillFromNoise(LevelAccessor arg, StructureFeatureManager arg2, ChunkAccess arg3) {
		BlockState[] lvs = this.settings.getLayers();
		BlockPos.MutableBlockPos lv = new BlockPos.MutableBlockPos();
		Heightmap lv2 = arg3.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap lv3 = arg3.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

		for (int i = 0; i < lvs.length; ++i) {
			BlockState lv4 = lvs[i];
			if (lv4 != null) {
				for (int j = 0; j < 16; ++j) {
					for (int k = 0; k < 16; ++k) {
						arg3.setBlockState(lv.set(j, i, k), lv4, false);
						lv2.update(j, i, k, lv4);
						lv3.update(j, i, k, lv4);
					}
				}
			}
		}

	}

	@Override
	public int getBaseHeight(int i, int j, Heightmap.Types arg) {
		List<BlockState> lvs = this.settings.getLayers();

		for (int k = lvs.size() - 1; k >= 0; --k) {
			BlockState lv = lvs.get(k);
			if (lv != null && arg.isOpaque().test(lv)) {
				return k + 1;
			}
		}

		return 0;
	}

	@Override
	public BlockGetter getBaseColumn(int i, int j) {
		return new NoiseColumn(0, settings.getLayers().stream().map((arg) -> arg == null ? Blocks.AIR.defaultBlockState() : arg).toArray(BlockState[]::new));
	}
}
*/