package materials_integration;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = "materials_integration", bus = Bus.GAME)
public class LootTableEventHandler {
   private static boolean tagsLoaded = false;

   @SubscribeEvent
   public static void onLootTableLoad(LootTableLoadEvent event) {
      if (!tagsLoaded) {
         TagFileReader.loadAllTags();
         tagsLoaded = true;
      }

      ResourceLocation tableName = event.getName();
      if (tableName.getPath().startsWith("blocks/")) {
         String blockPath = tableName.getPath().substring(7);
         ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(tableName.getNamespace(), blockPath);
         Block block = BuiltInRegistries.BLOCK.get(blockId);
         if (block != null) {
            String dropType = getDropTypeForBlock(block);
            if (dropType != null) {
               replaceLootTable(event, block, dropType);
            }
         }
      }
   }

   private static String getDropTypeForBlock(Block block) {
      String[] tagTypes = new String[]{
         "log_drop_four", "log_drop_two", "log_drop_chance", "log_drop",
         "planks_drop_four", "planks_drop_two", "planks_drop_chance", "planks_drop",
         "stone_drop_four", "stone_drop_two", "stone_drop_chance", "stone_drop",
         "rock_drop_four", "rock_drop_two", "rock_drop_chance", "rock_drop",
         "dirt_drop_four", "dirt_drop_two", "dirt_drop_chance", "dirt_drop",
         "sand_drop_four", "sand_drop_two", "sand_drop_chance", "sand_drop"
      };

      for (String tagName : tagTypes) {
         if (TagFileReader.isBlockInTag(block, tagName)) {
            String[] parts = tagName.split("_");
            String resourceType = parts[0];
            String dropVariant = parts.length > 2 ? parts[2] : "normal";
            return resourceType + "_" + dropVariant;
         }
      }
      return null;
   }

    private static LootItemCondition.Builder silkTouchCondition() {
       MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
       if (server == null) {
          return MatchTool.toolMatches(
             net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
          );
       }
       HolderLookup.RegistryLookup<Enchantment> enchLookup = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
       net.minecraft.core.Holder<Enchantment> silkTouch = enchLookup.getOrThrow(Enchantments.SILK_TOUCH);
       return MatchTool.toolMatches(
          net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
             .withSubPredicate(
                ItemSubPredicates.ENCHANTMENTS,
                ItemEnchantmentsPredicate.enchantments(
                   List.of(new EnchantmentPredicate(silkTouch, Ints.atLeast(1)))
                )
             )
       );
    }

   private static void replaceLootTable(LootTableLoadEvent event, Block block, String dropType) {
      String[] parts = dropType.split("_");
      String resourceType = parts[0];
      String dropVariant = parts.length > 1 ? parts[1] : "normal";
      ResourceLocation integrationItem = getIntegrationItem(resourceType);
      if (integrationItem != null) {
         LootPool.Builder poolBuilder = LootPool.lootPool()
            .name("materials_integration_pool")
            .when(ExplosionCondition.survivesExplosion());

         Item integrationItemObj = BuiltInRegistries.ITEM.get(integrationItem);

         switch (dropVariant) {
            case "four":
               poolBuilder.add(
                  LootItem.lootTableItem(Objects.requireNonNull(integrationItemObj))
                     .when(silkTouchCondition())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
               );
               break;
            case "two":
               poolBuilder.add(
                  LootItem.lootTableItem(Objects.requireNonNull(integrationItemObj))
                     .when(silkTouchCondition())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
               );
               break;
            case "chance":
               poolBuilder.add(
                  LootItem.lootTableItem(Objects.requireNonNull(integrationItemObj))
                     .when(silkTouchCondition())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                     .setWeight(1)
               );
               poolBuilder.add(
                  LootItem.lootTableItem(integrationItemObj)
                     .when(silkTouchCondition())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(0.0F)))
                     .setWeight(1)
               );
               break;
            case "normal":
            default:
               poolBuilder.add(
                  LootItem.lootTableItem(Objects.requireNonNull(integrationItemObj))
                     .when(silkTouchCondition())
                     .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
               );
         }

         poolBuilder.add(
            LootItem.lootTableItem(block.asItem())
               .when(silkTouchCondition())
               .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
         );

         LootTable newTable = LootTable.lootTable().withPool(poolBuilder).build();
         event.setTable(newTable);
      }
   }

   private static ResourceLocation getIntegrationItem(String resourceType) {
      return switch (resourceType) {
         case "log" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.LOG_INTEGRATION.value());
         case "planks" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.PLANKS_INTEGRATION.value());
         case "stone" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.STONE_INTEGRATION.value());
         case "rock" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.ROCK_INTEGRATION.value());
         case "dirt" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.DIRT_INTEGRATION.value());
         case "sand" -> BuiltInRegistries.ITEM.getKey(MaterialsIntegration.SAND_INTEGRATION.value());
         default -> null;
      };
   }
}
