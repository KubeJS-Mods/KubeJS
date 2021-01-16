package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.loot.BlockLootEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
@Mixin(LootTables.class)
public abstract class LootTablesMixin
{
	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0))
	private void test(Map<ResourceLocation, JsonElement> map, BiConsumer<ResourceLocation, JsonElement> action)
	{
		Map<ResourceLocation, JsonElement> map1 = new HashMap<>(map);
		new BlockLootEventJS(map1).post(ScriptType.SERVER, "block.loot_tables");
		map1.forEach(action);
	}
}
