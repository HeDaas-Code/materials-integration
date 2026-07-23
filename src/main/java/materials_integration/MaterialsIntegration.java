package materials_integration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod("materials_integration")
public class MaterialsIntegration {
   public static final String MODID = "materials_integration";
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "materials_integration");
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "materials_integration");
   public static final String DIRT_DROP = "dirt_drop";
   public static final String LOG_DROP = "log_drop";
   public static final String PLANKS_DROP = "planks_drop";
   public static final String STONE_DROP = "stone_drop";
   public static final String SAND_DROP = "sand_drop";
   public static final String ROCK_DROP = "rock_drop";
   public static final String DROP_TWO = "two";
   public static final String DROP_FOUR = "four";
   public static final String DROP_CHANCE = "chance";
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "materials_integration");
   public static final DeferredHolder<Item, Item> DIRT_INTEGRATION = ITEMS.register("dirt_integration", () -> new Tooltip("tooltip.materials_integration.dirt"));
   public static final DeferredHolder<Item, Item> LOG_INTEGRATION = ITEMS.register("log_integration", () -> new Tooltip("tooltip.materials_integration.log"));
   public static final DeferredHolder<Item, Item> PLANKS_INTEGRATION = ITEMS.register("planks_integration", () -> new Tooltip("tooltip.materials_integration.planks"));
   public static final DeferredHolder<Item, Item> ROCK_INTEGRATION = ITEMS.register("rock_integration", () -> new Tooltip("tooltip.materials_integration.rock"));
   public static final DeferredHolder<Item, Item> SAND_INTEGRATION = ITEMS.register("sand_integration", () -> new Tooltip("tooltip.materials_integration.sand"));
   public static final DeferredHolder<Item, Item> STONE_INTEGRATION = ITEMS.register("stone_integration", () -> new Tooltip("tooltip.materials_integration.stone"));
   public static final DeferredHolder<Item, Item> FUEL_INTEGRATION = ITEMS.register("fuel_integration", () -> new Tooltip("tooltip.materials_integration.fuel") {
         @Override
         public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 1600;
         }
      });
   public static final DeferredHolder<Item, Item> WASTE = ITEMS.register("waste", () -> new Tooltip("tooltip.materials_integration.waste") {
         @Override
         public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
            return 120;
         }
      });
   public static final DeferredHolder<Block, Block> SORTING_WORKBENCH = BLOCKS.register(
      "sorting_workbench", () -> new SortingWorkbenchBlock(Properties.of().strength(2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredHolder<Item, Item> SORTING_WORKBENCH_ITEM = ITEMS.register(
      "sorting_workbench", () -> new BlockItem(SORTING_WORKBENCH.value(), new Item.Properties())
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RESOURCE_INTEGRATION = CREATIVE_MODE_TABS.register(
      "resource_integration",
      () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.materials_integration.resource_integration"))
            .icon(() -> LOG_INTEGRATION.value().getDefaultInstance())
            .displayItems((parameters, output) -> {
               output.accept(LOG_INTEGRATION.value());
               output.accept(DIRT_INTEGRATION.value());
               output.accept(PLANKS_INTEGRATION.value());
               output.accept(ROCK_INTEGRATION.value());
               output.accept(SAND_INTEGRATION.value());
               output.accept(STONE_INTEGRATION.value());
               output.accept(FUEL_INTEGRATION.value());
               output.accept(WASTE.value());
               output.accept(SORTING_WORKBENCH_ITEM.value());
            })
            .build()
   );

   public MaterialsIntegration(IEventBus modEventBus) {
      System.out.println("MaterialsIntegration MOD initialization started");
      Networking.register(modEventBus);
      BLOCKS.register(modEventBus);
      ITEMS.register(modEventBus);
      CREATIVE_MODE_TABS.register(modEventBus);
      ModMenuTypes.MENUS.register(modEventBus);
      ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
      ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
      modEventBus.addListener(this::commonSetup);
      System.out.println("MaterialsIntegration MOD initialization completed");
   }

   private void commonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         ComposterBlock.COMPOSTABLES.put(WASTE.value().asItem(), 0.3F);
         IntegrationRecipeManager.initialize();
      });
   }
}
