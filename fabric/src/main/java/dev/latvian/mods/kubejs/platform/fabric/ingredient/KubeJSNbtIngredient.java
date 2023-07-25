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
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KubeJSNbtIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<KubeJSNbtIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("nbt"), KubeJSNbtIngredient::new, KubeJSNbtIngredient::new);

	private final Ingredient base;
	@Nullable
	private final CompoundTag nbt;
	private final boolean strict;

	public KubeJSNbtIngredient(Ingredient base, @Nullable CompoundTag nbt, boolean strict) {
		if (nbt == null && !strict) {
			throw new IllegalArgumentException("NbtIngredient can only have null NBT in strict mode");
		}

		this.base = base;
		this.nbt = nbt;
		this.strict = strict;
	}

	public KubeJSNbtIngredient(FriendlyByteBuf buf) {
		this(Ingredient.fromNetwork(buf), buf.readNbt(), buf.readBoolean());
	}

	public KubeJSNbtIngredient(JsonObject json) {
		this(Ingredient.fromJson(json.get("base")), readNbt(json.get("nbt")), GsonHelper.getAsBoolean(json, "strict", false));
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
	public void toJson(JsonObject json) {
		json.add("base", base.toJson());

		if (strict) {
			json.addProperty("strict", true);
		}

		if (nbt != null) {
			json.addProperty("nbt", nbt.toString());
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		base.toNetwork(buf);
		buf.writeNbt(nbt);
		buf.writeBoolean(strict);
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
