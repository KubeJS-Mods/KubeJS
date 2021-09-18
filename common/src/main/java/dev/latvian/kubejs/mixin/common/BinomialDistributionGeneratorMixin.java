package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BinomialDistributionGenerator.class)
public abstract class BinomialDistributionGeneratorMixin implements JsonSerializableKJS {
	@Shadow
	@Final
	private int n;

	@Shadow
	@Final
	private float p;

	@Override
	public JsonElement toJsonKJS() {
		JsonObject o = new JsonObject();
		o.addProperty("type", RandomIntGenerator.BINOMIAL.toString());
		o.addProperty("n", n);
		o.addProperty("p", p);
		return o;
	}
}
