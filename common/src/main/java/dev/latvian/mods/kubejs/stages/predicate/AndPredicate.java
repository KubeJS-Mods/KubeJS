package dev.latvian.mods.kubejs.stages.predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class AndPredicate implements StagePredicate {
	final List<StagePredicate> predicates;

	public AndPredicate(List<StagePredicate> predicates) {
		this.predicates = predicates;
	}

	@Override
	public boolean test(Stages stages) {
		for (StagePredicate predicate : predicates) {
			if (!predicate.test(stages)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public JsonElement toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("type", "and");
		JsonArray predicates = new JsonArray();
		for (StagePredicate predicate : this.predicates) {
			predicates.add(predicate.toJson());
		}
		object.add("predicates", predicates);
		return object;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeInt(AND_PREDICATE);
		buf.writeCollection(predicates, (buf1, predicate) -> predicate.toNetwork(buf1));
	}
}
