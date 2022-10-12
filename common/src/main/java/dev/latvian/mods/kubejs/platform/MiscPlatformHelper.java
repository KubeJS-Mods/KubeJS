package dev.latvian.mods.kubejs.platform;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kubejs.script.PlatformWrapper;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface MiscPlatformHelper {
	Supplier<MiscPlatformHelper> INSTANCE = Suppliers.memoize(() -> {
		var serviceLoader = ServiceLoader.load(MiscPlatformHelper.class);
		return serviceLoader.findFirst().orElseThrow(() -> new RuntimeException("Could not find platform implementation for MiscPlatformHelper!"));
	});

	static MiscPlatformHelper get() {
		return INSTANCE.get();
	}

	void setModName(PlatformWrapper.ModInfo info, String name);
}
