package dev.latvian.mods.kubejs.stages.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.network.FriendlyByteBuf;

public class ExactPredicate implements StagePredicate {
	final String stage;

	public ExactPredicate(String stage) {
		this.stage = stage;
	}

	@Override
	public boolean test(Stages stages) {
		return stages.has(stage);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(stage);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeInt(EXACT_PREDICATE);
		buf.writeUtf(stage);
	}
}
