package dev.latvian.mods.kubejs.block.entity;

import com.google.common.collect.ImmutableSet;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.entity.ablities.BlockAbility;
import dev.latvian.mods.kubejs.block.entity.screen.event.DOMLoadedEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class BlockEntityBuilder extends BuilderBase<BlockEntityType<?>> {
	public transient BlockBuilder blockBuilder = null;
	public transient Consumer<TickerCallback> ticker = null;
	public transient float ticksEvery;

	public transient Map<String, Tuple<BlockAbility.AbilityJS, Function<BlockAbility.AbilityJS, BlockAbility<?>>>> blockAbilities = new HashMap<>();
	public transient Consumer<DOMLoadedEvent> onDomLoaded;

	public BlockEntityBuilder(ResourceLocation i) {
		super(i);
	}

	public BlockEntityBuilder tick(float ticks, Consumer<TickerCallback> tc) {
		this.ticker = tc;
		this.ticksEvery = ticks;
		return this;
	}

	public BlockEntityBuilder ability(String id, BlockAbility.AbilityJS ability) {
		if (ability.type() != null) {
			this.blockAbilities.put(id, new Tuple<>(ability, BlockAbility.registry.get(ability.type())));
		} else {
			throw new IllegalArgumentException("Abilities must provide a type!");
		}
		return this;
	}

	public BlockEntityBuilder createScreen(String screen) {
		ConsoleJS.STARTUP.pushLineNumber();
		ConsoleJS.STARTUP.warn("Screens have not been implemented yet");
		ConsoleJS.STARTUP.popLineNumber();
		return this;
	}

	public BlockEntityBuilder onContentLoaded(Consumer<DOMLoadedEvent> cb) {
		this.onDomLoaded = cb;
		ConsoleJS.STARTUP.pushLineNumber();
		ConsoleJS.STARTUP.warn("Screens have not been implemented yet (domContentLoaded)");
		ConsoleJS.STARTUP.popLineNumber();
		return this;
	}

	@HideFromJS
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new BasicBlockEntity(blockPos, blockState, this);
	}

	@HideFromJS
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level _level, BlockState _blockState, BlockEntityType<T> _blockEntityType) {
		if (this.ticker != null) {
			AtomicInteger ticks = new AtomicInteger();
			return (level, blockPos, blockState, blockEntity) -> {
				if (ticks.get() % ticksEvery == 0) {
					TickerCallback tc = new TickerCallback(new BlockContainerJS(level, blockPos), blockEntity);
					this.ticker.accept(tc);
				}
				ticks.getAndIncrement();
			};
		}
		return null;
	}

	@Override
	public RegistryObjectBuilderTypes<? super BlockEntityType<?>> getRegistryType() {
		return RegistryObjectBuilderTypes.BLOCK_ENTITY_TYPE;
	}

	@Override
	public BlockEntityType<?> createObject() {
		return new BlockEntityType<>(
				(blockPos, blockState) -> new BasicBlockEntity(blockPos, blockState, this),
				ImmutableSet.of(blockBuilder.get()),
				null
		);
	}
}
