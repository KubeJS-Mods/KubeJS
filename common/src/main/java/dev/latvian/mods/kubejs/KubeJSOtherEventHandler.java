package dev.latvian.mods.kubejs;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.level.gen.AddWorldgenEventJS;
import dev.latvian.mods.kubejs.level.gen.RemoveWorldgenEventJS;
import dev.latvian.mods.kubejs.level.gen.ruletest.KubeJSRuleTests;

/**
 * @author LatvianModder
 */
public class KubeJSOtherEventHandler {
	public static void init() {
		KubeJSRuleTests.init();
		LifecycleEvent.SETUP.register(KubeJSOtherEventHandler::setup);
	}

	// perform anything that needs to be done post-registry here
	private static void setup() {
		WorldgenEvents.REMOVE.post(new RemoveWorldgenEventJS());
		WorldgenEvents.ADD.post(new AddWorldgenEventJS());
	}
}