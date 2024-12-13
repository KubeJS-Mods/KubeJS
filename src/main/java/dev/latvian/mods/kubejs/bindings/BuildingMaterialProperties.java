package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.custom.ButtonOrPressurePlateBuilder;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record BuildingMaterialProperties(
	Blocks blocks,
	Optional<Boolean> baseBlock,
	Optional<Boolean> baseBlockSuffix,
	Consumer<BlockBuilder> properties,
	Optional<BlockSetType> behaviour,
	Optional<TickDuration> ticksToStayPressed
) {
	public record Blocks(
		Optional<Boolean> slab,
		Optional<Boolean> stairs,
		Optional<Boolean> fence,
		Optional<Boolean> fenceGate,
		Optional<Boolean> wall,
		Optional<Boolean> pressurePlate,
		Optional<Boolean> button,
		Optional<Boolean> trapdoor,
		Optional<Boolean> door
	) {
	}

	public static final TypeInfo TYPE_INFO = TypeInfo.of(BuildingMaterialProperties.class);

	private boolean add(Function<Blocks, Optional<Boolean>> func) {
		return blocks == null || func.apply(blocks).orElse(true);
	}

	@HideFromJS
	public void register(Context cx, RegistryKubeEvent<Block> event, KubeResourceLocation id) {
		var builder = new ArrayList<BlockBuilder>();

		if (baseBlock.orElse(true)) {
			boolean _baseBlockSuffix = baseBlockSuffix.orElse(true);
			var baseBlock = (BlockBuilder) event.create(cx, _baseBlockSuffix ? id.withPath(p -> p + "_block") : id);
			builder.add(baseBlock);

			if (_baseBlockSuffix) {
				baseBlock.texture(id.wrapped().withPath(p -> "block/" + p).toString());
			}
		}

		if (add(Blocks::slab)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_slab"), "slab"));
		}

		if (add(Blocks::stairs)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_stairs"), "stairs"));
		}

		if (add(Blocks::fence)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_fence"), "fence"));
		}

		if (add(Blocks::fenceGate)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_fence_gate"), "fence_gate"));
		}

		if (add(Blocks::wall)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_wall"), "wall"));
		}

		if (add(Blocks::pressurePlate)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_pressure_plate"), "pressure_plate"));
		}

		if (add(Blocks::button)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_button"), "button"));
		}

		if (add(Blocks::trapdoor)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_trapdoor"), "trapdoor"));
		}

		if (add(Blocks::door)) {
			builder.add((BlockBuilder) event.create(cx, id.withPath(p -> p + "_door"), "door"));
		}

		for (var b : builder) {
			if (properties != null) {
				properties.accept(b);
			}

			if (b instanceof ButtonOrPressurePlateBuilder p) {
				behaviour.ifPresent(p::behaviour);
				ticksToStayPressed.ifPresent(p::ticksToStayPressed);
			}
		}
	}
}
