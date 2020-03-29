package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeadItem extends Item {
   public LeadItem(Item.Properties p_i48484_1_) {
      super(p_i48484_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      Block lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_).getBlock();
      if (lvt_4_1_.isIn(BlockTags.FENCES)) {
         PlayerEntity lvt_5_1_ = p_195939_1_.getPlayer();
         if (!lvt_2_1_.isRemote && lvt_5_1_ != null) {
            func_226641_a_(lvt_5_1_, lvt_2_1_, lvt_3_1_);
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public static ActionResultType func_226641_a_(PlayerEntity p_226641_0_, World p_226641_1_, BlockPos p_226641_2_) {
      LeashKnotEntity lvt_3_1_ = null;
      boolean lvt_4_1_ = false;
      double lvt_5_1_ = 7.0D;
      int lvt_7_1_ = p_226641_2_.getX();
      int lvt_8_1_ = p_226641_2_.getY();
      int lvt_9_1_ = p_226641_2_.getZ();
      List<MobEntity> lvt_10_1_ = p_226641_1_.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB((double)lvt_7_1_ - 7.0D, (double)lvt_8_1_ - 7.0D, (double)lvt_9_1_ - 7.0D, (double)lvt_7_1_ + 7.0D, (double)lvt_8_1_ + 7.0D, (double)lvt_9_1_ + 7.0D));
      Iterator var11 = lvt_10_1_.iterator();

      while(var11.hasNext()) {
         MobEntity lvt_12_1_ = (MobEntity)var11.next();
         if (lvt_12_1_.getLeashHolder() == p_226641_0_) {
            if (lvt_3_1_ == null) {
               lvt_3_1_ = LeashKnotEntity.create(p_226641_1_, p_226641_2_);
            }

            lvt_12_1_.setLeashHolder(lvt_3_1_, true);
            lvt_4_1_ = true;
         }
      }

      return lvt_4_1_ ? ActionResultType.SUCCESS : ActionResultType.PASS;
   }
}
