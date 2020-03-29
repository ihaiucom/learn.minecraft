package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties p_i48532_1_) {
      super(p_i48532_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      Direction lvt_2_1_ = p_195939_1_.getFace();
      if (lvt_2_1_ == Direction.DOWN) {
         return ActionResultType.FAIL;
      } else {
         World lvt_3_1_ = p_195939_1_.getWorld();
         BlockItemUseContext lvt_4_1_ = new BlockItemUseContext(p_195939_1_);
         BlockPos lvt_5_1_ = lvt_4_1_.getPos();
         BlockPos lvt_6_1_ = lvt_5_1_.up();
         if (lvt_4_1_.canPlace() && lvt_3_1_.getBlockState(lvt_6_1_).isReplaceable(lvt_4_1_)) {
            double lvt_7_1_ = (double)lvt_5_1_.getX();
            double lvt_9_1_ = (double)lvt_5_1_.getY();
            double lvt_11_1_ = (double)lvt_5_1_.getZ();
            List<Entity> lvt_13_1_ = lvt_3_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_7_1_ + 1.0D, lvt_9_1_ + 2.0D, lvt_11_1_ + 1.0D));
            if (!lvt_13_1_.isEmpty()) {
               return ActionResultType.FAIL;
            } else {
               ItemStack lvt_14_1_ = p_195939_1_.getItem();
               if (!lvt_3_1_.isRemote) {
                  lvt_3_1_.removeBlock(lvt_5_1_, false);
                  lvt_3_1_.removeBlock(lvt_6_1_, false);
                  ArmorStandEntity lvt_15_1_ = new ArmorStandEntity(lvt_3_1_, lvt_7_1_ + 0.5D, lvt_9_1_, lvt_11_1_ + 0.5D);
                  float lvt_16_1_ = (float)MathHelper.floor((MathHelper.wrapDegrees(p_195939_1_.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  lvt_15_1_.setLocationAndAngles(lvt_7_1_ + 0.5D, lvt_9_1_, lvt_11_1_ + 0.5D, lvt_16_1_, 0.0F);
                  this.applyRandomRotations(lvt_15_1_, lvt_3_1_.rand);
                  EntityType.applyItemNBT(lvt_3_1_, p_195939_1_.getPlayer(), lvt_15_1_, lvt_14_1_.getTag());
                  lvt_3_1_.addEntity(lvt_15_1_);
                  lvt_3_1_.playSound((PlayerEntity)null, lvt_15_1_.func_226277_ct_(), lvt_15_1_.func_226278_cu_(), lvt_15_1_.func_226281_cx_(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
               }

               lvt_14_1_.shrink(1);
               return ActionResultType.SUCCESS;
            }
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   private void applyRandomRotations(ArmorStandEntity p_179221_1_, Random p_179221_2_) {
      Rotations lvt_3_1_ = p_179221_1_.getHeadRotation();
      float lvt_5_1_ = p_179221_2_.nextFloat() * 5.0F;
      float lvt_6_1_ = p_179221_2_.nextFloat() * 20.0F - 10.0F;
      Rotations lvt_4_1_ = new Rotations(lvt_3_1_.getX() + lvt_5_1_, lvt_3_1_.getY() + lvt_6_1_, lvt_3_1_.getZ());
      p_179221_1_.setHeadRotation(lvt_4_1_);
      lvt_3_1_ = p_179221_1_.getBodyRotation();
      lvt_5_1_ = p_179221_2_.nextFloat() * 10.0F - 5.0F;
      lvt_4_1_ = new Rotations(lvt_3_1_.getX(), lvt_3_1_.getY() + lvt_5_1_, lvt_3_1_.getZ());
      p_179221_1_.setBodyRotation(lvt_4_1_);
   }
}
