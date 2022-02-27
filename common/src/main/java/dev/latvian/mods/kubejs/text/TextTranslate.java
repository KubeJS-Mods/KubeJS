package dev.latvian.mods.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.JsonIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class TextTranslate extends Text {
	private static final Object[] NO_OBJECTS = {};

	private final String key;
	private final Object[] objects;

	public TextTranslate(String k, Object[] o) {
		key = k;
		objects = o;

		for (var i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Component || !(objects[i] instanceof Text) && JsonIO.toPrimitive(JsonIO.of(objects[i])) == null) {
				objects[i] = of(objects[i]);
			}
		}
	}

	public TextTranslate(String k) {
		key = k;
		objects = NO_OBJECTS;
	}

	public String getKey() {
		return key;
	}

	public Object[] getObjects() {
		return objects;
	}

	@Override
	public TranslatableComponent rawComponent() {
		var o = new Object[objects.length];

		for (var i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Text text) {
				o[i] = text.component();
			} else if (objects[i] instanceof Component component) {
				o[i] = component.copy();
			} else {
				o[i] = objects[i];
			}
		}

		return new TranslatableComponent(key, o);
	}

	@Override
	public Text rawCopy() {
		var o = new Object[objects.length];

		for (var i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Text text) {
				o[i] = text.copy();
			} else if (objects[i] instanceof Component component) {
				o[i] = component.copy();
			} else {
				o[i] = objects[i];
			}
		}

		return new TextTranslate(key, o);
	}

	@Override
	public JsonObject toJson() {
		var o = getStyleAndSiblingJson();
		o.addProperty("translate", key);

		if (objects.length > 0) {
			var array = new JsonArray();

			for (var ob : objects) {
				array.add(JsonIO.of(ob));
			}

			o.add("with", array);
		}

		return o;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TextTranslate translate) || !key.equals(translate.key)) {
			return false;
		} else {
			var o = translate.objects;

			if (objects.length == o.length) {
				for (var i = 0; i < objects.length; i++) {
					if (!Objects.equals(objects[i], o[i])) {
						return false;
					}
				}

				return super.equals(obj);
			}

			return false;
		}
	}

	@Override
	public int hashCode() {
		return (key.hashCode() * 31 + Objects.hash(objects)) * 31 + super.hashCode();
	}
}