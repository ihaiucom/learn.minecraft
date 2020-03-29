package net.minecraft.block;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock extends ContainerBlock {
   public static final BooleanProperty HAS_RECORD;

   protected JukeboxBlock(Block.Properties p_i48372_1_) {
      super(p_i48372_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HAS_RECORD, false));
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if ((Boolean)p_225533_1_.get(HAS_RECORD)) {
         this.dropRecord(p_225533_2_, p_225533_3_);
         p_225533_1_ = (BlockState)p_225533_1_.with(HAS_RECORD, false);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 2);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public void insertRecord(IWorld p_176431_1_, BlockPos p_176431_2_, BlockState p_176431_3_, ItemStack p_176431_4_) {
      TileEntity lvt_5_1_ = p_176431_1_.getTileEntity(p_176431_2_);
      if (lvt_5_1_ instanceof JukeboxTileEntity) {
         ((JukeboxTileEntity)lvt_5_1_).setRecord(p_176431_4_.copy());
         p_176431_1_.setBlockState(p_176431_2_, (BlockState)p_176431_3_.with(HAS_RECORD, true), 2);
      }
   }

   private void dropRecord(World p_203419_1_, BlockPos p_203419_2_) {
      if (!p_203419_1_.isRemote) {
         TileEntity lvt_3_1_ = p_203419_1_.getTileEntity(p_203419_2_);
         if (lvt_3_1_ instanceof JukeboxTileEntity) {
            JukeboxTileEntity lvt_4_1_ = (JukeboxTileEntity)lvt_3_1_;
            ItemStack lvt_5_1_ = lvt_4_1_.getRecord();
            if (!lvt_5_1_.isEmpty()) {
               p_203419_1_.playEvent(1010, p_203419_2_, 0);
               lvt_4_1_.clear();
               float lvt_6_1_ = 0.7F;
               double lvt_7_1_ = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
               double lvt_9_1_ = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
               double lvt_11_1_ = (double)(p_203419_1_.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
               ItemStack lvt_13_1_ = lvt_5_1_.copy();
               ItemEntity lvt_14_1_ = new ItemEntity(p_203419_1_, (double)p_203419_2_.getX() + lvt_7_1_, (double)p_203419_2_.getY() + lvt_9_1_, (double)p_203419_2_.getZ() + lvt_11_1_, lvt_13_1_);
               lvt_14_1_.setDefaultPickupDelay();
               p_203419_1_.addEntity(lvt_14_1_);
            }
         }
      }
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         this.dropRecord(p_196243_2_, p_196243_3_);
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new JukeboxTileEntity();
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity lvt_4_1_ = p_180641_2_.getTileEntity(p_180641_3_);
      if (lvt_4_1_ instanceof JukeboxTileEntity) {
         Item lvt_5_1_ = ((JukeboxTileEntity)lvt_4_1_).getRecord().getItem();
         if (lvt_5_1_ instanceof MusicDiscItem) {
            return ((MusicDiscItem)lvt_5_1_).getComparatorValue();
         }
      }

      return 0;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HAS_RECORD);
   }

   static {
      HAS_RECORD = BlockStateProperties.HAS_RECORD;
   }
}
