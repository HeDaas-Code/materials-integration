package materials_integration.compat.jei;

import java.util.List;
import materials_integration.SortingWorkbenchRecipe;
import net.minecraft.world.item.ItemStack;

public class SortingWorkbenchJEIRecipe {
   private final SortingWorkbenchRecipe recipe;

   public SortingWorkbenchJEIRecipe(SortingWorkbenchRecipe recipe) {
      this.recipe = recipe;
   }

   public ItemStack getInputStack() {
      return new ItemStack(this.recipe.getInput());
   }

   public List<ItemStack> getOutputStacks() {
      return this.recipe.getOutputs();
   }

   public SortingWorkbenchRecipe getOriginalRecipe() {
      return this.recipe;
   }
}
