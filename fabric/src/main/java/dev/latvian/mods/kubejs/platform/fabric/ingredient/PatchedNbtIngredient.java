/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatchedNbtIngredient implements CustomIngredient {
	public static final CustomIngredientSerializer<PatchedNbtIngredient> SERIALIZER = new Serializer();

	private final Ingredient base;
	@Nullable
	private final CompoundTag nbt;
	private final boolean strict;

	public PatchedNbtIngredient(Ingredient base, @Nullable CompoundTag nbt, boolean strict) {
		if (nbt == null && !strict) {
			throw new IllegalArgumentException("NbtIngredient can only have null NBT in strict mode");
		}

		this.base = base;
		this.nbt = nbt;
		this.strict = strict;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (!base.test(stack)) {
			return false;
		}

		if (strict) {
			return Objects.equals(nbt, stack.getTag());
		} else {
			return NbtUtils.compareNbt(nbt, stack.getTag(), true);
		}
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> stacks = new ArrayList<>(List.of(base.getItems()));
		stacks.replaceAll(stack -> {
			ItemStack copy = stack.copy();

			if (nbt != null) {
				copy.setTag(nbt.copy());
			}

			return copy;
		});
		stacks.removeIf(stack -> !base.test(stack));
		return stacks;
	}

	@Override
	public boolean requiresTesting() {
		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements CustomIngredientSerializer<PatchedNbtIngredient> {
		private final ResourceLocation id = new ResourceLocation("fabric", "nbt");

		@Override
		public ResourceLocation getIdentifier() {
			return id;
		}

		@Override
		public PatchedNbtIngredient read(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"));
			CompoundTag nbt = readNbt(json.get("nbt"));
			boolean strict = GsonHelper.getAsBoolean(json, "strict", false);
			return new PatchedNbtIngredient(base, nbt, strict);
		}

		@Nullable
		private static CompoundTag readNbt(@Nullable JsonElement json) {
			if (json == null || json.isJsonNull()) {
				return null;
			}

			try {
				if (json.isJsonObject()) {
					return TagParser.parseTag(json.toString());
				} else {
					return TagParser.parseTag(GsonHelper.convertToString(json, "nbt"));
				}
			} catch (CommandSyntaxException commandSyntaxException) {
				throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
			}
		}

		@Override
		public void write(JsonObject json, PatchedNbtIngredient ingredient) {
			json.add("base", ingredient.base.toJson());
			json.addProperty("strict", ingredient.strict);

			if (ingredient.nbt != null) {
				json.addProperty("nbt", ingredient.nbt.toString());
			}
		}

		@Override
		public PatchedNbtIngredient read(FriendlyByteBuf buf) {
			Ingredient base = Ingredient.fromNetwork(buf);
			CompoundTag nbt = buf.readNbt();
			boolean strict = buf.readBoolean();
			return new PatchedNbtIngredient(base, nbt, strict);
		}

		@Override
		public void write(FriendlyByteBuf buf, PatchedNbtIngredient ingredient) {
			ingredient.base.toNetwork(buf);
			buf.writeNbt(ingredient.nbt);
			buf.writeBoolean(ingredient.strict);
		}
	}
}
