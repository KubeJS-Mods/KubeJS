package dev.latvian.mods.kubejs.stages.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface StagePredicate extends Predicate<Stages> {
	int AND_PREDICATE = 0;
	int OR_PREDICATE = 1;
	int NOT_PREDICATE = 2;
	int EXACT_PREDICATE = 3;

	JsonElement toJson();

	void toNetwork(FriendlyByteBuf buf);

	static StagePredicate of(Object o) {
		if (o instanceof StagePredicate p) {
			return p;
		} else if (o instanceof CharSequence s) {
			return new ExactPredicate(s.toString());
		}
		List<?> list = ListJS.of(o);
		if (list != null) {
			return new AndPredicate(list.stream().map(StagePredicate::of).collect(Collectors.toList()));
		}
		Map<?, ?> map = MapJS.of(o);
		if (map != null) {
			if (map.containsKey("and")) {
				List<?> l = ListJS.of(map.get("and"));
				if (l != null) return new AndPredicate(l.stream().map(StagePredicate::of).collect(Collectors.toList()));
			} else if (map.containsKey("or")) {
				List<?> l = ListJS.of(map.get("or"));
				if (l != null) return new OrPredicate(l.stream().map(StagePredicate::of).collect(Collectors.toList()));
			} else if (map.containsKey("not")) {
				return new NotPredicate(of(map.get("not")));
			}
		}
		throw new IllegalArgumentException("Unknown predicate: " + o);
	}

	static StagePredicate fromNetwork(FriendlyByteBuf buf) {
		int type = buf.readInt();
		return switch (type) {
			case AND_PREDICATE -> new AndPredicate(buf.readCollection(ArrayList::new, StagePredicate::fromNetwork));
			case OR_PREDICATE -> new OrPredicate(buf.readCollection(ArrayList::new, StagePredicate::fromNetwork));
			case NOT_PREDICATE -> new NotPredicate(fromNetwork(buf));
			case EXACT_PREDICATE -> new ExactPredicate(buf.readUtf());
			default -> throw new IllegalArgumentException("Unknown predicate type: " + type);
		};
	}

	static StagePredicate fromJson(JsonElement json) {
		if (json.isJsonPrimitive()) {
			return new ExactPredicate(json.getAsString());
		} else if (json instanceof JsonObject object && object.has("type")) {
			switch (object.get("type").getAsString()) {
				case "and" -> {
					List<StagePredicate> predicates = new ArrayList<>();
					for (JsonElement element : object.getAsJsonArray("predicates")) {
						predicates.add(fromJson(element));
					}
					return new AndPredicate(predicates);
				}
				case "or" -> {
					List<StagePredicate> predicates = new ArrayList<>();
					for (JsonElement element : object.getAsJsonArray("predicates")) {
						predicates.add(fromJson(element));
					}
					return new OrPredicate(predicates);
				}
				case "not" -> {
					return new NotPredicate(fromJson(object.get("predicate")));
				}
				default -> throw new IllegalArgumentException("Unknown stage predicate type: " + object.get("type"));
			}
		}
		throw new IllegalArgumentException("Unknown stage predicate: " + json);
	}
}
