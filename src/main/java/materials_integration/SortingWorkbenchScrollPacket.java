package materials_integration;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SortingWorkbenchScrollPacket implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<SortingWorkbenchScrollPacket> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("materials_integration", "sorting_workbench_scroll"));

   public static final StreamCodec<RegistryFriendlyByteBuf, SortingWorkbenchScrollPacket> STREAM_CODEC =
      StreamCodec.ofMember(
         (buf, packet) -> packet.write(buf),
         buf -> new SortingWorkbenchScrollPacket(buf)
      );

   private final int containerId;
   private final float scrollOffset;

   public SortingWorkbenchScrollPacket(int containerId, float scrollOffset) {
      this.containerId = containerId;
      this.scrollOffset = scrollOffset;
   }

   private SortingWorkbenchScrollPacket(RegistryFriendlyByteBuf buf) {
      this.containerId = buf.readInt();
      this.scrollOffset = buf.readFloat();
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeInt(this.containerId);
      buf.writeFloat(this.scrollOffset);
   }

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SortingWorkbenchScrollPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            if (player.containerMenu instanceof SortingWorkbenchMenu menu && menu.containerId == packet.containerId) {
               menu.handleScrollFromClient(packet.scrollOffset);
            }
         }
      });
   }
}
