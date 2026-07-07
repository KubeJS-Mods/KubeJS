package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilsTest {
	@Test
	void copyReturnsJsonNullForNull() {
		assertThat(JsonUtils.copy(null)).isSameAs(JsonNull.INSTANCE);
		assertThat(JsonUtils.copy(JsonNull.INSTANCE)).isSameAs(JsonNull.INSTANCE);
	}

	@Test
	void copyReturnsSamePrimitiveInstance() {
		var prim = new JsonPrimitive(42);
		assertThat(JsonUtils.copy(prim)).isSameAs(prim);
	}

	@Test
	void copyDeepCopiesContainersIndependently() {
		var inner = new JsonArray();
		inner.add(1);

		var obj = new JsonObject();
		obj.add("list", inner);

		var copy = JsonUtils.copy(obj).getAsJsonObject();

		assertThat(copy).isEqualTo(obj);
		assertThat(copy).isNotSameAs(obj);

		inner.add(2);

		assertThat(copy.getAsJsonArray("list").size()).isEqualTo(1);
		assertThat(obj.getAsJsonArray("list").size()).isEqualTo(2);
	}

	@Test
	void toObjectMapsNullAndJsonNullToNull() {
		assertThat(JsonUtils.toObject(null)).isNull();
		assertThat(JsonUtils.toObject(JsonNull.INSTANCE)).isNull();
	}

	@Test
	void toObjectConvertsObjectToMap() {
		var obj = new JsonObject();
		obj.addProperty("a", 1);
		obj.addProperty("b", "x");

		var result = JsonUtils.toObject(obj);

		assertThat(result).isInstanceOf(Map.class);
		var map = (Map<String, Object>) result;
		assertThat(map).containsOnlyKeys("a", "b");
		assertThat(((Number) map.get("a")).intValue()).isEqualTo(1);
		assertThat(map.get("b")).isEqualTo("x");
	}

	@Test
	void toObjectConvertsArrayToList() {
		var array = new JsonArray();
		array.add(1);
		array.add(2);

		var result = JsonUtils.toObject(array);

		assertThat(result).isInstanceOf(List.class);
		assertThat((List<?>) result).hasSize(2);
	}

	@Test
	void toStringSerializesCompactAndKeepsHtmlChars() {
		assertThat(JsonUtils.toString(JsonNull.INSTANCE)).isEqualTo("null");
		assertThat(JsonUtils.toString(new JsonPrimitive("a<b"))).isEqualTo("\"a<b\"");
	}

	@Test
	void prettyStringIndentsWithTabs() {
		var obj = new JsonObject();
		obj.addProperty("a", 1);

		var pretty = JsonUtils.toPrettyString(obj);

		assertThat(pretty).contains("\n\t\"a\"");
		assertThat(JsonUtils.fromString(pretty)).isEqualTo(obj);
	}

	@Test
	void fromStringMapsBlankAndNullLiteralToJsonNull() {
		assertThat(JsonUtils.fromString(null)).isSameAs(JsonNull.INSTANCE);
		assertThat(JsonUtils.fromString("")).isSameAs(JsonNull.INSTANCE);
		assertThat(JsonUtils.fromString("null")).isSameAs(JsonNull.INSTANCE);
	}

	@Test
	void fromStringParsesValues() {
		assertThat(JsonUtils.fromString("123").getAsInt()).isEqualTo(123);
		assertThat(JsonUtils.fromString("true").getAsBoolean()).isTrue();
		assertThat(JsonUtils.fromString("\"hi\"").getAsString()).isEqualTo("hi");
	}

	@Test
	void toStringRoundTripsThroughFromString() {
		var obj = new JsonObject();
		obj.addProperty("a", 1);
		obj.addProperty("b", "x");

		assertThat(JsonUtils.fromString(JsonUtils.toString(obj))).isEqualTo(obj);
	}

	@Test
	void toPrimitiveHandlesEachPrimitiveType() {
		assertThat(JsonUtils.toPrimitive(null)).isNull();
		assertThat(JsonUtils.toPrimitive(JsonNull.INSTANCE)).isNull();
		assertThat(JsonUtils.toPrimitive(new JsonObject())).isNull();
		assertThat(JsonUtils.toPrimitive(new JsonArray())).isNull();
		assertThat(JsonUtils.toPrimitive(new JsonPrimitive(true))).isEqualTo(true);
		assertThat(((Number) JsonUtils.toPrimitive(new JsonPrimitive(7))).intValue()).isEqualTo(7);
		assertThat(JsonUtils.toPrimitive(new JsonPrimitive("hello"))).isEqualTo("hello");
	}

	@Test
	void toPrimitiveParsesNumericStringAsNumber() {
		var result = JsonUtils.toPrimitive(new JsonPrimitive("3.14"));
		assertThat(result).isInstanceOf(Number.class);
		assertThat(((Number) result).doubleValue()).isEqualTo(3.14);
	}
}
