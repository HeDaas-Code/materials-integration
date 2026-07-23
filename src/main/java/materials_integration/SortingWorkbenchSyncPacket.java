package materials_integration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SortingWorkbenchSyncPacket implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<SortingWorkbenchSyncPacket> TYPE =
      new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("materials_integration", "sorting_workbench_sync"));

   public static final StreamCodec<RegistryFriendlyByteBuf, SortingWorkbenchSyncPacket> STREAM_CODEC =
      StreamCodec.ofMember(
         SortingWorkbenchSyncPacket::write,
         SortingWorkbenchSyncPacket::new
      );

   private final int containerId;
   private final List<ItemStack> availableOutputs;
   private final int selectedOutputIndex;
   private final int scrollOffset;
   private final int totalOutputRows;

   public SortingWorkbenchSyncPacket(int containerId, List<ItemStack> availableOutputs, int selectedOutputIndex, int scrollOffset, int totalOutputRows) {
      this.containerId = containerId;
      this.availableOutputs = availableOutputs;
      this.selectedOutputIndex = selectedOutputIndex;
      this.scrollOffset = scrollOffset;
      this.totalOutputRows = totalOutputRows;
   }

   private SortingWorkbenchSyncPacket(RegistryFriendlyByteBuf buf) {
      this.containerId = buf.readInt();
      int outputCount = buf.readInt();
      this.availableOutputs = new ArrayList<>();
      for (int i = 0; i < outputCount; i++) {
         this.availableOutputs.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
      }
      this.selectedOutputIndex = buf.readInt();
      this.scrollOffset = buf.readInt();
      this.totalOutputRows = buf.readInt();
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeInt(this.containerId);
      buf.writeInt(this.availableOutputs.size());
      for (ItemStack stack : this.availableOutputs) {
         ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
      }
      buf.writeInt(this.selectedOutputIndex);
      buf.writeInt(this.scrollOffset);
      buf.writeInt(this.totalOutputRows);
   }

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SortingWorkbenchSyncPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null && minecraft.player.containerMenu instanceof SortingWorkbenchMenu menu && menu.containerId == packet.containerId) {
            menu.updateFromPacket(packet.availableOutputs, packet.selectedOutputIndex, packet.scrollOffset, packet.totalOutputRows);
            if (minecraft.screen instanceof SortingWorkbenchScreen screen) {
               screen.updateScrollFromServer(packet.scrollOffset, packet.totalOutputRows);
            }
         }
      });
   }
}
