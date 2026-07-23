package materials_integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IntegrationRecipeManager {
   private static final Map<String, List<ItemStack>> TAG_TO_OUTPUTS = new HashMap<>();
   private static final Map<Item, String> ITEM_TO_TAG = new HashMap<>();

   public static void initialize() {
      initializeTagMapping("dirt_drop", MaterialsIntegration.DIRT_INTEGRATION.value());
      initializeTagMapping("log_drop", MaterialsIntegration.LOG_INTEGRATION.value());
      initializeTagMapping("planks_drop", MaterialsIntegration.PLANKS_INTEGRATION.value());
      initializeTagMapping("stone_drop", MaterialsIntegration.STONE_INTEGRATION.value());
      initializeTagMapping("sand_drop", MaterialsIntegration.SAND_INTEGRATION.value());
      initializeTagMapping("rock_drop", MaterialsIntegration.ROCK_INTEGRATION.value());
      buildReverseMapping();
   }

   private static void initializeTagMapping(String tagName, Item integrationItem) {
      TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("materials_integration", tagName));
      List<ItemStack> outputs = new ArrayList<>();
      outputs.add(new ItemStack(integrationItem));
      TAG_TO_OUTPUTS.put(tagName, outputs);
   }

   private static void buildReverseMapping() {
      for (String tagName : TAG_TO_OUTPUTS.keySet()) {
         TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("materials_integration", tagName));
         BuiltInRegistries.ITEM.forEach(item -> {
            BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getKey(item))
               .ifPresent(holder -> {
                  if (holder.is(tag)) {
                     ITEM_TO_TAG.put(item, tagName);
                  }
               });
         });
      }
   }

   public static boolean hasRecipeFor(ItemStack input) {
      return ITEM_TO_TAG.containsKey(input.getItem());
   }

   public static List<ItemStack> getRecipesFor(ItemStack input) {
      String tagName = ITEM_TO_TAG.get(input.getItem());
      return tagName != null ? new ArrayList<>(TAG_TO_OUTPUTS.get(tagName)) : Collections.emptyList();
   }
}
