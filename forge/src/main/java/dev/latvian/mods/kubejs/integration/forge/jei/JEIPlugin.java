package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = new ResourceLocation(KubeJS.MOD_ID, "jei");
	public IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime r) {
		runtime = r;
		BuiltinKubeJSPlugin.GLOBAL.put("jeiRuntime", runtime);

		new HideJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, object -> IngredientJS.of(object)::testVanilla, stack -> !stack.isEmpty()).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_ITEMS);

		new HideJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> {
			var fs = FluidStackJS.of(object);
			return fluidStack -> fluidStack.getFluid().isSame(fs.getFluid()) && Objects.equals(fluidStack.getTag(), fs.getNbt());
		}, stack -> !stack.isEmpty()).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_FLUIDS);

		new HideCustomJEIEventJS(runtime).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_CUSTOM);

		new RemoveJEICategoriesEvent(runtime).post(ScriptType.CLIENT, JEIIntegration.JEI_REMOVE_CATEGORIES);
		new RemoveJEIRecipesEvent(runtime).post(ScriptType.CLIENT, JEIIntegration.JEI_REMOVE_RECIPES);

		new AddJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, object -> ItemStackJS.of(object).getItemStack(), stack -> !stack.isEmpty()).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_ITEMS);
		new AddJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> fromArchitectury(FluidStackJS.of(object).getFluidStack()), stack -> !stack.isEmpty()).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_FLUIDS);
	}

	private FluidStack fromArchitectury(dev.architectury.fluid.FluidStack stack) {
		return new FluidStack(stack.getFluid(), (int) stack.getAmount(), stack.getTag());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		new JEISubtypesEventJS(registration).post(ScriptType.CLIENT, JEIIntegration.JEI_SUBTYPES);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		new InformationJEIEventJS(registration).post(ScriptType.CLIENT, JEIIntegration.JEI_INFORMATION);
	}
}