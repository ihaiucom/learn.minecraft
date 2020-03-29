package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BrewingStandBlock extends ContainerBlock {
   public static final BooleanProperty[] HAS_BOTTLE;
   protected static final VoxelShape SHAPE;

   public BrewingStandBlock(Block.Properties p_i48438_1_) {
      super(p_i48438_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HAS_BOTTLE[0], false)).with(HAS_BOTTLE[1], false)).with(HAS_BOTTLE[2], false));
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BrewingStandTileEntity();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_7_1_ instanceof BrewingStandTileEntity) {
            p_225533_4_.openContainer((BrewingStandTileEntity)lvt_7_1_);
            p_225533_4_.addStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof BrewingStandTileEntity) {
            ((BrewingStandTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      double lvt_5_1_ = (double)p_180655_3_.getX() + 0.4D + (double)p_180655_4_.nextFloat() * 0.2D;
      double lvt_7_1_ = (double)p_180655_3_.getY() + 0.7D + (double)p_180655_4_.nextFloat() * 0.3D;
      double lvt_9_1_ = (double)p_180655_3_.getZ() + 0.4D + (double)p_180655_4_.nextFloat() * 0.2D;
      p_180655_2_.addParticle(ParticleTypes.SMOKE, lvt_5_1_, lvt_7_1_, lvt_9_1_, 0.0D, 0.0D, 0.0D);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity lvt_6_1_ = p_196243_2_.getTileEntity(p_196243_3_);
         if (lvt_6_1_ instanceof BrewingStandTileEntity) {
            InventoryHelper.dropInventoryItems(p_196243_2_, (BlockPos)p_196243_3_, (BrewingStandTileEntity)lvt_6_1_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
      SHAPE = VoxelShapes.or(Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));
   }
}
