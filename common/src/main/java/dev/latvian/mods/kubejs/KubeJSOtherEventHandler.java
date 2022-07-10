package dev.latvian.mods.kubejs;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.level.gen.WorldgenAddEventJS;
import dev.latvian.mods.kubejs.level.gen.WorldgenRemoveEventJS;
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
		WorldgenRemoveEventJS.EVENT.post(new WorldgenRemoveEventJS());
		WorldgenAddEventJS.EVENT.post(new WorldgenAddEventJS());
	}
}