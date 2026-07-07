package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IDTest {
	@Test
	void namespaceDefaultsToMinecraft() {
		assertThat(ID.namespace("mod:thing")).isEqualTo("mod");
		assertThat(ID.namespace("thing")).isEqualTo("minecraft");
		assertThat(ID.namespace(null)).isEqualTo("minecraft");
		assertThat(ID.namespace("")).isEqualTo("minecraft");
	}

	@Test
	void pathStripsNamespace() {
		assertThat(ID.path("mod:thing")).isEqualTo("thing");
		assertThat(ID.path("thing")).isEqualTo("thing");
		assertThat(ID.path(null)).isEqualTo("air");
	}

	@Test
	void stringQualifiesWithMinecraft() {
		assertThat(ID.string("air")).isEqualTo("minecraft:air");
		assertThat(ID.string("mod:x")).isEqualTo("mod:x");
		assertThat(ID.string("")).isEmpty();
	}

	@Test
	void kjsStringQualifiesWithKubeJS() {
		assertThat(ID.kjsString("x")).isEqualTo("kubejs:x");
		assertThat(ID.kjsString("mod:x")).isEqualTo("mod:x");
	}

	@Test
	void reduceDropsMinecraftNamespaceOnly() {
		assertThat(ID.reduce(Identifier.parse("minecraft:stone"))).isEqualTo("stone");
		assertThat(ID.reduce(Identifier.parse("mod:stone"))).isEqualTo("mod:stone");
	}

	@Test
	void resourcePathFlattensForeignNamespace() {
		assertThat(ID.resourcePath(Identifier.parse("minecraft:stone"))).isEqualTo("stone");
		assertThat(ID.resourcePath(Identifier.parse("mod:stone"))).isEqualTo("mod/stone");
	}

	@Test
	void isValidKeyRejectsMalformedStrings() {
		assertThat(ID.isValidKey("minecraft:stone")).isTrue();
		assertThat(ID.isValidKey("bad id!")).isFalse();
		assertThat(ID.isValidKey(42)).isFalse();
	}
}
