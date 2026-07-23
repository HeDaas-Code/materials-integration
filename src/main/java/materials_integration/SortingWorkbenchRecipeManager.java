package materials_integration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SortingWorkbenchRecipeManager {
   public static boolean hasRecipeFor(ItemStack input) {
      if (input.isEmpty()) {
         return false;
      }
      Level level = getLevel();
      return level != null && !getRecipesFor(input).isEmpty();
   }

   public static List<ItemStack> getRecipesFor(ItemStack input) {
      List<ItemStack> results = new ArrayList<>();
      if (input.isEmpty()) {
         return results;
      }
      Level level = getLevel();
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

   private static Level getLevel() {
      return ServerLifecycleHooks.getCurrentServer() != null ? ServerLifecycleHooks.getCurrentServer().overworld() : null;
   }
}
