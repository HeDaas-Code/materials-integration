package materials_integration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterRenderersEvent;

@EventBusSubscriber(
   modid = "materials_integration",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class MaterialsIntegrationClient {
   @SubscribeEvent
   public static void onClientSetup(RegisterRenderersEvent event) {
      // Render type registration handled by block model JSON
   }
}
