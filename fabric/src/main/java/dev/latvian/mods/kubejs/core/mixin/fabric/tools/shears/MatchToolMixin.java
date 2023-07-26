package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MatchTool.class)
public abstract class MatchToolMixin {
    @ModifyVariable(at = @At("HEAD"), method = "<init>", argsOnly = true)
    private static ItemPredicate shearsLoot(ItemPredicate predicate) {
        JsonElement element = predicate.serializeToJson().deepCopy();
        JsonObject object = element.isJsonObject() ? element.getAsJsonObject() : null;
        if (object != null) {
            JsonArray items = object.getAsJsonArray("items");
            if (items != null) {
                for (JsonElement jsonElement : items.deepCopy()) {
                    if (jsonElement.getAsString().equals("minecraft:shears")) {
                        for (ResourceLocation e : ShearsItemBuilder.SHEARS_ID_SET) {
                            items.add(e.toString());
                        }
                    }
                }
            }
        }
        return ItemPredicate.fromJson(element);
    }
}