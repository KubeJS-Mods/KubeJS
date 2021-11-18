package dev.latvian.kubejs.bindings;

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
        switch (s.toLowerCase(Locale.ROOT)) {
            case "undefined": return UNDEFINED;
            case "undead": return UNDEAD;
            case "arthro": return ARTHROPOD;
            case "illager": return ILLAGER;
            case "water": return WATER;
            default: return UNDEFINED;
        }
    }
}
