package materials_integration.compat.jei;

import java.util.List;
import materials_integration.MaterialsIntegration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SortingWorkbenchRecipeCategory implements IRecipeCategory<SortingWorkbenchJEIRecipe> {
   public static final RecipeType<SortingWorkbenchJEIRecipe> RECIPE_TYPE = RecipeType.create(
      "materials_integration", "sorting_workbench", SortingWorkbenchJEIRecipe.class
   );
   private final IDrawable background;
   private final IDrawable icon;
   private final Component title;

   public SortingWorkbenchRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(160, 250);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MaterialsIntegration.SORTING_WORKBENCH_ITEM.value()));
      this.title = Component.translatable("jei.category.materials_integration.sorting_workbench");
   }

   @Override
   public RecipeType<SortingWorkbenchJEIRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   @Override
   public Component getTitle() {
      return this.title;
   }

   @Override
   @SuppressWarnings("removal")
   public IDrawable getBackground() {
      return this.background;
   }

   @Override
   public IDrawable getIcon() {
      return this.icon;
   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, SortingWorkbenchJEIRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 5, 10).addItemStack(recipe.getInputStack());
      List<ItemStack> outputs = recipe.getOutputStacks();

      for (int i = 0; i < outputs.size(); i++) {
         int x = 30 + i % 7 * 18;
         int y = 3 + i / 7 * 18;
         builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(outputs.get(i));
      }
   }
}
