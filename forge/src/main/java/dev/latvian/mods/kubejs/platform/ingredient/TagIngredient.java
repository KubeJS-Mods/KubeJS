package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class TagIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<TagIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(TagIngredient::ofTagFromJson, TagIngredient::ofTagFromNetwork);

	public static TagIngredient ofTag(String tag) {
		return new TagIngredient(Tags.item(UtilsJS.getMCID(tag))).validateTag();
	}

	public static TagIngredient ofTagFromNetwork(FriendlyByteBuf buf) {
		return ofTag(buf.readUtf());
	}

	public static TagIngredient ofTagFromJson(JsonObject json) {
		return ofTag(json.get("tag").getAsString());
	}

	public final TagKey<Item> tag;
	private TagContext.Result cachedResult;

	private TagIngredient(TagKey<Item> tag) {
		this.tag = tag;
	}

	private TagIngredient validateTag() {
		if (RecipeJS.itemErrors && TagContext.INSTANCE.getValue().isEmpty(tag)) {
			throw new RecipeExceptionJS("Tag %s doesn't contain any items!".formatted(this)).error();
		}

		return this;
	}

	public Collection<Holder<Item>> getHolders() {
		var context = TagContext.INSTANCE.getValue();
		if (cachedResult == null || cachedResult.context() != context) {
			// results are cached depending on the current tag context
			cachedResult = new TagContext.Result(context, Sets.newLinkedHashSet(context.getTag(tag)));
		}
		return cachedResult.holders();
	}

	@Override
	public boolean kjs$isInvalidRecipeIngredient() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return getHolders().isEmpty();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.is(tag);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:tag");
		json.addProperty("tag", tag.location().toString());
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(tag.location().toString());
	}

	@Override
	public void kjs$gatherStacks(ItemStackSet set) {
		for (var holder : getHolders()) {
			set.addItem(holder.value());
		}
	}

	@Override
	public void kjs$gatherItemTypes(Set<Item> set) {
		for (var holder : getHolders()) {
			set.add(holder.value());
		}
	}

	@Override
	public ItemStack kjs$getFirst() {
		for (var holder : getHolders()) {
			return new ItemStack(holder);
		}

		return ItemStack.EMPTY;
	}
}
