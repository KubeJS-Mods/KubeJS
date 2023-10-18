package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SoundTypeWrapper implements TypeWrapperFactory<SoundType> {
	public static SoundType EMPTY = new SoundType(1.0f, 1.0f, SoundEvents.WOOD_BREAK, SoundEvents.WOOD_STEP, SoundEvents.WOOD_PLACE, SoundEvents.WOOD_HIT, SoundEvents.WOOD_FALL);
	public static final SoundTypeWrapper INSTANCE = new SoundTypeWrapper();

	private Map<String, SoundType> map;

	public Map<String, SoundType> getMap() {
		if (map == null) {
			map = new LinkedHashMap<>();
			map.put("empty", EMPTY);

			try {
				for (var field : SoundType.class.getFields()) {
					if (field.getType() == SoundType.class && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
						try {
							var r = RemappingHelper.getMinecraftRemapper().getMappedField(SoundType.class, field);
							map.put((r.isBlank() ? field.getName() : r).toLowerCase(), (SoundType) field.get(null));
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
	public SoundType wrap(Context cx, Object o) {
		if (o instanceof SoundType t) {
			return t;
		} else if (o == null || Undefined.isUndefined(o)) {
			return EMPTY;
		} else {
			return getMap().getOrDefault((o instanceof JsonElement j ? j.getAsString() : o.toString()).toLowerCase(), EMPTY);
		}
	}
}
