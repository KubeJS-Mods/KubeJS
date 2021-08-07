package dev.latvian.kubejs.mixin.forge;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.forge.WorldgenAddEventJSForge;
import dev.latvian.kubejs.world.gen.forge.WorldgenRemoveEventJSForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {
	@Inject(method = "enhanceBiome", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks$BiomeCallbackFunction;apply(Lnet/minecraft/world/level/biome/Biome$ClimateSettings;Lnet/minecraft/world/level/biome/Biome$BiomeCategory;Ljava/lang/Float;Ljava/lang/Float;Lnet/minecraft/world/level/biome/BiomeSpecialEffects;Lnet/minecraft/world/level/biome/BiomeGenerationSettings;Lnet/minecraft/world/level/biome/MobSpawnSettings;)Lnet/minecraft/world/level/biome/Biome;", remap = false, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void enhanceBiomeKJS(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, Float depth, Float scale, BiomeSpecialEffects effects, BiomeGenerationSettings gen, MobSpawnSettings spawns, RecordCodecBuilder.Instance<Biome> codec, ForgeHooks.BiomeCallbackFunction callback, CallbackInfoReturnable<Biome> cir, BiomeGenerationSettingsBuilder genBuilder, MobSpawnInfoBuilder spawnBuilder, BiomeLoadingEvent event) {
		new WorldgenRemoveEventJSForge(event).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_REMOVE);
		new WorldgenAddEventJSForge(event).post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD);
	}
}
