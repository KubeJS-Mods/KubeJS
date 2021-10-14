package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.JsonSerializableKJS;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BinomialDistributionGenerator.class)
public abstract class BinomialDistributionGeneratorMixin implements NumberProvider, JsonSerializableKJS {

	@Shadow
	@Final
	private NumberProvider n;
	@Shadow
	@Final
	private NumberProvider p;

	@Override
	public JsonElement toJsonKJS() {
		JsonObject o = new JsonObject();
		// FIXME!
		o.addProperty("type", NumberProviders.BINOMIAL.toString());
		o.addProperty("n", n);
		o.addProperty("p", p);
		return o;
	}
}
