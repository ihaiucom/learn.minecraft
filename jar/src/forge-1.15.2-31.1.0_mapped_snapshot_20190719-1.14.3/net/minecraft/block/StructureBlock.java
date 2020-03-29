package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StructureBlock extends ContainerBlock {
   public static final EnumProperty<StructureMode> MODE;

   protected StructureBlock(Block.Properties p_i48314_1_) {
      super(p_i48314_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new StructureBlockTileEntity();
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
      if (lvt_7_1_ instanceof StructureBlockTileEntity) {
         return ((StructureBlockTileEntity)lvt_7_1_).usedBy(p_225533_4_) ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isRemote) {
         if (p_180633_4_ != null) {
            TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
            if (lvt_6_1_ instanceof StructureBlockTileEntity) {
               ((StructureBlockTileEntity)lvt_6_1_).createdBy(p_180633_4_);
            }
         }

      }
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(MODE, StructureMode.DATA);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(MODE);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         TileEntity lvt_7_1_ = p_220069_2_.getTileEntity(p_220069_3_);
         if (lvt_7_1_ instanceof StructureBlockTileEntity) {
            StructureBlockTileEntity lvt_8_1_ = (StructureBlockTileEntity)lvt_7_1_;
            boolean lvt_9_1_ = p_220069_2_.isBlockPowered(p_220069_3_);
            boolean lvt_10_1_ = lvt_8_1_.isPowered();
            if (lvt_9_1_ && !lvt_10_1_) {
               lvt_8_1_.setPowered(true);
               this.trigger(lvt_8_1_);
            } else if (!lvt_9_1_ && lvt_10_1_) {
               lvt_8_1_.setPowered(false);
            }

         }
      }
   }

   private void trigger(StructureBlockTileEntity p_189874_1_) {
      switch(p_189874_1_.getMode()) {
      case SAVE:
         p_189874_1_.save(false);
         break;
      case LOAD:
         p_189874_1_.load(false);
         break;
      case CORNER:
         p_189874_1_.unloadStructure();
      case DATA:
      }

   }

   static {
      MODE = BlockStateProperties.STRUCTURE_BLOCK_MODE;
   }
}
