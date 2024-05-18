package dev.latvian.mods.kubejs.core.mixin.fabric;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.crafting.ShapedRecipe.class)
public class ShapedRecipeMixin {
	@Inject(
		method = "itemStackFromJson",
		at = @At(
			value = "RETURN",
			remap = false
		),
		cancellable = false
	)
	private static void itemStackFromJson(JsonObject jsonObject, CallbackInfoReturnable<ItemStack> cir) {
		if(cir.getReturnValue() == null || cir.getReturnValue() == ItemStack.EMPTY) {
			return;
		}

		if(jsonObject.has("nbt")) {
			var json = jsonObject.get("nbt");
			// Process null
			if (json == null || json.isJsonNull()) {
				return;
			}

			try {
				CompoundTag tag = null;
				if (json.isJsonObject()) {
					// We use a normal .toString() to convert the json to string, and read it as SNBT.
					// Using DynamicOps would mess with the type of integers and cause things like damage comparisons to fail...
					tag = TagParser.parseTag(json.toString());
				} else {
					// Assume it's a string representation of the NBT
					tag = TagParser.parseTag(GsonHelper.convertToString(json, "nbt"));
				}
				cir.getReturnValue().getOrCreateTag().merge(tag);
			} catch (CommandSyntaxException commandSyntaxException) {
				throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
			}
		}
	}
}
