package dev.latvian.mods.kubejs.bindings;

import net.minecraft.world.entity.MobType;

import java.util.Locale;

/**
 * @author ILIKEPIEFOO2
 */
public class MobTypeWrapper {
	public static final MobTypeWrapper UNDEFINED = new MobTypeWrapper(MobType.UNDEFINED);
	public static final MobTypeWrapper UNDEAD = new MobTypeWrapper(MobType.UNDEAD);
	public static final MobTypeWrapper ARTHROPOD = new MobTypeWrapper(MobType.ARTHROPOD);
	public static final MobTypeWrapper ILLAGER = new MobTypeWrapper(MobType.ILLAGER);
	public static final MobTypeWrapper WATER = new MobTypeWrapper(MobType.WATER);

	public final MobType type;

	public MobTypeWrapper(MobType mobtype) {
		this.type = mobtype;
	}

	public static MobTypeWrapper fromString(String s) {
		return switch (s.toLowerCase(Locale.ROOT)) {
			case "undefined" -> UNDEFINED;
			case "undead" -> UNDEAD;
			case "arthro", "arthropod" -> ARTHROPOD;
			case "illager" -> ILLAGER;
			case "water" -> WATER;
			default -> UNDEFINED;
		};
	}

	public static MobTypeWrapper of(Object o) {
		if(o instanceof MobTypeWrapper) {
			return (MobTypeWrapper) o;
		} else if(o instanceof MobType) {
			return new MobTypeWrapper((MobType) o);
		} else if(o instanceof String) {
			return fromString(o.toString());
		} else {
			return UNDEFINED;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof MobTypeWrapper)
			return type == ((MobTypeWrapper) o).type;
		if (o instanceof MobType)
			return type == o;
		if (o instanceof String)
			return this.equals(fromString((String) o));
		return false;
	}
}