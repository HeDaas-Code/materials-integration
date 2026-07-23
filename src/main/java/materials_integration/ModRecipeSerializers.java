package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
   public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
      Registries.RECIPE_SERIALIZER, "materials_integration"
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SortingWorkbenchRecipe>> SORTING_WORKBENCH = RECIPE_SERIALIZERS.register(
      "sorting_workbench", () -> new SortingWorkbenchRecipe.Serializer()
   );
}
