package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
   public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "materials_integration");
   public static final DeferredHolder<MenuType<?>, MenuType<SortingWorkbenchMenu>> SORTING_WORKBENCH = MENUS.register(
      "sorting_workbench", () -> IMenuTypeExtension.create((windowId, inv, data) -> new SortingWorkbenchMenu(windowId, inv))
   );
}
