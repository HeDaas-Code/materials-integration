package materials_integration;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {
   public static void register(IEventBus modEventBus) {
      modEventBus.addListener(Networking::onRegisterPayloadHandlers);
   }

   private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("materials_integration").versioned("1");
      registrar.playToClient(
         SortingWorkbenchSyncPacket.TYPE,
         SortingWorkbenchSyncPacket.STREAM_CODEC,
         SortingWorkbenchSyncPacket::handle
      );
      registrar.playToServer(
         SortingWorkbenchScrollPacket.TYPE,
         SortingWorkbenchScrollPacket.STREAM_CODEC,
         SortingWorkbenchScrollPacket::handle
      );
   }
}
