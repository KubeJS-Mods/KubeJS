package dev.latvian.mods.kubejs.fluid.fabric;

import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FluidPlatformHelperImpl {
	// ForgeFlowingFluid and FluidAttributes are from Forge.

	public abstract static class ForgeFlowingFluid extends FlowingFluid {
		private final Supplier<? extends Fluid> flowing;
		private final Supplier<? extends Fluid> still;
		@Nullable
		private final Supplier<? extends Item> bucket;
		@Nullable
		private final Supplier<? extends LiquidBlock> block;
		private final FluidAttributes.Builder builder;
		private final boolean canMultiply;
		private final int slopeFindDistance;
		private final int levelDecreasePerBlock;
		private final float explosionResistance;
		private final int tickRate;

		protected ForgeFlowingFluid(Properties properties) {
			this.flowing = properties.flowing;
			this.still = properties.still;
			this.builder = properties.attributes;
			this.canMultiply = properties.canMultiply;
			this.bucket = properties.bucket;
			this.block = properties.block;
			this.slopeFindDistance = properties.slopeFindDistance;
			this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
			this.explosionResistance = properties.explosionResistance;
			this.tickRate = properties.tickRate;
		}

		@Override
		public Fluid getFlowing() {
			return flowing.get();
		}

		@Override
		public Fluid getSource() {
			return still.get();
		}

		@Override
		protected boolean canConvertToSource() {
			return canMultiply;
		}

		@Override
		protected void beforeDestroyingBlock(@NotNull LevelAccessor worldIn, @NotNull BlockPos pos, BlockState state) {
			BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
			Block.dropResources(state, worldIn, pos, blockEntity);
		}

		@Override
		protected int getSlopeFindDistance(@NotNull LevelReader worldIn) {
			return slopeFindDistance;
		}

		@Override
		protected int getDropOff(@NotNull LevelReader worldIn) {
			return levelDecreasePerBlock;
		}

		@Override
		public Item getBucket() {
			return bucket != null ? bucket.get() : Items.AIR;
		}

		@Override
		protected boolean canBeReplacedWith(@NotNull FluidState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Fluid fluidIn, @NotNull Direction direction) {
			return direction == Direction.DOWN && !isSame(fluidIn);
		}

		@Override
		public int getTickDelay(@NotNull LevelReader level) {
			return tickRate;
		}

		@Override
		protected float getExplosionResistance() {
			return explosionResistance;
		}

		@Override
		protected BlockState createLegacyBlock(@NotNull FluidState state) {
			if (block != null) {
				return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
			}
			return Blocks.AIR.defaultBlockState();
		}

		@Override
		public boolean isSame(@NotNull Fluid fluidIn) {
			return fluidIn == still.get() || fluidIn == flowing.get();
		}

		@NotNull
		@Override
		public Optional<SoundEvent> getPickupSound() {
			return Optional.ofNullable(createAttributes().getFillSound());
		}

		protected FluidAttributes createAttributes() {
			return builder.build(this);
		}

		public static class Flowing extends ForgeFlowingFluid {
			public Flowing(Properties properties) {
				super(properties);
				registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
			}


			@Override
			protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
				super.createFluidStateDefinition(builder);
				builder.add(LEVEL);
			}

			@Override
			public int getAmount(FluidState state) {
				return state.getValue(LEVEL);
			}

			@Override
			public boolean isSource(@NotNull FluidState state) {
				return false;
			}
		}

		public static class Source extends ForgeFlowingFluid {
			public Source(Properties properties) {
				super(properties);
			}

			@Override
			public int getAmount(@NotNull FluidState fluidState) {
				return 8;
			}

			@Override
			public boolean isSource(@NotNull FluidState fluidState) {
				return true;
			}
		}

		public static class Properties {
			private final Supplier<? extends Fluid> still;
			private final Supplier<? extends Fluid> flowing;
			private final FluidAttributes.Builder attributes;
			private boolean canMultiply;
			private Supplier<? extends Item> bucket;
			private Supplier<? extends LiquidBlock> block;
			private int slopeFindDistance = 4;
			private int levelDecreasePerBlock = 1;
			private float explosionResistance = 1;
			private int tickRate = 5;

			public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, FluidAttributes.Builder attributes) {
				this.still = still;
				this.flowing = flowing;
				this.attributes = attributes;
			}

			public Properties canMultiply() {
				canMultiply = true;
				return this;
			}

			public Properties bucket(Supplier<? extends Item> bucket) {
				this.bucket = bucket;
				return this;
			}

			public Properties block(Supplier<? extends LiquidBlock> block) {
				this.block = block;
				return this;
			}

			public Properties slopeFindDistance(int slopeFindDistance) {
				this.slopeFindDistance = slopeFindDistance;
				return this;
			}

			public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
				this.levelDecreasePerBlock = levelDecreasePerBlock;
				return this;
			}

			public Properties explosionResistance(float explosionResistance) {
				this.explosionResistance = explosionResistance;
				return this;
			}

			public Properties tickRate(int tickRate) {
				this.tickRate = tickRate;
				return this;
			}
		}
	}

	public static class FluidAttributes {
		private String translationKey;

		private final ResourceLocation stillTexture;
		private final ResourceLocation flowingTexture;

		@Nullable
		private final ResourceLocation overlayTexture;

		private final SoundEvent fillSound;
		private final SoundEvent emptySound;

		/**
		 * The light level emitted by this fluid.
		 * <p>
		 * Default value is 0, as most fluids do not actively emit light.
		 */
		private final int luminosity;

		/**
		 * Density of the fluid - completely arbitrary; negative density indicates that the fluid is
		 * lighter than air.
		 * <p>
		 * Default value is approximately the real-life density of water in kg/m^3.
		 */
		private final int density;

		/**
		 * Temperature of the fluid - completely arbitrary; higher temperature indicates that the fluid is
		 * hotter than air.
		 * <p>
		 * Default value is approximately the real-life room temperature of water in degrees Kelvin.
		 */
		private final int temperature;

		/**
		 * Viscosity ("thickness") of the fluid - completely arbitrary; negative values are not
		 * permissible.
		 * <p>
		 * Default value is approximately the real-life density of water in m/s^2 (x10^-3).
		 * <p>
		 * Higher viscosity means that a fluid flows more slowly, like molasses.
		 * Lower viscosity means that a fluid flows more quickly, like helium.
		 */
		private final int viscosity;

		/**
		 * This indicates if the fluid is gaseous.
		 * <p>
		 * Generally this is associated with negative density fluids.
		 */
		private final boolean isGaseous;

		/**
		 * The rarity of the fluid.
		 * <p>
		 * Used primarily in tool tips.
		 */
		private final Rarity rarity;

		/**
		 * Color used by universal bucket and the ModelFluid baked model.
		 * Note that this int includes the alpha so converting this to RGB with alpha would be
		 * float r = ((color >> 16) & 0xFF) / 255f; // red
		 * float g = ((color >> 8) & 0xFF) / 255f; // green
		 * float b = ((color >> 0) & 0xFF) / 255f; // blue
		 * float a = ((color >> 24) & 0xFF) / 255f; // alpha
		 */
		private final int color;

		protected FluidAttributes(Builder builder, Fluid fluid) {
			this.translationKey = builder.translationKey;
			this.stillTexture = builder.stillTexture;
			this.flowingTexture = builder.flowingTexture;
			this.overlayTexture = builder.overlayTexture;
			this.color = builder.color;
			this.fillSound = builder.fillSound;
			this.emptySound = builder.emptySound;
			this.luminosity = builder.luminosity;
			this.temperature = builder.temperature;
			this.viscosity = builder.viscosity;
			this.density = builder.density;
			this.isGaseous = builder.isGaseous;
			this.rarity = builder.rarity;
		}

		public SoundEvent getFillSound() {
			return fillSound;
		}

		public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
			return new Builder(stillTexture, flowingTexture, FluidAttributes::new);
		}

		public static class Builder {
			private final ResourceLocation stillTexture;
			private final ResourceLocation flowingTexture;
			private ResourceLocation overlayTexture;
			private int color = 0xFFFFFFFF;
			private String translationKey;
			private SoundEvent fillSound;
			private SoundEvent emptySound;
			private int luminosity = 0;
			private int density = 1000;
			private int temperature = 300;
			private int viscosity = 1000;
			private boolean isGaseous;
			private Rarity rarity = Rarity.COMMON;
			private BiFunction<Builder, Fluid, FluidAttributes> factory;

			protected Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<Builder, Fluid, FluidAttributes> factory) {
				this.factory = factory;
				this.stillTexture = stillTexture;
				this.flowingTexture = flowingTexture;
			}

			public final Builder translationKey(String translationKey) {
				this.translationKey = translationKey;
				return this;
			}

			public final Builder color(int color) {
				this.color = color;
				return this;
			}

			public final Builder overlay(ResourceLocation texture) {
				overlayTexture = texture;
				return this;
			}

			public final Builder luminosity(int luminosity) {
				this.luminosity = luminosity;
				return this;
			}

			public final Builder density(int density) {
				this.density = density;
				return this;
			}

			public final Builder temperature(int temperature) {
				this.temperature = temperature;
				return this;
			}

			public final Builder viscosity(int viscosity) {
				this.viscosity = viscosity;
				return this;
			}

			public final Builder gaseous() {
				isGaseous = true;
				return this;
			}

			public final Builder rarity(Rarity rarity) {
				this.rarity = rarity;
				return this;
			}

			public final Builder sound(SoundEvent sound) {
				this.fillSound = this.emptySound = sound;
				return this;
			}

			public final Builder sound(SoundEvent fillSound, SoundEvent emptySound) {
				this.fillSound = fillSound;
				this.emptySound = emptySound;
				return this;
			}

			public FluidAttributes build(Fluid fluid) {
				return factory.apply(this, fluid);
			}
		}
	}

	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		if (source) {
			return new ForgeFlowingFluid.Source(createProperties(builder));
		} else {
			return new ForgeFlowingFluid.Flowing(createProperties(builder));
		}
	}

	public static ForgeFlowingFluid.Properties createProperties(FluidBuilder fluidBuilder) {
		if (fluidBuilder.extraPlatformInfo != null) {
			return (ForgeFlowingFluid.Properties) fluidBuilder.extraPlatformInfo;
		}

		var builder = FluidAttributes.builder(fluidBuilder.stillTexture, fluidBuilder.flowingTexture)
				.translationKey("fluid." + fluidBuilder.id.getNamespace() + "." + fluidBuilder.id.getPath())
				.color(fluidBuilder.color)
				.rarity(fluidBuilder.rarity)
				.density(fluidBuilder.density)
				.viscosity(fluidBuilder.viscosity)
				.luminosity(fluidBuilder.luminosity)
				.temperature(fluidBuilder.temperature);

		if (fluidBuilder.isGaseous) {
			builder.gaseous();
		}

		var properties = new ForgeFlowingFluid.Properties(fluidBuilder, fluidBuilder.flowingFluid, builder).bucket(fluidBuilder.bucketItem).block(() -> (LiquidBlock) fluidBuilder.block.get());
		fluidBuilder.extraPlatformInfo = properties;
		return properties;
	}


	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		return new BucketItemJS(builder, itemBuilder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder fluidBuilder;

		public BucketItemJS(FluidBuilder b, FluidBucketItemBuilder itemBuilder) {
			super(b.get(), itemBuilder.createItemProperties());
			fluidBuilder = b;
		}
	}

	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		FluidRenderHandlerRegistry.INSTANCE.register(builder.get(), builder.flowingFluid.get(), new SimpleFluidRenderHandler(
				builder.stillTexture,
				builder.flowingTexture,
				builder.color
		));

		BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), builder.get(), builder.flowingFluid.get());
		return new LiquidBlock((FlowingFluid) builder.get(), properties);
	}

	public static Fluid getContainedFluid(Item item) {
		return item instanceof BucketItem bucketItem ? bucketItem.content : null;
	}
}