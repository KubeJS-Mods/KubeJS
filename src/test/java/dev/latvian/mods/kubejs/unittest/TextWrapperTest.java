package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextWrapper;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TextWrapperTest {
	@Test
	void ofReturnsSameComponent() {
		var component = Component.literal("hi");
		assertThat(TextWrapper.of(component)).isSameAs(component);
	}

	@Test
	void ofStringEmptyGivesEmptyComponent() {
		var component = TextWrapper.ofString("");
		assertThat(TextWrapper.isEmpty(component)).isTrue();
	}

	@Test
	void ofStringNonEmptyGivesLiteral() {
		var component = TextWrapper.ofString("hello");
		assertThat(component.getString()).isEqualTo("hello");
		assertThat(TextWrapper.isEmpty(component)).isFalse();
	}

	@Test
	void emptyIsEmpty() {
		var component = TextWrapper.empty();
		assertThat(component.getString()).isEmpty();
		assertThat(TextWrapper.isEmpty(component)).isTrue();
	}

	@Test
	void stringAndLiteralProduceLiterals() {
		assertThat(TextWrapper.string("abc").getString()).isEqualTo("abc");
		assertThat(TextWrapper.literal("abc").getString()).isEqualTo("abc");
	}

	@Test
	void translateProducesTranslatableContents() {
		var contents = TextWrapper.translate("my.key").getContents();
		assertThat(contents).isInstanceOf(TranslatableContents.class);
		assertThat(((TranslatableContents) contents).getKey()).isEqualTo("my.key");
	}

	@Test
	void translatableProducesTranslatableContents() {
		var contents = TextWrapper.translatable("my.key").getContents();
		assertThat(contents).isInstanceOf(TranslatableContents.class);
		assertThat(((TranslatableContents) contents).getKey()).isEqualTo("my.key");
	}

	@Test
	void translateWithFallbackKeepsKeyAndFallback() {
		var contents = TextWrapper.translateWithFallback("my.key", "fallback").getContents();
		assertThat(contents).isInstanceOf(TranslatableContents.class);
		assertThat(((TranslatableContents) contents).getKey()).isEqualTo("my.key");
		assertThat(((TranslatableContents) contents).getFallback()).isEqualTo("fallback");
	}

	@Test
	void translatableWithFallbackKeepsKeyAndFallback() {
		var contents = TextWrapper.translatableWithFallback("my.key", "fallback").getContents();
		assertThat(contents).isInstanceOf(TranslatableContents.class);
		assertThat(((TranslatableContents) contents).getFallback()).isEqualTo("fallback");
	}

	@Test
	void keybindProducesKeybindContents() {
		var contents = TextWrapper.keybind("key.jump").getContents();
		assertThat(contents).isInstanceOf(KeybindContents.class);
		assertThat(((KeybindContents) contents).getName()).isEqualTo("key.jump");
	}

	@Test
	void joinVarargsConcatenates() {
		var joined = TextWrapper.join(Component.literal("a"), Component.literal("b"));
		assertThat(joined.getString()).isEqualTo("ab");
	}

	@Test
	void joinWithSeparator() {
		var joined = TextWrapper.join(Component.literal("-"), List.of(Component.literal("a"), Component.literal("b")));
		assertThat(joined.getString()).isEqualTo("a-b");
	}

	@Test
	void loreKeepsLines() {
		List<Component> lines = List.of(Component.literal("line1"), Component.literal("line2"));
		assertThat(TextWrapper.lore(lines).lines()).isEqualTo(lines);
	}

	@Test
	void prettyPrintNbtReturnsComponent() {
		assertThat(TextWrapper.prettyPrintNbt(IntTag.valueOf(5))).isNotNull();
	}
}
