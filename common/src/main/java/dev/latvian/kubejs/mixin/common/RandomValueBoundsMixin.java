package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RandomValueBounds.class)
public abstract class RandomValueBoundsMixin implements JsonSerializableKJS {
	@Shadow
	@Final
	private float min;

	@Shadow
	@Final
	private float max;

	@Override
	public JsonElement toJsonKJS() {
		JsonObject o = new JsonObject();
		o.addProperty("type", RandomIntGenerator.UNIFORM.toString());
		o.addProperty("min", min);
		o.addProperty("max", max);
		return o;
	}
}