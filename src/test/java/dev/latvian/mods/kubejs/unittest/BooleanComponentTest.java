package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(BootstrapExtension.class)
public class BooleanComponentTest {
	@Test
	void wrapReadsBooleanValues() {
		assertThat(BooleanComponent.BOOLEAN.wrap(null, true)).isTrue();
		assertThat(BooleanComponent.BOOLEAN.wrap(null, false)).isFalse();
	}

	@Test
	void wrapReadsJsonPrimitive() {
		assertThat(BooleanComponent.BOOLEAN.wrap(null, new JsonPrimitive(true))).isTrue();
		assertThat(BooleanComponent.BOOLEAN.wrap(null, new JsonPrimitive(false))).isFalse();
	}

	@Test
	void wrapParsesStrings() {
		assertThat(BooleanComponent.BOOLEAN.wrap(null, "true")).isTrue();
		assertThat(BooleanComponent.BOOLEAN.wrap(null, "TRUE")).isTrue();
		assertThat(BooleanComponent.BOOLEAN.wrap(null, "false")).isFalse();
		assertThat(BooleanComponent.BOOLEAN.wrap(null, "yes")).isFalse();
	}

	@Test
	void wrapRejectsOtherTypes() {
		assertThatThrownBy(() -> BooleanComponent.BOOLEAN.wrap(null, 5))
			.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> BooleanComponent.BOOLEAN.wrap(null, null))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void hasPriorityOnlyForBooleans() {
		assertThat(BooleanComponent.BOOLEAN.hasPriority(null, true)).isTrue();
		assertThat(BooleanComponent.BOOLEAN.hasPriority(null, new JsonPrimitive(true))).isTrue();
		assertThat(BooleanComponent.BOOLEAN.hasPriority(null, "true")).isFalse();
		assertThat(BooleanComponent.BOOLEAN.hasPriority(null, new JsonPrimitive("x"))).isFalse();
		assertThat(BooleanComponent.BOOLEAN.hasPriority(null, 5)).isFalse();
	}

	@Test
	void metadataIsStable() {
		assertThat(BooleanComponent.BOOLEAN.toString()).isEqualTo("boolean");
		assertThat(BooleanComponent.BOOLEAN.typeInfo()).isEqualTo(TypeInfo.BOOLEAN);
		assertThat(BooleanComponent.BOOLEAN.codec()).isSameAs(Codec.BOOL);
	}
}
