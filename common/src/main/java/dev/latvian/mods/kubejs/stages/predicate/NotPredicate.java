package dev.latvian.mods.kubejs.stages.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.network.FriendlyByteBuf;

public class NotPredicate implements StagePredicate {
	final StagePredicate predicate;

	public NotPredicate(StagePredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(Stages stages) {
		return !predicate.test(stages);
	}

	@Override
	public JsonElement toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("type", "not");
		object.add("predicate", predicate.toJson());
		return object;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeInt(NOT_PREDICATE);
		predicate.toNetwork(buf);
	}
}
