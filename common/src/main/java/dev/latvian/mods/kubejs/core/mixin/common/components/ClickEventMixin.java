package dev.latvian.mods.kubejs.core.mixin.common.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickEvent.class)
public abstract class ClickEventMixin implements JsonSerializable {
	@Shadow
	public abstract ClickEvent.Action getAction();

	@Shadow
	public abstract String getValue();

	@Override
	public JsonElement toJson() {
		return Util.make(new JsonObject(), json -> {
			json.addProperty("action", getAction().getName());
			json.addProperty("value", getValue());
		});
	}
}
