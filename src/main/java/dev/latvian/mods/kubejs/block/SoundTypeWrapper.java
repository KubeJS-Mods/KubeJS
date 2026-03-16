package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SoundType;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SoundTypeWrapper implements TypeWrapperFactory<SoundType> {
	public static final SoundTypeWrapper INSTANCE = new SoundTypeWrapper();

	private Map<String, SoundType> map;

	public Map<String, SoundType> getMap() {
		if (map == null) {
			map = new LinkedHashMap<>();
			map.put("empty", SoundType.EMPTY);

			try {
				for (var field : SoundType.class.getFields()) {
					if (field.getType() == SoundType.class && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
						try {
							map.put(field.getName().toLowerCase(Locale.ROOT), (SoundType) field.get(null));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return map;
	}

	@Override
	public SoundType wrap(Context cx, Object o, TypeInfo target) {
		return switch (o) {
			case SoundType t -> t;
			case null -> throw new KubeRuntimeException("SoundType cannot be null!").source(SourceLine.of(cx));
			case Undefined u -> throw new KubeRuntimeException("Cannot wrap undefined as SoundType!").source(SourceLine.of(cx));
			case Scriptable s when Undefined.isUndefined(s) -> throw new KubeRuntimeException("Cannot wrap undefined as SoundType!").source(SourceLine.of(cx));
			case JsonPrimitive j -> wrap(cx, j.getAsString(), target);
			case Identifier id -> wrap(cx, id.toString(), target);
			// TODO: maybe a record-style type wrapper? that's kinda all SoundType is anyways
			case CharSequence cs -> {
				var soundType = getMap().get(cs.toString());
				if (soundType != null) {
					yield soundType;
				}

				throw new KubeRuntimeException("Unknown SoundType '%s'".formatted(o)).source(SourceLine.of(cx));
			}
			default -> throw new KubeRuntimeException("Don't know how to wrap %s as sound type!".formatted(o)).source(SourceLine.of(cx));
		};
	}
}
