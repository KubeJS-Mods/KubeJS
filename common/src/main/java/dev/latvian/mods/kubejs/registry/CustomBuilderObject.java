package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

// Class that acts as a holder for a custom registry object, usually based off a class from another mod.
class CustomBuilderObject extends BuilderBase {

	private final Supplier<Object> object;
	private final RegistryInfo registry;

	public CustomBuilderObject(ResourceLocation i, Supplier<Object> object, RegistryInfo registry) {
		super(i);
		this.object = object;
		this.registry = registry;
		// This, along with overriding getTranslationKeyGroup, is to avoid a crash occurring from getRegistryType returning null
		// when called from the super constructor, because the value it returns is not set until after the super constructor is run
		this.translationKey = getTranslationKeyGroup() + "." + id.getNamespace() + "." + id.getPath();
	}

	@Override
	public String getTranslationKeyGroup() {
		if (getRegistryType() == null) {
			return "If you see this something broke. Please file a bug report.";
		} else {
			return super.getTranslationKeyGroup();
		}
	}

	@Override
	public RegistryInfo getRegistryType() {
		return registry;
	}

	@Override
	public Object createObject() {
		return object.get();
	}
}