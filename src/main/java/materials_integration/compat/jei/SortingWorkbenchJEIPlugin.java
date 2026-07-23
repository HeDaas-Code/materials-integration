package materials_integration.compat.jei;

import java.util.List;
import materials_integration.MaterialsIntegration;
import materials_integration.ModRecipeTypes;
import materials_integration.SortingWorkbenchRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class SortingWorkbenchJEIPlugin implements IModPlugin {
   private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("materials_integration", "sorting_workbench");

   @Override
   public ResourceLocation getPluginUid() {
      return UID;
   }

   @Override
   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new SortingWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
   }

   @Override
   public void registerRecipes(IRecipeRegistration registration) {
      assert Minecraft.getInstance().level != null;
      RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
      List<SortingWorkbenchRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.SORTING_WORKBENCH.value());
      List<SortingWorkbenchJEIRecipe> jeiRecipes = recipes.stream().map(SortingWorkbenchJEIRecipe::new).toList();
      registration.addRecipes(SortingWorkbenchRecipeCategory.RECIPE_TYPE, jeiRecipes);
   }

   @Override
   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         new ItemStack(MaterialsIntegration.SORTING_WORKBENCH_ITEM.value()),
         SortingWorkbenchRecipeCategory.RECIPE_TYPE
      );
   }

   @Override
   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
   }
}
