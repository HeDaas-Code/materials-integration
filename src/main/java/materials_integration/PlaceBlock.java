package materials_integration;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber(modid = "materials_integration")
public class PlaceBlock {
   private static final Map<String, Block> DEFAULT_BLOCKS = new HashMap<>();

   private static boolean isInteractionBlock(BlockState state) {
      Block block = state.getBlock();
      return state.is(BlockTags.DOORS)
         || state.is(BlockTags.BUTTONS)
         || state.is(BlockTags.ANVIL)
         || state.is(BlockTags.FENCE_GATES)
         || state.is(BlockTags.TRAPDOORS)
         || state.hasAnalogOutputSignal()
         || block instanceof MenuProvider
         || isWorkbenchBlock(block)
         || isModWorkbenchBlock(block);
   }

   private static boolean isWorkbenchBlock(Block block) {
      ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
      if (blockId == null) {
         return false;
      }
      String path = blockId.getPath();
      return path.contains("crafting_table")
         || path.contains("furnace")
         || path.contains("blast_furnace")
         || path.contains("smoker")
         || path.contains("stonecutter")
         || path.contains("loom")
         || path.contains("cartography_table")
         || path.contains("smithing_table")
         || path.contains("grindstone")
         || path.contains("anvil")
         || path.contains("enchanting_table")
         || path.contains("brewing_stand");
   }

   private static boolean isModWorkbenchBlock(Block block) {
      ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
      if (blockId == null) {
         return false;
      }
      String path = blockId.getPath();
      return path.contains("workbench")
         || path.contains("crafting")
         || path.contains("table") && (path.contains("craft") || path.contains("work"))
         || isBlockInWorkbenchTag(block);
   }

   private static boolean isBlockInWorkbenchTag(Block block) {
      return false;
   }

   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      Player player = event.getEntity();
      Level level = event.getLevel();
      if (event.getHand() == InteractionHand.MAIN_HAND) {
         ItemStack heldStack = player.getMainHandItem();
         if (isIntegrationItem(heldStack)) {
            if (level.isClientSide()) {
               player.swing(InteractionHand.MAIN_HAND);
            } else {
               BlockPos pos = event.getPos();
               BlockState targetState = level.getBlockState(pos);

               if (!isInteractionBlock(targetState)) {
                  ResourceLocation heldItemId = BuiltInRegistries.ITEM.getKey(heldStack.getItem());
                  String itemPath = heldItemId != null ? heldItemId.getPath() : null;
                  if (itemPath != null) {
                     Item targetItem = getBlockItem(targetState.getBlock());
                     if (targetItem != null) {
                        BlockState stateToPlace = null;
                        ResourceLocation tagLocation = ResourceLocation.fromNamespaceAndPath("materials_integration", itemPath);
                        TagKey<Item> outputTag = ItemTags.create(tagLocation);
                        if (isItemInTag(targetItem, outputTag)) {
                           stateToPlace = targetState;
                        } else if (DEFAULT_BLOCKS.containsKey(itemPath)) {
                           Block defaultBlock = DEFAULT_BLOCKS.get(itemPath);
                           if (defaultBlock != null) {
                              stateToPlace = defaultBlock.defaultBlockState();
                           }
                        }

                        if (stateToPlace != null) {
                           Direction clickedFace = event.getFace();
                           if (clickedFace != null) {
                              BlockPos placePos = pos.relative(clickedFace);
                              if (level.isAir(placePos)) {
                                 BlockItem blockItem = createBlockItem(stateToPlace);
                                 if (blockItem != null) {
                                    ItemStack tempStack = new ItemStack(blockItem);
                                    Vec3 clickLocation = event.getHitVec().getLocation();
                                    BlockHitResult blockHit = new BlockHitResult(clickLocation, clickedFace, placePos, false);
                                    UseOnContext useContext = new UseOnContext(level, player, event.getHand(), tempStack, blockHit);
                                    BlockPlaceContext placeContext = new BlockPlaceContext(useContext);
                                    InteractionResult result = blockItem.place(placeContext);
                                    if (result.consumesAction()) {
                                       SoundEvent sound = stateToPlace.getSoundType().getPlaceSound();
                                       level.playSound(null, placePos, sound, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                                       if (!player.isCreative()) {
                                          heldStack.shrink(1);
                                       }
                                       player.swing(InteractionHand.MAIN_HAND);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isItemInTag(Item item, TagKey<Item> tag) {
      return BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getKey(item))
         .map(holder -> holder.is(tag))
         .orElse(false);
   }

   private static Item getBlockItem(Block block) {
      return BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(block));
   }

   private static BlockItem createBlockItem(BlockState state) {
      Block block = state.getBlock();
      Item item = BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(block));
      return item instanceof BlockItem ? (BlockItem) item : null;
   }

   private static boolean isIntegrationItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
      return itemId != null && itemId.getNamespace().equals("materials_integration");
   }

   static {
      DEFAULT_BLOCKS.put("log_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:oak_log")));
      DEFAULT_BLOCKS.put("dirt_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:dirt")));
      DEFAULT_BLOCKS.put("planks_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:oak_planks")));
      DEFAULT_BLOCKS.put("rock_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:stone_bricks")));
      DEFAULT_BLOCKS.put("stone_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:cobblestone")));
      DEFAULT_BLOCKS.put("sand_integration", BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:sand")));
   }
}
