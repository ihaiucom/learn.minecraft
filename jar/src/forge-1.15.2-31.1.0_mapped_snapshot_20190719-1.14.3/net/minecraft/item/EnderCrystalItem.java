package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;

public class EnderCrystalItem extends Item {
   public EnderCrystalItem(Item.Properties p_i48503_1_) {
      super(p_i48503_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      if (lvt_4_1_.getBlock() != Blocks.OBSIDIAN && lvt_4_1_.getBlock() != Blocks.BEDROCK) {
         return ActionResultType.FAIL;
      } else {
         BlockPos lvt_5_1_ = lvt_3_1_.up();
         if (!lvt_2_1_.isAirBlock(lvt_5_1_)) {
            return ActionResultType.FAIL;
         } else {
            double lvt_6_1_ = (double)lvt_5_1_.getX();
            double lvt_8_1_ = (double)lvt_5_1_.getY();
            double lvt_10_1_ = (double)lvt_5_1_.getZ();
            List<Entity> lvt_12_1_ = lvt_2_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_6_1_ + 1.0D, lvt_8_1_ + 2.0D, lvt_10_1_ + 1.0D));
            if (!lvt_12_1_.isEmpty()) {
               return ActionResultType.FAIL;
            } else {
               if (!lvt_2_1_.isRemote) {
                  EnderCrystalEntity lvt_13_1_ = new EnderCrystalEntity(lvt_2_1_, lvt_6_1_ + 0.5D, lvt_8_1_, lvt_10_1_ + 0.5D);
                  lvt_13_1_.setShowBottom(false);
                  lvt_2_1_.addEntity(lvt_13_1_);
                  if (lvt_2_1_.dimension instanceof EndDimension) {
                     DragonFightManager lvt_14_1_ = ((EndDimension)lvt_2_1_.dimension).getDragonFightManager();
                     lvt_14_1_.tryRespawnDragon();
                  }
               }

               p_195939_1_.getItem().shrink(1);
               return ActionResultType.SUCCESS;
            }
         }
      }
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }
}
