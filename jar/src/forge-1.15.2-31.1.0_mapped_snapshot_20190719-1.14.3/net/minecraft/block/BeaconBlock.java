package net.minecraft.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BeaconBlock extends ContainerBlock implements IBeaconBeamColorProvider {
   public BeaconBlock(Block.Properties p_i48443_1_) {
      super(p_i48443_1_);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BeaconTileEntity();
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_7_1_ instanceof BeaconTileEntity) {
            p_225533_4_.openContainer((BeaconTileEntity)lvt_7_1_);
            p_225533_4_.addStat(Stats.INTERACT_WITH_BEACON);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return false;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof BeaconTileEntity) {
            ((BeaconTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }
}
