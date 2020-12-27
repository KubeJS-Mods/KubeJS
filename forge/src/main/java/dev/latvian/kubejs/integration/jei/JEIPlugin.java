package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin
{
	public IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(KubeJS.MOD_ID, "jei");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime r)
	{
		runtime = r;
		DefaultBindings.GLOBAL.put("jeiRuntime", runtime);

		new HideJEIEventJS<>(runtime, VanillaTypes.ITEM, object -> {
			List<ItemStack> list = new ArrayList<>();

			for (ItemStackJS stack : IngredientJS.of(object).getStacks())
			{
				list.add(stack.getItemStack());
			}

			return list;
		}).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_ITEMS);

		new HideJEIEventJS<>(runtime, VanillaTypes.FLUID, object -> {
			FluidStackJS fs = FluidStackJS.of(object);
			if (fs.isEmpty())
			{
				return Collections.emptyList();
			}
			return Collections.singletonList(fromArchitectury(fs.getFluidStack()));
		}).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_FLUIDS);

		new HideCustomJEIEventJS(runtime).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_CUSTOM);

		new AddJEIEventJS<>(runtime, VanillaTypes.ITEM, object -> ItemStackJS.of(object).getItemStack()).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_ITEMS);
		new AddJEIEventJS<>(runtime, VanillaTypes.FLUID, object -> fromArchitectury(FluidStackJS.of(object).getFluidStack())).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_FLUIDS);
	}

	private FluidStack fromArchitectury(me.shedaniel.architectury.fluid.FluidStack stack)
	{
		return new FluidStack(stack.getFluid(), stack.getAmount().intValue(), stack.getTag());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration)
	{
		new AddJEISubtypesEventJS(registration).post(ScriptType.CLIENT, JEIIntegration.JEI_SUBTYPES);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		List<IngredientInfoRecipe<?>> list = new ArrayList<>();
		new InformationJEIEventJS(list).post(ScriptType.CLIENT, JEIIntegration.JEI_INFORMATION);
		registration.addRecipes(list, VanillaRecipeCategoryUid.INFORMATION);
	}
}