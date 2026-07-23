package materials_integration;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;

public class SortingWorkbenchScreen extends AbstractContainerScreen<SortingWorkbenchMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("materials_integration", "textures/gui/sorting_workbench.png");
   private static final int SCROLLBAR_X = 138;
   private static final int SCROLLBAR_Y = 15;
   private static final int SCROLLBAR_WIDTH = 12;
   private static final int SCROLLBAR_HEIGHT = 72;
   private static final int SCROLLBAR_SLIDER_HEIGHT = 15;
   private static final int OUTPUT_AREA_X = 26;
   private static final int OUTPUT_AREA_Y = 15;
   private static final int OUTPUT_AREA_WIDTH = 108;
   private static final int OUTPUT_AREA_HEIGHT = 72;
   private float scrollOff;
   private boolean scrolling;
   private ItemStack hoveredOutput = ItemStack.EMPTY;

   public SortingWorkbenchScreen(SortingWorkbenchMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageWidth = 176;
      this.imageHeight = 181;
      this.inventoryLabelY = 88;
   }

   @Override
   protected void init() {
      super.init();
      this.scrollOff = 0.0F;
   }

   @Override
   protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.drawOutputSlotBackgrounds(guiGraphics);
      if (this.menu.canScroll()) {
         int scrollY = (int) (57.0F * this.scrollOff);
         guiGraphics.blit(TEXTURE, this.leftPos + 138, this.topPos + 15 + scrollY, 176, 0, 12, 15);
      }
   }

   private void drawOutputSlotBackgrounds(GuiGraphics guiGraphics) {
      int visibleOutputCount = this.menu.getVisibleOutputCount();
      int scrollOffset = this.menu.getScrollOffset();

      for (int i = 0; i < visibleOutputCount; i++) {
         int row = i / 6;
         int col = i % 6;
         int x = this.leftPos + 26 + col * 18;
         int y = this.topPos + 15 + row * 18;
         guiGraphics.blit(TEXTURE, x, y, 176, 18, 18, 18);
      }
   }

   @Override
   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      this.renderOutputItems(guiGraphics, mouseX, mouseY);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   private void renderOutputItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      int visibleOutputCount = this.menu.getVisibleOutputCount();
      int scrollOffset = this.menu.getScrollOffset();
      int startIndex = scrollOffset * 6;
      this.hoveredOutput = ItemStack.EMPTY;

      for (int i = 0; i < visibleOutputCount; i++) {
         int absoluteIndex = startIndex + i;
         if (absoluteIndex < this.menu.getAvailableOutputsSize()) {
            int row = i / 6;
            int col = i % 6;
            int x = this.leftPos + 26 + col * 18 + 1;
            int y = this.topPos + 15 + row * 18 + 1;
            ItemStack output = this.menu.getAvailableOutput(absoluteIndex);
            if (!output.isEmpty()) {
               guiGraphics.renderItem(output, x, y);
               guiGraphics.renderItemDecorations(this.font, output, x, y);
               if (absoluteIndex == this.menu.getSelectedOutputIndex()) {
                  guiGraphics.blit(TEXTURE, x - 1, y - 1, 176, 36, 18, 18);
               }

               if (this.isMouseOverOutputSlot(mouseX, mouseY, col, row)) {
                  this.hoveredOutput = output;
               }
            }
         }
      }
   }

   @Override
   protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      super.renderTooltip(guiGraphics, mouseX, mouseY);
      if (!this.hoveredOutput.isEmpty()) {
         List<Component> tooltip = this.getTooltipFromItem(this.hoveredOutput);
         guiGraphics.renderTooltip(this.font, tooltip, this.hoveredOutput.getTooltipImage(), this.hoveredOutput, mouseX, mouseY);
      }
   }

   private List<Component> getTooltipFromItem(ItemStack stack) {
      return stack.getTooltipLines(TooltipContext.of(this.minecraft != null ? this.minecraft.level : null), this.minecraft != null ? this.minecraft.player : null, TooltipFlag.Default.NORMAL);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.scrolling = false;
      if (this.menu.canScroll() && this.isMouseOverScrollbar(mouseX, mouseY)) {
         this.scrolling = true;
         return true;
      } else if (this.isMouseInOutputArea(mouseX, mouseY)) {
         this.handleOutputClick(mouseX, mouseY);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   private void handleOutputClick(double mouseX, double mouseY) {
      int visibleOutputCount = this.menu.getVisibleOutputCount();
      int scrollOffset = this.menu.getScrollOffset();
      int startIndex = scrollOffset * 6;

      for (int i = 0; i < visibleOutputCount; i++) {
         int row = i / 6;
         int col = i % 6;
         if (this.isMouseOverOutputSlot(mouseX, mouseY, col, row)) {
            int absoluteIndex = startIndex + i;
            if (absoluteIndex < this.menu.getAvailableOutputsSize()) {
               this.menu.selectOutput(absoluteIndex);
               assert this.minecraft != null;
               assert this.minecraft.gameMode != null;
               this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, absoluteIndex);
            }
            return;
         }
      }
   }

   private boolean isMouseOverOutputSlot(double mouseX, double mouseY, int col, int row) {
      int x = this.leftPos + 26 + col * 18;
      int y = this.topPos + 15 + row * 18;
      return mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.menu.canScroll() && this.isMouseInOutputArea(mouseX, mouseY)) {
         float newScrollOff = this.scrollOff - (float) scrollY / 4.0F;
         this.scrollOff = Math.max(0.0F, Math.min(1.0F, newScrollOff));
         this.menu.scrollTo(this.scrollOff);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.scrolling && this.menu.canScroll()) {
         int scrollAreaTop = this.topPos + 15;
         int scrollAreaBottom = scrollAreaTop + 72 - 15;
         float relativeY = ((float) mouseY - (float) scrollAreaTop) / (float) (scrollAreaBottom - scrollAreaTop);
         this.scrollOff = Math.max(0.0F, Math.min(1.0F, relativeY));
         this.menu.scrollTo(this.scrollOff);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   public void updateScrollFromServer(int scrollOffset, int totalOutputRows) {
      int maxScroll = Math.max(0, totalOutputRows - 4);
      if (maxScroll > 0) {
         this.scrollOff = (float) scrollOffset / (float) maxScroll;
      } else {
         this.scrollOff = 0.0F;
      }
   }

   public void updateFromPacket(List<ItemStack> availableOutputs, int selectedOutputIndex, int scrollOffset, int totalOutputRows) {
      this.updateScrollFromServer(scrollOffset, totalOutputRows);
      this.init();
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.scrolling) {
         this.scrolling = false;
         return true;
      } else {
         return super.mouseReleased(mouseX, mouseY, button);
      }
   }

   private boolean isMouseInOutputArea(double mouseX, double mouseY) {
      return mouseX >= (double) (this.leftPos + 26)
         && mouseX <= (double) (this.leftPos + 26 + 108)
         && mouseY >= (double) (this.topPos + 15)
         && mouseY <= (double) (this.topPos + 15 + 72);
   }

   private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
      return mouseX >= (double) (this.leftPos + 138)
         && mouseX <= (double) (this.leftPos + 138 + 12)
         && mouseY >= (double) (this.topPos + 15)
         && mouseY <= (double) (this.topPos + 15 + 72);
   }
}
