package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BootstrapExtension.class)
public class BlockWrapperTest {
	@Test
	void resolvesVanillaBlock() {
		assertThat(BlockWrapper.getBlock(Identifier.parse("minecraft:stone"))).isSameAs(Blocks.STONE);
		assertThat(BlockWrapper.getId(Blocks.STONE)).isEqualTo(Identifier.parse("minecraft:stone"));
	}

	@Test
	void facingMapIsPopulated() {
		assertThat(BlockWrapper.getFacing()).isNotEmpty().containsKey("north");
	}

	@Test
	void blockStateStringFormats() {
		assertThat(BlockWrapper.toBlockStateString("minecraft:stone", Map.of())).isEqualTo("minecraft:stone");
		assertThat(BlockWrapper.toBlockStateString("minecraft:furnace", Map.of("lit", "true")))
			.contains("minecraft:furnace").contains("lit").contains("true");
	}

	@Test
	void idPredicateBuilds() {
		assertThat(BlockWrapper.id(Identifier.parse("minecraft:stone"))).isNotNull();
	}

	@Test
	void typeListListsCustomTypes() {
		assertThat(BlockWrapper.getTypeList()).isNotEmpty();
	}
}
