package materials_integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SortingWorkbenchBlock extends HorizontalDirectionalBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape SHAPE_NORTH = Shapes.or(
      Block.box(0, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 11, 16)
   );
   private static final VoxelShape SHAPE_SOUTH = Shapes.or(
      Block.box(-4, 0, 0, 16, 16, 16), Block.box(-16, 0, 3, -4, 11, 15)
   );
   private static final VoxelShape SHAPE_EAST = Shapes.or(
      Block.box(0, 0, 0, 16, 16, 20), Block.box(3, 0, 20, 15, 11, 32)
   );
   private static final VoxelShape SHAPE_WEST = Shapes.or(
      Block.box(0, 0, -4, 16, 16, 16), Block.box(1, 0, -16, 13, 11, -4)
   );

   public SortingWorkbenchBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch (state.getValue(FACING)) {
         case SOUTH -> SHAPE_SOUTH;
         case EAST -> SHAPE_EAST;
         case WEST -> SHAPE_WEST;
         default -> SHAPE_NORTH;
      };
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
      return this.getShape(state, level, pos, CollisionContext.empty());
   }

   @Override
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.getShape(state, level, pos, context);
   }

   @Override
   public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
      return this.getShape(state, level, pos, CollisionContext.empty());
   }

   @Override
   protected MapCodec<? extends SortingWorkbenchBlock> codec() {
      return MapCodec.unit(this);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         player.openMenu(this.getMenuProvider(state, level, pos));
         return InteractionResult.CONSUME;
      }
   }

    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
      return new SimpleMenuProvider(
         (containerId, playerInventory, player) -> new SortingWorkbenchMenu(containerId, playerInventory),
         Component.translatable("container.materials_integration.sorting_workbench")
      );
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   @Override
   public boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return true;
   }
}
