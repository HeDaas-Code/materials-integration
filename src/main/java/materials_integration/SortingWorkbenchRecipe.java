package materials_integration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class SortingWorkbenchRecipe implements Recipe<Container> {
   private final ResourceLocation id;
   private final Item input;
   private final TagKey<Item> outputTag;

   public SortingWorkbenchRecipe(ResourceLocation id, Item input, TagKey<Item> outputTag) {
      this.id = id;
      this.input = input;
      this.outputTag = outputTag;
   }

   @Override
   public boolean matches(Container container, Level level) {
      ItemStack inputStack = container.getItem(0);
      return !inputStack.isEmpty() && inputStack.getItem() == this.input;
   }

   @Override
   public ItemStack assemble(Container container, HolderLookup.Provider registryAccess) {
      return ItemStack.EMPTY;
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
      return ItemStack.EMPTY;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return ModRecipeSerializers.SORTING_WORKBENCH.value();
   }

   @Override
   public RecipeType<?> getType() {
      return ModRecipeTypes.SORTING_WORKBENCH.value();
   }

   public Item getInput() {
      return this.input;
   }

   public TagKey<Item> getOutputTag() {
      return this.outputTag;
   }

   public List<ItemStack> getOutputs() {
      List<ItemStack> outputs = new ArrayList<>();
      BuiltInRegistries.ITEM.getTag(this.outputTag).ifPresent(namedTag -> {
         for (Holder<Item> holder : namedTag) {
            outputs.add(new ItemStack(holder.value()));
         }
      });
      return outputs;
   }

   public static class Serializer implements RecipeSerializer<SortingWorkbenchRecipe> {
      private static final MapCodec<SortingWorkbenchRecipe> CODEC = RecordCodecBuilder.create(instance ->
         instance.group(
            com.mojang.serialization.Codec.STRING.fieldOf("input").forGetter(r -> BuiltInRegistries.ITEM.getKey(r.getInput()).toString()),
            com.mojang.serialization.Codec.STRING.fieldOf("output_tag").forGetter(r -> r.getOutputTag().location().toString())
         ).apply(instance, (inputStr, outputTagStr) -> {
            ResourceLocation inputId = ResourceLocation.parse(inputStr);
            Item inputItem = BuiltInRegistries.ITEM.get(inputId);
            ResourceLocation outputTagId = ResourceLocation.parse(outputTagStr);
            TagKey<Item> outputTag = TagKey.create(Registries.ITEM, outputTagId);
            return new SortingWorkbenchRecipe(ResourceLocation.parse(inputStr), inputItem, outputTag);
         })
      );

      private static final StreamCodec<RegistryFriendlyByteBuf, SortingWorkbenchRecipe> STREAM_CODEC =
         StreamCodec.ofMember(
            (recipe, buf) -> {
               buf.writeUtf(BuiltInRegistries.ITEM.getKey(recipe.getInput()).toString());
               buf.writeUtf(recipe.getOutputTag().location().toString());
            },
            buf -> {
               ResourceLocation inputId = ResourceLocation.parse(buf.readUtf());
               ResourceLocation outputTagId = ResourceLocation.parse(buf.readUtf());
               Item inputItem = BuiltInRegistries.ITEM.get(inputId);
               TagKey<Item> outputTag = TagKey.create(Registries.ITEM, outputTagId);
               return new SortingWorkbenchRecipe(inputId, inputItem, outputTag);
            }
         );

      @Override
      public MapCodec<SortingWorkbenchRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, SortingWorkbenchRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
