package dev.latvian.mods.kubejs.testmod.assertion;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import org.assertj.core.api.AbstractAssert;

/// AssertJ entry point for asserting on any [KubeEntityEvent] - which every entity event and most
/// block events implement - from a game-test script via {@code TestRuntime.assertThat(event)}.
public class KubeEntityEventAssert extends AbstractAssert<KubeEntityEventAssert, KubeEntityEvent> {
	public KubeEntityEventAssert(KubeEntityEvent actual) {
		super(actual, KubeEntityEventAssert.class);
	}

	public KubeEntityEventAssert hasEntityType(String expected) {
		isNotNull();
		var id = BuiltInRegistries.ENTITY_TYPE.getKey(actual.getEntity().getType()).toString();

		if (!id.equals(expected)) {
			failWithMessage("Expected entity type <%s> but was <%s>", expected, id);
		}

		return this;
	}

	public KubeEntityEventAssert hasPlayer() {
		isNotNull();

		if (actual.getPlayer() == null) {
			failWithMessage("Expected the event to have a player but it did not");
		}

		return this;
	}

	public KubeEntityEventAssert hasNoPlayer() {
		isNotNull();
		var player = actual.getPlayer();

		if (player != null) {
			failWithMessage("Expected the event to have no player but was <%s>", player);
		}

		return this;
	}
}
