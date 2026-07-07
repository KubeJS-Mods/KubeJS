package dev.latvian.mods.kubejs.unittest;

import dev.latvian.mods.kubejs.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NBTUtilsTest {
	private static String quote(String input) {
		var sb = new StringBuilder();
		NBTUtils.quoteAndEscape(sb, input);
		return sb.toString();
	}

	@Test
	void plainStringUsesSingleQuotes() {
		assertThat(quote("hello")).isEqualTo("'hello'");
	}

	@Test
	void doubleQuoteContentWrappedInSingleQuotes() {
		assertThat(quote("he\"llo")).isEqualTo("'he\"llo'");
	}

	@Test
	void singleQuoteContentWrappedInDoubleQuotes() {
		assertThat(quote("he'llo")).isEqualTo("\"he'llo\"");
	}

	@Test
	void backslashIsEscaped() {
		assertThat(quote("a\\b")).isEqualTo("'a\\\\b'");
	}

	@Test
	void wrappingQuoteInContentIsEscaped() {
		assertThat(quote("it's \"x\"")).isEqualTo("\"it's \\\"x\\\"\"");
	}

	@Test
	void quoteAndEscapeAppendsToExistingContent() {
		var sb = new StringBuilder("prefix=");
		NBTUtils.quoteAndEscape(sb, "hi");
		assertThat(sb.toString()).isEqualTo("prefix='hi'");
	}

	@Test
	void toJsonMapsNullAndEndTagToJsonNull() {
		assertThat(NBTUtils.toJson(null).isJsonNull()).isTrue();
		assertThat(NBTUtils.toJson(EndTag.INSTANCE).isJsonNull()).isTrue();
	}

	@Test
	void toJsonConvertsPrimitiveTags() {
		assertThat(NBTUtils.toJson(StringTag.valueOf("hi")).getAsString()).isEqualTo("hi");
		assertThat(NBTUtils.toJson(IntTag.valueOf(7)).getAsInt()).isEqualTo(7);
		assertThat(NBTUtils.toJson(DoubleTag.valueOf(2.5)).getAsDouble()).isEqualTo(2.5);
	}

	@Test
	void toJsonConvertsListTagToArray() {
		var list = new ListTag();
		list.add(IntTag.valueOf(1));
		list.add(IntTag.valueOf(2));

		var json = NBTUtils.toJson(list);

		assertThat(json.isJsonArray()).isTrue();
		assertThat(json.getAsJsonArray().size()).isEqualTo(2);
		assertThat(json.getAsJsonArray().get(0).getAsInt()).isEqualTo(1);
	}

	@Test
	void toJsonConvertsCompoundTagToObject() {
		var tag = new CompoundTag();
		tag.put("name", StringTag.valueOf("Steve"));
		tag.put("age", IntTag.valueOf(5));

		var json = NBTUtils.toJson(tag);

		assertThat(json.isJsonObject()).isTrue();
		var obj = json.getAsJsonObject();
		assertThat(obj.get("name").getAsString()).isEqualTo("Steve");
		assertThat(obj.get("age").getAsInt()).isEqualTo(5);
	}
}
