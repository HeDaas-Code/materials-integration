package materials_integration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class SortingWorkbenchMenu extends AbstractContainerMenu {
   private final Container inputContainer;
   private final ResultContainer outputContainer;
   private final Level level;
   private final Player player;
   private final Slot inSlot;
   private final List<ItemStack> availableOutputs = new ArrayList<>();
   private int scrollOffset = 0;
   private static final int OUTPUT_COLS = 6;
   static final int VISIBLE_ROWS = 4;
   private int totalOutputRows = 0;
   private int selectedOutputIndex = -1;

   public SortingWorkbenchMenu(int containerId, Inventory playerInventory) {
      super(ModMenuTypes.SORTING_WORKBENCH.value(), containerId);
      this.level = playerInventory.player.level();
      this.player = playerInventory.player;
      this.inputContainer = new SimpleContainer(1) {
         @Override
         public void setChanged() {
            super.setChanged();
            SortingWorkbenchMenu.this.slotsChanged(this);
         }
      };
      this.outputContainer = new ResultContainer();
      this.inSlot = this.addSlot(new Slot(this.inputContainer, 0, 6, 71) {
         @Override
         public boolean mayPlace(ItemStack stack) {
            return SortingWorkbenchRecipeManager.hasRecipeFor(stack);
         }
      });
      this.addSlot(
         new Slot(this.outputContainer, 0, 154, 71) {
            @Override
            public boolean mayPlace(ItemStack stack) {
               return false;
            }

            @Override
            public boolean mayPickup(Player player) {
               return !player.isSpectator();
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
               if (!player.isSpectator()) {
                  ItemStack inputStack = SortingWorkbenchMenu.this.inSlot.getItem();
                  int inputCount = inputStack.getCount();
                  if (inputCount > 0) {
                     if (!SortingWorkbenchMenu.this.level.isClientSide) {
                        this.handleNormalTake(player, stack, inputCount);
                        SortingWorkbenchMenu.this.updateAvailableOutputs();
                        SortingWorkbenchMenu.this.broadcastChanges();
                        SortingWorkbenchMenu.this.syncDataToClient();
                     }
                  }
               }
            }

            private void handleNormalTake(Player player, ItemStack outputStack, int inputCount) {
               int takeCount = Math.min(inputCount, outputStack.getMaxStackSize());
               ItemStack takenStack = outputStack.copy();
               takenStack.setCount(takeCount);
               SortingWorkbenchMenu.this.inSlot.remove(takeCount);
                SortingWorkbenchMenu.this.moveItemStackTo(takenStack, 2, 38, false);
               SortingWorkbenchMenu.this.level
                  .playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.BLOCKS,
                     0.5F,
                     0.8F + SortingWorkbenchMenu.this.level.random.nextFloat() * 0.4F
                  );
            }
         }
      );

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 99 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 157));
      }
   }

   @Override
   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index == 1) {
            ItemStack inputStack = this.inSlot.getItem();
            int inputCount = inputStack.getCount();
            if (inputCount <= 0) {
               return ItemStack.EMPTY;
            }

            if (!this.level.isClientSide) {
               ItemStack fullOutput = slotStack.copy();
               fullOutput.setCount(inputCount);
               ItemHandlerHelper.giveItemToPlayer(player, fullOutput);
               this.inSlot.remove(inputCount);
               this.level
                  .playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.BLOCKS,
                     0.5F,
                     0.8F + this.level.random.nextFloat() * 0.4F
                  );
               this.updateAvailableOutputs();
               this.broadcastChanges();
               this.syncDataToClient();
            }

            return ItemStack.EMPTY;
         }

         if (index == 0) {
            if (!this.moveItemStackTo(slotStack, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.slots.get(0).mayPlace(slotStack)) {
            if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 2 && index < 29) {
            if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 29 && index < 38 && !this.moveItemStackTo(slotStack, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         }

         slot.setChanged();
         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
         this.broadcastChanges();
      }

      return itemstack;
   }

   @Override
   public boolean clickMenuButton(Player player, int id) {
      if (!this.level.isClientSide) {
         this.selectOutput(id);
         this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 0.7F, 1.0F);
         this.syncDataToClient();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void slotsChanged(Container container) {
      if (container == this.inputContainer && !this.level.isClientSide) {
         this.updateAvailableOutputs();
         this.syncDataToClient();
      }
      super.slotsChanged(container);
   }

   private void updateAvailableOutputs() {
      ItemStack input = this.inputContainer.getItem(0);
      this.availableOutputs.clear();
      if (input.isEmpty()) {
         this.outputContainer.setItem(0, ItemStack.EMPTY);
         this.selectedOutputIndex = -1;
      } else {
         List<ItemStack> recipes = SortingWorkbenchRecipeManager.getRecipesFor(input);
         this.availableOutputs.addAll(recipes);
         if (this.selectedOutputIndex >= 0 && this.selectedOutputIndex < this.availableOutputs.size()) {
            ItemStack selectedOutput = this.availableOutputs.get(this.selectedOutputIndex).copy();
            selectedOutput.setCount(input.getCount());
            this.outputContainer.setItem(0, selectedOutput);
         } else if (!this.availableOutputs.isEmpty()) {
            this.selectedOutputIndex = 0;
            ItemStack selectedOutput = this.availableOutputs.get(0).copy();
            selectedOutput.setCount(input.getCount());
            this.outputContainer.setItem(0, selectedOutput);
         }
      }

      this.totalOutputRows = (int) Math.ceil((double) this.availableOutputs.size() / 6.0);
      this.scrollOffset = Math.min(this.scrollOffset, Math.max(0, this.totalOutputRows - 4));
   }

   private void syncDataToClient() {
      if (!this.level.isClientSide && this.player instanceof ServerPlayer serverPlayer) {
         serverPlayer.connection.send(
            new SortingWorkbenchSyncPacket(this.containerId, this.availableOutputs, this.selectedOutputIndex, this.scrollOffset, this.totalOutputRows));
      }
   }

   public void selectOutput(int index) {
      if (index >= 0 && index < this.availableOutputs.size()) {
         ItemStack input = this.inputContainer.getItem(0);
         if (!input.isEmpty()) {
            this.selectedOutputIndex = index;
            ItemStack selectedOutput = this.availableOutputs.get(index).copy();
            selectedOutput.setCount(input.getCount());
            this.outputContainer.setItem(0, selectedOutput);
         }
      }
   }

   public ItemStack getAvailableOutput(int index) {
      return index < this.availableOutputs.size() ? this.availableOutputs.get(index) : ItemStack.EMPTY;
   }

   public int getAvailableOutputsSize() {
      return this.availableOutputs.size();
   }

   public void updateFromPacket(List<ItemStack> outputs, int selectedIndex, int scroll, int totalRows) {
      this.availableOutputs.clear();
      this.availableOutputs.addAll(outputs);
      this.selectedOutputIndex = selectedIndex;
      this.scrollOffset = scroll;
      this.totalOutputRows = totalRows;
      ItemStack input = this.inputContainer.getItem(0);
      if (!input.isEmpty() && selectedIndex >= 0 && selectedIndex < this.availableOutputs.size()) {
         ItemStack selectedOutput = this.availableOutputs.get(selectedIndex).copy();
         selectedOutput.setCount(input.getCount());
         this.outputContainer.setItem(0, selectedOutput);
      } else {
         this.outputContainer.setItem(0, ItemStack.EMPTY);
      }
   }

   public void handleScrollFromClient(float scroll) {
      if (!this.level.isClientSide) {
         int maxScroll = Math.max(0, this.totalOutputRows - 4);
         int newScrollOffset = (int) (scroll * (float) maxScroll + 0.5F);
         newScrollOffset = Math.max(0, Math.min(maxScroll, newScrollOffset));
         if (newScrollOffset != this.scrollOffset) {
            this.scrollOffset = newScrollOffset;
            this.syncDataToClient();
         }
      }
   }

   public void scrollTo(float scroll) {
      if (this.level.isClientSide) {
         net.minecraft.client.Minecraft.getInstance().getConnection().send(new SortingWorkbenchScrollPacket(this.containerId, scroll));
      } else {
         this.handleScrollFromClient(scroll);
      }
   }

   public boolean canScroll() {
      return this.totalOutputRows > 4;
   }

   public int getTotalOutputRows() {
      return this.totalOutputRows;
   }

   public int getScrollOffset() {
      return this.scrollOffset;
   }

   public int getVisibleOutputCount() {
      int startIndex = this.scrollOffset * 6;
      int remaining = this.availableOutputs.size() - startIndex;
      return Math.min(remaining, 24);
   }

   public int getSelectedOutputIndex() {
      return this.selectedOutputIndex;
   }

   @Override
   public boolean stillValid(Player player) {
      return true;
   }

   @Override
   public void removed(Player player) {
      super.removed(player);
      if (!this.level.isClientSide) {
         ItemStack inputStack = this.inputContainer.removeItemNoUpdate(0);
         if (!inputStack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(inputStack);
         }
         this.outputContainer.removeItemNoUpdate(0);
      }
   }
}
