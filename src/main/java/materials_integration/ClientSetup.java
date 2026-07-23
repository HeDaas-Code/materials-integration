package materials_integration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
   modid = "materials_integration",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
      event.register(ModMenuTypes.SORTING_WORKBENCH.value(), SortingWorkbenchScreen::new);
   }
}
