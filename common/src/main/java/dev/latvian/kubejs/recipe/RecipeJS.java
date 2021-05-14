package dev.latvian.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS {
	public static RecipeJS currentRecipe = null;
	public static boolean itemErrors = false;
	private static MessageDigest messageDigest;

	public ResourceLocation id;
	public RecipeTypeJS type;
	public JsonObject json = null;
	public Recipe<?> originalRecipe = null;
	public final List<ItemStackJS> outputItems = new ArrayList<>(1);
	public final List<IngredientJS> inputItems = new ArrayList<>(1);
	public boolean serializeOutputs;
	public boolean serializeInputs;

	public abstract void create(ListJS args);

	public abstract void deserialize();

	public abstract void serialize();

	public final void deserializeJson() {
		currentRecipe = this;
		deserialize();
		currentRecipe = null;
	}

	public final void serializeJson() {
		currentRecipe = this;
		json.addProperty("type", type.getId());
		serialize();
		currentRecipe = null;
	}

	public final void save() {
		originalRecipe = null;
	}

	public RecipeJS merge(Object data) {
		JsonObject j = MapJS.json(data);

		if (j != null) {
			for (Map.Entry<String, JsonElement> entry : j.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}

			save();
		}

		return this;
	}

	@Deprecated
	public RecipeJS set(Object data) {
		return merge(data);
	}

	public RecipeJS id(ResourceLocation _id) {
		id = _id;
		save();
		return this;
	}

	public RecipeJS group(String g) {
		setGroup(g);
		save();
		return this;
	}

	public final boolean hasInput(IngredientJS ingredient, boolean exact) {
		return getInputIndex(ingredient, exact) != -1;
	}

	public final int getInputIndex(IngredientJS ingredient, boolean exact) {
		for (int i = 0; i < inputItems.size(); i++) {
			IngredientJS in = inputItems.get(i);

			if (exact ? in.equals(ingredient) : in.anyStackMatches(ingredient)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact) {
		return replaceInput(i, with, exact, (in, original) -> in.withCount(original.getCount()));
	}

	public final boolean replaceInput(IngredientJS i, IngredientJS with, boolean exact, BiFunction<IngredientJS, IngredientJS, IngredientJS> func) {
		boolean changed = false;

		for (int j = 0; j < inputItems.size(); j++) {
			if (exact ? inputItems.get(j).equals(i) : inputItems.get(j).anyStackMatches(i)) {
				inputItems.set(j, convertReplacedInput(j, inputItems.get(j), func.apply(with.copy(), inputItems.get(j))));
				changed = true;
				serializeInputs = true;
				save();
			}
		}

		return changed;
	}

	public final boolean hasOutput(IngredientJS ingredient, boolean exact) {
		return getOutputIndex(ingredient, exact) != -1;
	}

	public final int getOutputIndex(IngredientJS ingredient, boolean exact) {
		for (int i = 0; i < outputItems.size(); i++) {
			ItemStackJS out = outputItems.get(i);

			if (exact ? ingredient.equals(out) : ingredient.test(out)) {
				return i;
			}
		}

		return -1;
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact) {
		return replaceOutput(i, with, exact, (out, original) -> out.withCount(original.getCount()).withChance(original.getChance()));
	}

	public final boolean replaceOutput(IngredientJS i, ItemStackJS with, boolean exact, BiFunction<ItemStackJS, ItemStackJS, ItemStackJS> func) {
		boolean changed = false;

		for (int j = 0; j < outputItems.size(); j++) {
			if (exact ? i.equals(outputItems.get(j)) : i.test(outputItems.get(j))) {
				outputItems.set(j, convertReplacedOutput(j, outputItems.get(j), func.apply(with.copy(), outputItems.get(j))));
				changed = true;
				serializeOutputs = true;
				save();
			}
		}

		return changed;
	}

	public String getGroup() {
		JsonElement e = json.get("group");
		return e instanceof JsonPrimitive ? e.getAsString() : "";
	}

	public void setGroup(String g) {
		if (g.isEmpty()) {
			json.remove("group");
		} else {
			json.addProperty("group", g);
		}

		save();
	}

	@Override
	public String toString() {
		return getOrCreateId() + "[" + type + "]";
	}

	public String getId() {
		return getOrCreateId().toString();
	}

	public String getMod() {
		return getOrCreateId().getNamespace();
	}

	public String getPath() {
		return getOrCreateId().getPath();
	}

	public String getType() {
		return type.toString();
	}

	public ResourceLocation getOrCreateId() {
		if (id == null) {
			id = new ResourceLocation(type.getIdRL().getNamespace() + ":kjs_" + getUniqueId());
		}

		return id;
	}

	public IngredientJS convertReplacedInput(int index, IngredientJS oldIngredient, IngredientJS newIngredient) {
		return newIngredient;
	}

	public ItemStackJS convertReplacedOutput(int index, ItemStackJS oldStack, ItemStackJS newStack) {
		return newStack;
	}

	@Nullable
	public ItemStackJS resultFromRecipeJson(JsonObject json) {
		return null;
	}

	@Nullable
	public JsonElement serializeIngredientStack(IngredientStackJS in) {
		return null;
	}

	@Nullable
	public JsonElement serializeItemStack(ItemStackJS stack) {
		return null;
	}

	public IngredientJS parseIngredientItem(@Nullable Object o, String key) {
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient.isInvalidRecipeIngredient() && !Platform.isFabric()) // This is stupid >:(
		{
			if (key.isEmpty()) {
				throw new RecipeExceptionJS(o + " is not a valid ingredient!");
			} else {
				throw new RecipeExceptionJS(o + " with key '" + key + "' is not a valid ingredient!");
			}
		}

		return ingredient;
	}

	public IngredientJS parseIngredientItem(@Nullable Object o) {
		return parseIngredientItem(o, "");
	}

	public ItemStackJS parseResultItem(@Nullable Object o) {
		ItemStackJS result = ItemStackJS.of(o);

		if (result.isInvalidRecipeIngredient() && !Platform.isFabric()) // This is stupid >:(
		{
			throw new RecipeExceptionJS(o + " is not a valid result!");
		}

		return result;
	}

	public List<IngredientJS> parseIngredientItemList(@Nullable Object o) {
		List<IngredientJS> list = new ArrayList<>();

		if (o instanceof JsonElement) {
			JsonArray array;

			if (o instanceof JsonArray) {
				array = ((JsonArray) o).getAsJsonArray();
			} else {
				array = new JsonArray();
				array.add((JsonElement) o);
			}

			for (JsonElement e : array) {
				list.add(parseIngredientItem(e));
			}
		} else {
			for (Object o1 : ListJS.orSelf(o)) {
				list.add(parseIngredientItem(o1));
			}
		}

		return list;
	}

	public List<IngredientStackJS> parseIngredientItemStackList(@Nullable Object o) {
		return parseIngredientItemList(o).stream().map(IngredientJS::asIngredientStack).collect(Collectors.toList());
	}

	public List<ItemStackJS> parseResultItemList(@Nullable Object o) {
		List<ItemStackJS> list = new ArrayList<>();

		if (o instanceof JsonElement) {
			JsonArray array;

			if (o instanceof JsonArray) {
				array = ((JsonArray) o).getAsJsonArray();
			} else {
				array = new JsonArray();
				array.add((JsonElement) o);
			}

			for (JsonElement e : array) {
				list.add(parseResultItem(e));
			}
		} else {
			for (Object o1 : ListJS.orSelf(o)) {
				list.add(parseResultItem(o1));
			}
		}

		return list;
	}

	public String getFromToString() {
		return inputItems + " -> " + outputItems;
	}

	private static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		if (element == null || element.isJsonNull()) {
			stream.writeByte('-');
		} else if (element instanceof JsonArray) {
			stream.writeByte('[');
			for (JsonElement e : (JsonArray) element) {
				writeJsonHash(stream, e);
			}
		} else if (element instanceof JsonObject) {
			stream.writeByte('{');
			for (Map.Entry<String, JsonElement> e : ((JsonObject) element).entrySet()) {
				stream.writeBytes(e.getKey());
				writeJsonHash(stream, e.getValue());
			}
		} else if (element instanceof JsonPrimitive) {
			stream.writeByte('=');
			if (((JsonPrimitive) element).isBoolean()) {
				stream.writeBoolean(element.getAsBoolean());
			} else if (((JsonPrimitive) element).isNumber()) {
				stream.writeDouble(element.getAsDouble());
			} else {
				stream.writeBytes(element.getAsString());
			}
		} else {
			stream.writeByte('?');
			stream.writeInt(element.hashCode());
		}
	}

	public byte[] getJsonHashBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeJsonHash(new DataOutputStream(baos), json);
		} catch (IOException ex) {
			ex.printStackTrace();
			int h = json.hashCode();
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) (h >> 0)};
		}

		return baos.toByteArray();
	}

	public String getUniqueId() {
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				throw new InternalError("MD5 not supported", nsae);
			}
		}

		if (messageDigest == null) {
			return new BigInteger(Hex.encodeHexString(getJsonHashBytes()), 16).toString(36);
		} else {
			messageDigest.reset();
			return new BigInteger(Hex.encodeHexString(messageDigest.digest(getJsonHashBytes())), 16).toString(36);
		}
	}
}