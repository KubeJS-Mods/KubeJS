package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UniformGenerator.class)
public abstract class RandomValueBoundsMixin implements JsonSerializableKJS {
	@Shadow
	@Final
	private NumberProvider min;
	@Shadow
	@Final
	private NumberProvider max;

	@Override
	public JsonElement toJsonKJS() {
		if (min == max) {
			return new JsonPrimitive(min);
		}

		JsonObject o = new JsonObject();
		// FIXME!
		o.addProperty("type", NumberProviders.UNIFORM.toString());
		o.addProperty("min", min);
		o.addProperty("max", max);
		return o;
	}
}