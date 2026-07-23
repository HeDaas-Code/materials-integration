package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
   public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "materials_integration");
   public static final DeferredHolder<RecipeType<?>, RecipeType<SortingWorkbenchRecipe>> SORTING_WORKBENCH = RECIPE_TYPES.register(
      "sorting_workbench", () -> new RecipeType<SortingWorkbenchRecipe>() {
            @Override
            public String toString() {
               return "materials_integration:sorting_workbench";
            }
         }
   );
}
