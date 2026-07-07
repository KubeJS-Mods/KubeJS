package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.component.CharacterComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BootstrapExtension.class)
public class CharacterComponentTest {
	@Test
	void hasPriorityForCharacterLikeValues() {
		assertThat(CharacterComponent.CHARACTER.hasPriority(null, 'a')).isTrue();
		assertThat(CharacterComponent.CHARACTER.hasPriority(null, "a")).isTrue();
		assertThat(CharacterComponent.CHARACTER.hasPriority(null, new JsonPrimitive("a"))).isTrue();
		assertThat(CharacterComponent.CHARACTER.hasPriority(null, 5)).isFalse();
		assertThat(CharacterComponent.CHARACTER.hasPriority(null, new JsonPrimitive(5))).isFalse();
	}

	@Test
	void isEmptyOnlyForNullCharacter() {
		assertThat(CharacterComponent.CHARACTER.isEmpty('\0')).isTrue();
		assertThat(CharacterComponent.CHARACTER.isEmpty('a')).isFalse();
	}

	@Test
	void displayStringWrapsInSingleQuotes() {
		assertThat(CharacterComponent.CHARACTER.toString(null, 'a')).isEqualTo("'a'");
	}

	@Test
	void metadataIsStable() {
		assertThat(CharacterComponent.CHARACTER.typeInfo()).isEqualTo(TypeInfo.CHARACTER);
		assertThat(CharacterComponent.CHARACTER.toString()).contains("character");
	}
}
