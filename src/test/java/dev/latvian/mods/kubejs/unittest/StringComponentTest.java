package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BootstrapExtension.class)
public class StringComponentTest {
	@Test
	void hasPriorityForStringLikeValues() {
		assertThat(StringComponent.STRING.hasPriority(null, "hello")).isTrue();
		assertThat(StringComponent.STRING.hasPriority(null, 'c')).isTrue();
		assertThat(StringComponent.STRING.hasPriority(null, new JsonPrimitive("x"))).isTrue();
		assertThat(StringComponent.STRING.hasPriority(null, 5)).isFalse();
		assertThat(StringComponent.STRING.hasPriority(null, new JsonPrimitive(5))).isFalse();
	}

	@Test
	void isEmptyChecksLength() {
		assertThat(StringComponent.STRING.isEmpty("")).isTrue();
		assertThat(StringComponent.STRING.isEmpty("a")).isFalse();
	}

	@Test
	void spreadSplitsIntoCharacters() {
		assertThat(StringComponent.STRING.spread("abc")).containsExactly('a', 'b', 'c');
		assertThat(StringComponent.STRING.spread("")).isEqualTo(List.of());
	}

	@Test
	void displayStringIsEscapedAndWrapped() {
		assertThat(StringComponent.STRING.toString(null, "hello")).isEqualTo("'hello'");
		assertThat(StringComponent.STRING.toString(null, "it's")).isEqualTo("\"it's\"");
	}

	@Test
	void allowEmptyFollowsVariant() {
		assertThat(StringComponent.STRING.allowEmpty()).isFalse();
		assertThat(StringComponent.OPTIONAL_STRING.allowEmpty()).isTrue();
	}

	@Test
	void metadataIsStable() {
		assertThat(StringComponent.STRING.typeInfo()).isEqualTo(TypeInfo.STRING);
		assertThat(StringComponent.STRING.toString()).contains("string");
	}
}
