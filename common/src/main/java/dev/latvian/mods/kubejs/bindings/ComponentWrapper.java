package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonParseException;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public class ComponentWrapper {

	public static MutableComponent of(@Nullable Object o) {
		o = UtilsJS.wrap(o, JSObjectType.ANY);
		if (o == null) {
			return Component.literal("null");
		} else if (o instanceof Component component) {
			return component.copy();
		} else if (o instanceof CharSequence || o instanceof Number || o instanceof Character) {
			return Component.literal(o.toString());
		} else if (o instanceof Enum<?> e) {
			return Component.literal(e.name());
		} else if (o instanceof StringTag tag) {
			var s = tag.getAsString();
			if (s.startsWith("{") && s.endsWith("}")) {
				try {
					return Component.Serializer.fromJson(s);
				} catch (JsonParseException ex) {
					return Component.literal("Error: " + ex);
				}
			} else {
				return Component.literal(s);
			}
		} else if (o instanceof Map<?, ?> map && (map.containsKey("text") || map.containsKey("translate"))) {
			MutableComponent text;

			if (map.containsKey("text")) {
				text = Component.literal(map.get("text").toString());
			} else {
				Object[] with;

				if (map.get("with") instanceof Collection<?> a) {
					with = new Object[a.size()];
					var i = 0;

					for (var e1 : a) {
						with[i] = e1;

						if (with[i] instanceof MapJS || with[i] instanceof ListJS) {
							with[i] = of(e1);
						}

						i++;
					}
				} else {
					with = new Object[0];
				}

				text = Component.translatable(map.get("translate").toString(), with);
			}

			if (map.containsKey("color")) {
				text.kjs$color(ColorWrapper.of(map.get("color")));
			}

			text.kjs$bold((Boolean) map.getOrDefault("bold", null));
			text.kjs$italic((Boolean) map.getOrDefault("italic", null));
			text.kjs$underlined((Boolean) map.getOrDefault("underlined", null));
			text.kjs$strikethrough((Boolean) map.getOrDefault("strikethrough", null));
			text.kjs$obfuscated((Boolean) map.getOrDefault("obfuscated", null));

			text.kjs$insertion((String) map.getOrDefault("insertion", null));
			text.kjs$font(map.containsKey("font") ? new ResourceLocation(map.get("font").toString()) : null);
			text.kjs$click(map.containsKey("click") ? clickEventOf(map.get("click")) : null);
			text.kjs$hover(map.containsKey("hover") ? of(map.get("hover")) : null);

			if (map.get("extra") instanceof Iterable<?> itr) {
				for (var e : itr) {
					text.append(of(e));
				}
			}

			return text;
		} else if (o instanceof Iterable<?> list) {
			var text = Component.empty().copy();

			for (var e1 : list) {
				text.append(of(e1));
			}

			return text;
		}

		return Component.literal(o.toString());
	}

	public static MutableComponent ofMutable(Object o) {
		return Component.literal("").append(of(o));
	}

	public static ClickEvent clickEventOf(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof ClickEvent ce) {
			return ce;
		}

		var json = MapJS.json(o);
		if (json != null) {
			var action = GsonHelper.getAsString(json, "action");
			var value = GsonHelper.getAsString(json, "value");
			return new ClickEvent(Objects.requireNonNull(ClickEvent.Action.getByName(action), "Invalid click event action %s!".formatted(action)), value);
		}

		var s = o.toString();

		var split = s.split(":", 2);

		return switch (split[0]) {
			case "command" -> new ClickEvent(ClickEvent.Action.RUN_COMMAND, split[1]);
			case "suggest_command" -> new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, split[1]);
			case "copy" -> new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, split[1]);
			case "file" -> new ClickEvent(ClickEvent.Action.OPEN_FILE, split[1]);
			default -> {
				var action = ClickEvent.Action.getByName(split[0]);
				if (action != null) {
					yield new ClickEvent(action, split[1]);
				}

				yield new ClickEvent(ClickEvent.Action.OPEN_URL, s);
			}
		};
	}

	public static Component prettyPrintNbt(Tag tag) {
		return NbtUtils.toPrettyComponent(tag);
	}

	public static MutableComponent join(MutableComponent separator, Iterable<? extends Component> texts) {
		var joined = Component.empty().plainCopy();
		var first = true;

		for (var t : texts) {
			if (first) {
				first = false;
			} else {
				joined.append(separator);
			}

			joined.append(t);
		}

		return joined;
	}

	public static MutableComponent string(String text) {
		return Component.literal(text);
	}

	public static MutableComponent translate(String key) {
		return Component.translatable(key, new Object[0]);
	}

	public static MutableComponent translate(String key, Object... objects) {
		return Component.translatable(key, objects);
	}

	public static MutableComponent keybind(String keybind) {
		return Component.keybind(keybind);
	}

	public static MutableComponent black(Object text) {
		return of(text).kjs$black();
	}

	public static MutableComponent darkBlue(Object text) {
		return of(text).kjs$darkBlue();
	}

	public static MutableComponent darkGreen(Object text) {
		return of(text).kjs$darkGreen();
	}

	public static MutableComponent darkAqua(Object text) {
		return of(text).kjs$darkAqua();
	}

	public static MutableComponent darkRed(Object text) {
		return of(text).kjs$darkRed();
	}

	public static MutableComponent darkPurple(Object text) {
		return of(text).kjs$darkPurple();
	}

	public static MutableComponent gold(Object text) {
		return of(text).kjs$gold();
	}

	public static MutableComponent gray(Object text) {
		return of(text).kjs$gray();
	}

	public static MutableComponent darkGray(Object text) {
		return of(text).kjs$darkGray();
	}

	public static MutableComponent blue(Object text) {
		return of(text).kjs$blue();
	}

	public static MutableComponent green(Object text) {
		return of(text).kjs$green();
	}

	public static MutableComponent aqua(Object text) {
		return of(text).kjs$aqua();
	}

	public static MutableComponent red(Object text) {
		return of(text).kjs$red();
	}

	public static MutableComponent lightPurple(Object text) {
		return of(text).kjs$lightPurple();
	}

	public static MutableComponent yellow(Object text) {
		return of(text).kjs$yellow();
	}

	public static MutableComponent white(Object text) {
		return of(text).kjs$white();
	}
}