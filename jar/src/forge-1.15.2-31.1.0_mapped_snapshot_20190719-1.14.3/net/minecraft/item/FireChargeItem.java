package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties p_i48499_1_) {
      super(p_i48499_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      boolean lvt_5_1_ = false;
      if (lvt_4_1_.getBlock() == Blocks.CAMPFIRE) {
         if (!(Boolean)lvt_4_1_.get(CampfireBlock.LIT) && !(Boolean)lvt_4_1_.get(CampfireBlock.WATERLOGGED)) {
            this.playUseSound(lvt_2_1_, lvt_3_1_);
            lvt_2_1_.setBlockState(lvt_3_1_, (BlockState)lvt_4_1_.with(CampfireBlock.LIT, true));
            lvt_5_1_ = true;
         }
      } else {
         lvt_3_1_ = lvt_3_1_.offset(p_195939_1_.getFace());
         if (lvt_2_1_.getBlockState(lvt_3_1_).isAir()) {
            this.playUseSound(lvt_2_1_, lvt_3_1_);
            lvt_2_1_.setBlockState(lvt_3_1_, ((FireBlock)Blocks.FIRE).getStateForPlacement(lvt_2_1_, lvt_3_1_));
            lvt_5_1_ = true;
         }
      }

      if (lvt_5_1_) {
         p_195939_1_.getItem().shrink(1);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.FAIL;
      }
   }

   private void playUseSound(World p_219995_1_, BlockPos p_219995_2_) {
      p_219995_1_.playSound((PlayerEntity)null, p_219995_2_, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}
