package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
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

		JEIKubeJSEvents.HIDE_ITEMS.post(new HideJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, object -> IngredientJS.of(object)::test, stack -> !stack.isEmpty()));

		JEIKubeJSEvents.HIDE_FLUIDS.post(new HideJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> {
			var fs = FluidStackJS.of(object);
			return fluidStack -> fluidStack.getFluid().isSame(fs.getFluid()) && Objects.equals(fluidStack.getTag(), fs.getNbt());
		}, stack -> !stack.isEmpty()));

		JEIKubeJSEvents.HIDE_CUSTOM.post(new HideCustomJEIEventJS(runtime));

		JEIKubeJSEvents.REMOVE_CATEGORIES.post(new RemoveJEICategoriesEvent(runtime));
		JEIKubeJSEvents.REMOVE_RECIPES.post(new RemoveJEIRecipesEvent(runtime));

		JEIKubeJSEvents.ADD_ITEMS.post(new AddJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, object -> ItemStackJS.of(object).getItemStack(), stack -> !stack.isEmpty()));
		JEIKubeJSEvents.ADD_FLUIDS.post(new AddJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> fromArchitectury(FluidStackJS.of(object).getFluidStack()), stack -> !stack.isEmpty()));
	}

	private FluidStack fromArchitectury(dev.architectury.fluid.FluidStack stack) {
		return new FluidStack(stack.getFluid(), (int) stack.getAmount(), stack.getTag());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		JEIKubeJSEvents.SUBTYPES.post(new JEISubtypesEventJS(registration));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		JEIKubeJSEvents.INFORMATION.post(new InformationJEIEventJS(registration));
	}
}