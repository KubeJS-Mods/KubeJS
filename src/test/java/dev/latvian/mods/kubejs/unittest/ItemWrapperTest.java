package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BootstrapExtension.class)
public class ItemWrapperTest {
	@Test
	void findsVanillaItem() {
		var result = ItemWrapper.findItem("minecraft:diamond");
		assertThat(result.result()).isPresent();
		assertThat(result.result().get()).isSameAs(Items.DIAMOND);
	}

	@Test
	void getIdRoundTrips() {
		assertThat(ItemWrapper.getId(Items.DIAMOND)).isEqualTo(Identifier.parse("minecraft:diamond"));
		assertThat(ItemWrapper.getItem(Identifier.parse("minecraft:diamond"))).isSameAs(Items.DIAMOND);
	}

	@Test
	void existsAndIsItem() {
		assertThat(ItemWrapper.exists(Identifier.parse("minecraft:diamond"))).isTrue();
		assertThat(ItemWrapper.exists(Identifier.parse("minecraft:definitely_not_an_item"))).isFalse();
		assertThat(ItemWrapper.isItem("not an item object")).isFalse();
		assertThat(ItemWrapper.isItem(Items.DIAMOND)).isFalse();
	}
}
