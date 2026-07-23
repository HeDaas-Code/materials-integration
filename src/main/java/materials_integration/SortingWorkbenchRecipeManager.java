package materials_integration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SortingWorkbenchRecipeManager {
   public static boolean hasRecipeFor(Level level, ItemStack input) {
      if (input.isEmpty()) {
         return false;
      }
      return level != null && !getRecipesFor(level, input).isEmpty();
   }

   public static List<ItemStack> getRecipesFor(Level level, ItemStack input) {
      List<ItemStack> results = new ArrayList<>();
      if (input.isEmpty()) {
         return results;
      }
      if (level == null) {
         return results;
      }
      level.getRecipeManager()
         .getAllRecipesFor(ModRecipeTypes.SORTING_WORKBENCH.value())
         .stream()
         .map(RecipeHolder::value)
         .filter(recipe -> recipe.matches(new SingleRecipeInput(input), level))
         .flatMap(recipe -> recipe.getOutputs().stream())
         .forEach(results::add);
      return results;
   }
}
