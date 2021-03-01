package dev.latvian.kubejs.docs;

import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
public class KubeJSDocs {
	public static void init() {
		DocumentationEvent.EVENT.register(KubeJSDocs::documentation);
	}

	public static void documentation(DocumentationEvent event) {
		event.type(Object.class)
				.field("class", f -> f
						.type(Class.class)
						.comment("Returns the runtime class of this object")
				)
				.method("hashCode", m -> m
						.type(int.class)
						.comment("Returns a hash code value for the object", "This method is supported for the benefit of hash tables such as those provided by HashMap")
				)
				.method("equals", m -> m
						.type(boolean.class)
						.param("obj", Object.class)
						.comment("Indicates whether some other object is \"equal to\" this one")
				)
				.method("toString", m -> m
						.type(String.class)
						.comment("Returns a string representation of the object, useful for logging")
				)
		;

		event.type(EventJS.class)
				.method("cancel", m -> m
						.comment("Stop event")
				)
		;
	}
}