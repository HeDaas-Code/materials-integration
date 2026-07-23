package materials_integration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TagFileReader {
   private static final Gson GSON = new Gson();
   private static final Map<String, Set<Block>> TAG_CACHE = new HashMap<>();

   public static void loadAllTags() {
      String[] tagNames = new String[]{
         "dirt_drop", "dirt_drop_two", "dirt_drop_four", "dirt_drop_chance",
         "log_drop", "log_drop_two", "log_drop_four", "log_drop_chance",
         "planks_drop", "planks_drop_two", "planks_drop_four", "planks_drop_chance",
         "stone_drop", "stone_drop_two", "stone_drop_four", "stone_drop_chance",
         "rock_drop", "rock_drop_two", "rock_drop_four", "rock_drop_chance",
         "sand_drop", "sand_drop_two", "sand_drop_four", "sand_drop_chance"
      };

      for (String tagName : tagNames) {
         loadTag(tagName);
      }
   }

   private static void loadTag(String tagName) {
          String path = String.format("/data/%s/tags/block/%s.json", "materials_integration", tagName);

      try {
         try (InputStream inputStream = TagFileReader.class.getResourceAsStream(path)) {
            if (inputStream != null) {
               JsonObject json = GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
               JsonArray values = json.getAsJsonArray("values");
               Set<Block> blocks = new HashSet<>();

               for (JsonElement element : values) {
                  if (element.isJsonPrimitive()) {
                     String blockId = element.getAsString();
                     addBlockFromId(blocks, blockId);
                  } else if (element.isJsonObject()) {
                     JsonObject obj = element.getAsJsonObject();
                     if (obj.has("id")) {
                        String blockId = obj.get("id").getAsString();
                        addBlockFromId(blocks, blockId);
                     }
                  }
               }

               TAG_CACHE.put(tagName, blocks);
            }
         }
      } catch (Exception e) {
      }
   }

   private static void addBlockFromId(Set<Block> blocks, String blockId) {
      try {
         ResourceLocation location = ResourceLocation.parse(blockId);
         Block block = BuiltInRegistries.BLOCK.get(location);
         if (block != null) {
            blocks.add(block);
         }
      } catch (Exception e) {
      }
   }

   public static boolean isBlockInTag(Block block, String tagName) {
      Set<Block> blocks = TAG_CACHE.get(tagName);
      return blocks != null && blocks.contains(block);
   }
}
