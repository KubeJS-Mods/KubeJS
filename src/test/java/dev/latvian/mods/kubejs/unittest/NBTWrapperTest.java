package dev.latvian.mods.kubejs.unittest;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NBTWrapperTest {
	@Test
	void isTagCompoundAcceptsCompoundLikeValues() {
		assertThat(NBTWrapper.isTagCompound(null)).isTrue();
		assertThat(NBTWrapper.isTagCompound(new CompoundTag())).isTrue();
		assertThat(NBTWrapper.isTagCompound("a string")).isTrue();
		assertThat(NBTWrapper.isTagCompound(new HashMap<>())).isTrue();
		assertThat(NBTWrapper.isTagCompound(new JsonObject())).isTrue();
	}

	@Test
	void isTagCompoundRejectsOtherValues() {
		assertThat(NBTWrapper.isTagCompound(5)).isFalse();
		assertThat(NBTWrapper.isTagCompound(new ListTag())).isFalse();
		assertThat(NBTWrapper.isTagCompound(IntTag.valueOf(1))).isFalse();
	}

	@Test
	void isTagCollectionAcceptsCollectionLikeValues() {
		assertThat(NBTWrapper.isTagCollection(null)).isTrue();
		assertThat(NBTWrapper.isTagCollection("a string")).isTrue();
		assertThat(NBTWrapper.isTagCollection(List.of(1, 2))).isTrue();
		assertThat(NBTWrapper.isTagCollection(new JsonArray())).isTrue();
	}

	@Test
	void isTagCollectionRejectsOtherValues() {
		assertThat(NBTWrapper.isTagCollection(new CompoundTag())).isFalse();
		assertThat(NBTWrapper.isTagCollection(5)).isFalse();
		assertThat(NBTWrapper.isTagCollection(new HashMap<>())).isFalse();
	}

	@Test
	void fromTagHandlesNullAndEnd() {
		assertThat(NBTWrapper.fromTag(null)).isNull();
		assertThat(NBTWrapper.fromTag(EndTag.INSTANCE)).isNull();
	}

	@Test
	void fromTagUnwrapsScalars() {
		assertThat(NBTWrapper.fromTag(StringTag.valueOf("hello"))).isEqualTo("hello");
		assertThat(NBTWrapper.fromTag(IntTag.valueOf(5))).isEqualTo(5);
	}

	@Test
	void fromTagUnwrapsEmptyCompoundAndList() {
		assertThat(NBTWrapper.fromTag(new CompoundTag())).isEqualTo(Map.of());
		assertThat(NBTWrapper.fromTag(new ListTag())).isEqualTo(List.of());
	}

	@Test
	void fromTagUnwrapsPopulatedCompound() {
		var tag = new CompoundTag();
		tag.put("s", StringTag.valueOf("v"));
		tag.put("n", IntTag.valueOf(7));

		var result = NBTWrapper.fromTag(tag);
		assertThat(result).isInstanceOf(Map.class);
		assertThat((Map<String, Object>) result).containsEntry("s", "v").containsEntry("n", 7);
	}

	@Test
	void fromTagUnwrapsPopulatedList() {
		var list = new ListTag();
		list.add(StringTag.valueOf("a"));
		list.add(StringTag.valueOf("b"));

		var result = NBTWrapper.fromTag(list);
		assertThat(result).isInstanceOf(List.class);
		assertThat((List<Object>) result).containsExactly("a", "b");
	}

	@Test
	void toTagIsIdentity() {
		var tag = IntTag.valueOf(3);
		assertThat(NBTWrapper.toTag(tag)).isSameAs(tag);
		assertThat(NBTWrapper.toTag(null)).isNull();
	}

	@Test
	void scalarBuildersProduceMatchingTags() {
		assertThat(NBTWrapper.byteTag((byte) 5)).isInstanceOf(ByteTag.class).isEqualTo(ByteTag.valueOf((byte) 5));
		assertThat(NBTWrapper.b((byte) 5)).isEqualTo(ByteTag.valueOf((byte) 5));
		assertThat(NBTWrapper.shortTag((short) 6)).isInstanceOf(ShortTag.class).isEqualTo(ShortTag.valueOf((short) 6));
		assertThat(NBTWrapper.s((short) 6)).isEqualTo(ShortTag.valueOf((short) 6));
		assertThat(NBTWrapper.intTag(7)).isInstanceOf(IntTag.class).isEqualTo(IntTag.valueOf(7));
		assertThat(NBTWrapper.i(7)).isEqualTo(IntTag.valueOf(7));
		assertThat(NBTWrapper.longTag(8L)).isInstanceOf(LongTag.class).isEqualTo(LongTag.valueOf(8L));
		assertThat(NBTWrapper.l(8L)).isEqualTo(LongTag.valueOf(8L));
		assertThat(NBTWrapper.floatTag(1.5F)).isInstanceOf(FloatTag.class).isEqualTo(FloatTag.valueOf(1.5F));
		assertThat(NBTWrapper.f(1.5F)).isEqualTo(FloatTag.valueOf(1.5F));
		assertThat(NBTWrapper.doubleTag(2.5)).isInstanceOf(DoubleTag.class).isEqualTo(DoubleTag.valueOf(2.5));
		assertThat(NBTWrapper.d(2.5)).isEqualTo(DoubleTag.valueOf(2.5));
		assertThat(NBTWrapper.stringTag("x")).isInstanceOf(StringTag.class).isEqualTo(StringTag.valueOf("x"));
	}

	@Test
	void arrayBuildersProduceMatchingTags() {
		assertThat(NBTWrapper.intArrayTag(new int[]{1, 2, 3})).isInstanceOf(IntArrayTag.class);
		assertThat(NBTWrapper.ia(new int[]{1, 2, 3})).isInstanceOf(IntArrayTag.class);
		assertThat(NBTWrapper.longArrayTag(new long[]{1L, 2L})).isInstanceOf(LongArrayTag.class);
		assertThat(NBTWrapper.la(new long[]{1L, 2L})).isInstanceOf(LongArrayTag.class);
		assertThat(NBTWrapper.byteArrayTag(new byte[]{1, 2})).isInstanceOf(ByteArrayTag.class);
		assertThat(NBTWrapper.ba(new byte[]{1, 2})).isInstanceOf(ByteArrayTag.class);
	}

	@Test
	void emptyContainerBuilders() {
		assertThat(NBTWrapper.compoundTag()).isInstanceOf(CompoundTag.class);
		assertThat(NBTWrapper.listTag()).isInstanceOf(ListTag.class);
	}

	@Test
	void toJsonConvertsTags() {
		assertThat(NBTWrapper.toJson(null)).isEqualTo(JsonNull.INSTANCE);
		assertThat(NBTWrapper.toJson(EndTag.INSTANCE)).isEqualTo(JsonNull.INSTANCE);

		var stringJson = NBTWrapper.toJson(StringTag.valueOf("x"));
		assertThat(stringJson).isInstanceOf(JsonPrimitive.class);
		assertThat(stringJson.getAsString()).isEqualTo("x");

		var intJson = NBTWrapper.toJson(IntTag.valueOf(5));
		assertThat(intJson).isInstanceOf(JsonPrimitive.class);
		assertThat(intJson.getAsInt()).isEqualTo(5);
	}

	@Test
	void toJsonRoundTripsCompound() {
		var tag = new CompoundTag();
		tag.put("k", StringTag.valueOf("v"));

		var json = NBTWrapper.toJson(tag);
		assertThat(json).isInstanceOf(JsonObject.class);
		assertThat(json.getAsJsonObject().get("k").getAsString()).isEqualTo("v");
	}

	@Test
	void toJsonHandlesList() {
		var list = new ListTag();
		list.add(IntTag.valueOf(1));
		list.add(IntTag.valueOf(2));

		Tag tag = list;
		var json = NBTWrapper.toJson(tag);
		assertThat(json).isInstanceOf(JsonArray.class);
		assertThat(json.getAsJsonArray()).hasSize(2);
	}
}
