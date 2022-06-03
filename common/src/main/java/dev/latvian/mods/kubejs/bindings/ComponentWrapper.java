package dev.latvian.mods.kubejs.bindings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.mods.kubejs.core.ComponentKJS;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class ComponentWrapper {

	public static MutableComponent of(@Nullable Object o) {
		o = UtilsJS.wrap(o, JSObjectType.ANY);
		if (o == null) {
			return new TextComponent("null");
		} else if (o instanceof Component component) {
			return component.copy();
		} else if (o instanceof CharSequence || o instanceof Number || o instanceof Character) {
			return new TextComponent(o.toString());
		} else if (o instanceof Enum<?> e) {
			return new TextComponent(e.name());
		} else if (o instanceof StringTag tag) {
			var s = tag.getAsString();
			if (s.startsWith("{") && s.endsWith("}")) {
				try {
					return Component.Serializer.fromJson(s);
				} catch (JsonParseException ex) {
					return new TextComponent("Error: " + ex);
				}
			} else {
				return new TextComponent(s);
			}
		} else if (o instanceof ListJS list) {
			var text = TextComponent.EMPTY.copy();

			for (var e1 : list) {
				text.append(of(e1));
			}

			return text;
		} else if (o instanceof MapJS map && (map.containsKey("text") || map.containsKey("translate"))) {
			MutableComponent text;

			if (map.containsKey("text")) {
				text = new TextComponent(map.get("text").toString());
			} else {
				Object[] with;

				if (map.containsKey("with")) {
					var a = map.getOrNewList("with");
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

				text = new TranslatableComponent(map.get("translate").toString(), with);
			}

			text.withStyle(style -> {
				if (map.containsKey("color")) {
					style.withColor(ColorWrapper.of(map.get("color")).createTextColorJS());
				}

				style.withBold((Boolean) map.getOrDefault("bold", null));
				style.withItalic((Boolean) map.getOrDefault("italic", null));
				style.withUnderlined((Boolean) map.getOrDefault("underlined", null));
				style.withStrikethrough((Boolean) map.getOrDefault("strikethrough", null));
				style.withObfuscated((Boolean) map.getOrDefault("obfuscated", null));

				style.withInsertion((String) map.getOrDefault("insertion", null));
				style.withFont(map.containsKey("font") ? new ResourceLocation(map.get("font").toString()) : null);
				style.withClickEvent(map.containsKey("click") ? clickEventOf(map.get("click")) : null);
				style.withHoverEvent(map.containsKey("hover") ? new HoverEvent(HoverEvent.Action.SHOW_TEXT, of(map.get("hover"))) : null);

				return style;
			});

			if (map.containsKey("extra")) {
				for (var e : map.getOrNewList("extra")) {
					text.append(of(e));
				}
			}

			return text;
		}

		return new TextComponent(o.toString());
	}

	public static MutableComponent join(MutableComponent separator, Iterable<? extends Component> texts) {
		var joined = TextComponent.EMPTY.plainCopy();
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
		return new TextComponent(text);
	}

	public static MutableComponent translate(String key) {
		return new TranslatableComponent(key, new Object[0]);
	}

	public static MutableComponent translate(String key, Object... objects) {
		return new TranslatableComponent(key, objects);
	}

	public static MutableComponent keybind(String keybind) {
		return new KeybindComponent(keybind);
	}

	public static MutableComponent black(Object text) {
		return ((ComponentKJS) of(text)).black();
	}

	public static MutableComponent darkBlue(Object text) {
		return ((ComponentKJS) of(text)).darkBlue();
	}

	public static MutableComponent darkGreen(Object text) {
		return ((ComponentKJS) of(text)).darkGreen();
	}

	public static MutableComponent darkAqua(Object text) {
		return ((ComponentKJS) of(text)).darkAqua();
	}

	public static MutableComponent darkRed(Object text) {
		return ((ComponentKJS) of(text)).darkRed();
	}

	public static MutableComponent darkPurple(Object text) {
		return ((ComponentKJS) of(text)).darkPurple();
	}

	public static MutableComponent gold(Object text) {
		return ((ComponentKJS) of(text)).gold();
	}

	public static MutableComponent gray(Object text) {
		return ((ComponentKJS) of(text)).gray();
	}

	public static MutableComponent darkGray(Object text) {
		return ((ComponentKJS) of(text)).darkGray();
	}

	public static MutableComponent blue(Object text) {
		return ((ComponentKJS) of(text)).blue();
	}

	public static MutableComponent green(Object text) {
		return ((ComponentKJS) of(text)).green();
	}

	public static MutableComponent aqua(Object text) {
		return ((ComponentKJS) of(text)).aqua();
	}

	public static MutableComponent red(Object text) {
		return ((ComponentKJS) of(text)).red();
	}

	public static MutableComponent lightPurple(Object text) {
		return ((ComponentKJS) of(text)).lightPurple();
	}

	public static MutableComponent yellow(Object text) {
		return ((ComponentKJS) of(text)).yellow();
	}

	public static MutableComponent white(Object text) {
		return ((ComponentKJS) of(text)).white();
	}

	public static ClickEvent clickEventOf(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof ClickEvent ce) {
			return ce;
		} else if (o instanceof JsonSerializable jsonSerializable) {
			if (jsonSerializable.toJson() instanceof JsonObject json) {
				var action = GsonHelper.getAsString(json, "action");
				var value = GsonHelper.getAsString(json, "value");
				return new ClickEvent(Objects.requireNonNull(ClickEvent.Action.getByName(action), "Invalid click event action %s!".formatted(action)), value);
			}
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
}