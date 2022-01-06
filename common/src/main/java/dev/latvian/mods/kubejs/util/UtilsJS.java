package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.registry.block.ToolType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.world.LevelJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.text.Text;
import dev.latvian.mods.kubejs.text.TextString;
import dev.latvian.mods.kubejs.text.TextTranslate;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

/**
 * @author LatvianModder
 */
public class UtilsJS {
	public static final Random RANDOM = new Random();
	public static final Pattern REGEX_PATTERN = Pattern.compile("\\/(.*)\\/([a-z]*)");
	public static final ResourceLocation AIR_LOCATION = new ResourceLocation("minecraft:air");
	public static final Pattern SNAKE_CASE_SPLIT = Pattern.compile("[_/]");

	public interface TryIO {
		void run() throws IOException;
	}

	public static void init() {
	}

	public static void tryIO(TryIO tryIO) {
		try {
			tryIO.run();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		return (T) o;
	}

	@Nullable
	public static Pattern parseRegex(Object o) {
		if (o instanceof CharSequence || o instanceof NativeRegExp) {
			return regex(o.toString());
		} else if (o instanceof Pattern pattern) {
			return pattern;
		}

		return null;
	}

	@Nullable
	public static Pattern regex(String string) {
		if (string.length() < 3) {
			return null;
		}

		var matcher = REGEX_PATTERN.matcher(string);

		if (matcher.matches()) {
			var flags = 0;
			var f = matcher.group(2);

			for (var i = 0; i < f.length(); i++) {
				switch (f.charAt(i)) {
					case 'd' -> flags |= Pattern.UNIX_LINES;
					case 'i' -> flags |= Pattern.CASE_INSENSITIVE;
					case 'x' -> flags |= Pattern.COMMENTS;
					case 'm' -> flags |= Pattern.MULTILINE;
					case 's' -> flags |= Pattern.DOTALL;
					case 'u' -> flags |= Pattern.UNICODE_CASE;
					case 'U' -> flags |= Pattern.UNICODE_CHARACTER_CLASS;
				}
			}

			return Pattern.compile(matcher.group(1), flags);
		}

		return null;
	}

	public static String toRegexString(Pattern pattern) {
		var sb = new StringBuilder("/");
		sb.append(pattern.pattern());
		sb.append('/');

		var flags = pattern.flags();

		if ((flags & Pattern.UNIX_LINES) != 0) {
			sb.append('d');
		}

		if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
			sb.append('i');
		}

		if ((flags & Pattern.COMMENTS) != 0) {
			sb.append('x');
		}

		if ((flags & Pattern.MULTILINE) != 0) {
			sb.append('m');
		}

		if ((flags & Pattern.DOTALL) != 0) {
			sb.append('s');
		}

		if ((flags & Pattern.UNICODE_CASE) != 0) {
			sb.append('u');
		}

		if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) {
			sb.append('U');
		}

		return sb.toString();
	}

	public static void queueIO(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Path getFile(String path) throws IOException {
		return KubeJS.verifyFilePath(KubeJS.getGameDirectory().resolve(path));
	}

	@Nullable
	public static Object copy(@Nullable Object o) {
		if (o instanceof Copyable copyable) {
			return copyable.copy();
		} else if (o instanceof JsonElement json) {
			return JsonUtilsJS.copy(json);
		} else if (o instanceof Tag tag) {
			return tag.copy();
		}
		return o;
	}

	@Nullable
	public static Object wrap(@Nullable Object o, JSObjectType type) {
		//Primitives and already normalized objects
		if (o == null || o instanceof WrappedJS || o instanceof Tag || o instanceof Number || o instanceof Character || o instanceof String || o instanceof Enum || o.getClass().isPrimitive() && !o.getClass().isArray()) {
			return o;
		} else if (o instanceof CharSequence || o instanceof ResourceLocation) {
			return o.toString();
		} else if (o instanceof Wrapper w) {
			return wrap(w.unwrap(), type);
		}
		// Vanilla text component
		else if (o instanceof Component component) {
			Text t = new TextString("");

			List<Component> list = new ArrayList<>();
			list.add(component);
			list.addAll(component.getSiblings());

			for (var c : list) {
				Text t1;

				if (c instanceof TranslatableComponent tlc) {
					t1 = new TextTranslate(tlc.getKey(), tlc.getArgs());
				} else {
					t1 = new TextString(c.getContents());
				}

				//TODO: Replace with AT
				t1.bold(c.getStyle().isBold());
				t1.italic(c.getStyle().isItalic());
				t1.underlined(c.getStyle().isUnderlined());
				t1.strikethrough(c.getStyle().isStrikethrough());
				t1.obfuscated(c.getStyle().isObfuscated());
				t1.insertion(c.getStyle().getInsertion());

				var ce = c.getStyle().getClickEvent();

				if (ce != null) {
					if (ce.getAction() == ClickEvent.Action.RUN_COMMAND) {
						t1.click("command:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
						t1.click("suggest_command:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
						t1.click("copy:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.OPEN_URL) {
						t1.click(ce.getValue());
					}
				}

				var he = c.getStyle().getHoverEvent();

				if (he != null && he.getAction() == HoverEvent.Action.SHOW_TEXT) {
					t1.hover(Text.of(he.getValue(HoverEvent.Action.SHOW_TEXT)));
				}

				t.append(t1);
			}

			return t;
		}
		// Maps
		else if (o instanceof Map) {
			if (!type.checkMap()) {
				return null;
			}

			var map = new MapJS(((Map) o).size());
			map.putAll((Map) o);
			return map;
		}
		// Lists, Collections, Iterables, GSON Arrays
		else if (o instanceof Iterable) {
			if (!type.checkList()) {
				return null;
			}

			var list = new ListJS();

			for (var o1 : (Iterable) o) {
				list.add(o1);
			}

			return list;
		}
		// Arrays (and primitive arrays are a pain)
		else if (o.getClass().isArray()) {
			if (type.checkList()) {
				return ListJS.ofArray(o);
			} else {
				return null;
			}
		}
		// GSON Primitives
		else if (o instanceof JsonPrimitive json) {
			return JsonUtilsJS.toPrimitive(json);
		}
		// GSON Objects
		else if (o instanceof JsonObject json) {
			if (!type.checkMap()) {
				return null;
			}

			var map = new MapJS(json.size());

			for (var entry : json.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}

			return map;
		}
		// GSON and NBT Null
		else if (o instanceof JsonNull || o instanceof EndTag) {
			return null;
		}
		// NBT
		else if (o instanceof CompoundTag tag) {
			if (!type.checkMap()) {
				return null;
			}

			var map = new MapJS(tag.size());

			for (var s : tag.getAllKeys()) {
				map.put(s, tag.get(s));
			}

			return map;
		} else if (o instanceof NumericTag tag) {
			return tag.getAsNumber();
		} else if (o instanceof StringTag tag) {
			return tag.getAsString();
		}

		return o;
	}

	public static int parseInt(@Nullable Object object, int def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.intValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Integer.parseInt(s);
		} catch (Exception ex) {
			return def;
		}
	}

	public static long parseLong(@Nullable Object object, long def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.longValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Long.parseLong(s);
		} catch (Exception ex) {
			return def;
		}
	}

	public static double parseDouble(@Nullable Object object, double def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number num) {
			return num.doubleValue();
		}

		try {
			var s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Double.parseDouble(String.valueOf(object));
		} catch (Exception ex) {
			return def;
		}
	}

	public static ToolType getToolType(String id) {
		return ToolType.byName(id);
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.2")
	public static LevelJS getWorld(Level level) {
		return getLevel(level);
	}

	public static LevelJS getLevel(Level level) {
		if (level.isClientSide()) {
			return getClientLevel();
		} else {
			return ServerJS.instance.getLevel(level);
		}
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.2")
	public static LevelJS getClientWorld() {
		return getClientLevel();
	}

	public static LevelJS getClientLevel() {
		return KubeJS.PROXY.getClientLevel();
	}

	public static String getID(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "minecraft:air";
		}

		if (s.indexOf(':') == -1) {
			return "minecraft:" + s;
		}

		return s;
	}

	public static ResourceLocation getMCID(@Nullable Object o) {
		if (o == null) {
			return AIR_LOCATION;
		} else if (o instanceof ResourceLocation id) {
			return id;
		}

		var s = o.toString();

		if (s == null || s.isEmpty()) {
			return AIR_LOCATION;
		}

		return new ResourceLocation(s);
	}

	public static String getNamespace(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "minecraft";
		}

		var i = s.indexOf(':');
		return i == -1 ? "minecraft" : s.substring(0, i);
	}

	public static String getPath(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "air";
		}

		var i = s.indexOf(':');
		return i == -1 ? s : s.substring(i + 1);
	}

	public static BlockState parseBlockState(String string) {
		if (string.isEmpty()) {
			return Blocks.AIR.defaultBlockState();
		}

		var i = string.indexOf('[');
		var hasProperties = i >= 0 && string.indexOf(']') == string.length() - 1;
		var state = KubeJSRegistries.blocks().get(new ResourceLocation(hasProperties ? string.substring(0, i) : string)).defaultBlockState();

		if (hasProperties) {
			for (var s : string.substring(i + 1, string.length() - 1).split(",")) {
				var s1 = s.split("=", 2);

				if (s1.length == 2 && !s1[0].isEmpty() && !s1[1].isEmpty()) {
					var p = state.getBlock().getStateDefinition().getProperty(s1[0]);

					if (p != null) {
						Optional<?> o = p.getValue(s1[1]);

						if (o.isPresent()) {
							state = state.setValue(p, UtilsJS.cast(o.get()));
						}
					}
				}
			}
		}

		return state;
	}

	public static ListJS rollChestLoot(ResourceLocation id, @Nullable EntityJS entity) {
		var list = new ListJS();
		if (ServerJS.instance != null) {
			var server = ServerJS.instance.getMinecraftServer();
			var tables = ServerJS.instance.getMinecraftServer().getLootTables();
			var table = tables.get(id);

			LootContext.Builder builder;

			if (entity != null) {
				var mcEntity = entity.minecraftEntity;
				builder = new LootContext.Builder((ServerLevel) mcEntity.level)
						.withOptionalParameter(LootContextParams.THIS_ENTITY, mcEntity)
						.withParameter(LootContextParams.ORIGIN, mcEntity.position());
			} else {
				builder = new LootContext.Builder(server.overworld())
						.withOptionalParameter(LootContextParams.THIS_ENTITY, null)
						.withParameter(LootContextParams.ORIGIN, Vec3.ZERO);
			}

			table.getRandomItems(builder.create(LootContextParamSets.CHEST), (stack) -> list.add(ItemStackJS.of(stack)));
		}
		return list;
	}

	public static void postModificationEvents() {
		new BlockModificationEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MODIFICATION);
		new ItemModificationEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_MODIFICATION);
	}

	public static Class<?> getRawType(Type type) {
		if (type instanceof Class<?> clz) {
			return clz;
		} else if (type instanceof ParameterizedType paramType) {
			var rawType = paramType.getRawType();
			checkArgument(rawType instanceof Class);
			return (Class<?>) rawType;
		} else if (type instanceof GenericArrayType arrType) {
			var componentType = arrType.getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		} else if (type instanceof TypeVariable) {
			return Object.class;
		} else if (type instanceof WildcardType wildcard) {
			return getRawType(wildcard.getUpperBounds()[0]);
		}

		var className = type == null ? "null" : type.getClass().getName();
		throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
	}

	public static String convertSnakeCaseToCamelCase(String string) {
		var s = SNAKE_CASE_SPLIT.split(string, 0);

		var sb = new StringBuilder();
		var first = true;

		for (var value : s) {
			if (!value.isEmpty()) {
				if (first) {
					first = false;
					sb.append(value);
				} else {
					sb.append(Character.toUpperCase(value.charAt(0)));
					sb.append(value, 1, value.length());
				}
			}
		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static NumberProvider numberProviderOf(Object o) {
		if (o instanceof Number n) {
			var f = n.floatValue();
			return UniformGenerator.between(f, f);
		} else if (o instanceof List l && !l.isEmpty()) {
			var min = (Number) l.get(0);
			var max = l.size() >= 2 ? (Number) l.get(1) : min;
			return UniformGenerator.between(min.floatValue(), max.floatValue());
		} else if (o instanceof Map) {
			var m = (Map<String, Object>) o;
			if (m.containsKey("min") && m.containsKey("max")) {
				return UniformGenerator.between(((Number) m.get("min")).intValue(), ((Number) m.get("max")).floatValue());
			} else if (m.containsKey("n") && m.containsKey("p")) {
				return BinomialDistributionGenerator.binomial(((Number) m.get("n")).intValue(), ((Number) m.get("p")).floatValue());
			} else if (m.containsKey("value")) {
				var f = ((Number) m.get("value")).floatValue();
				return UniformGenerator.between(f, f);
			}
		}

		return ConstantValue.exactly(0);
	}

	public static JsonElement numberProviderJson(NumberProvider gen) {
		return Deserializers.createConditionSerializer().create().toJsonTree(gen);
	}
}