package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties p_i48474_1_) {
      super(p_i48474_1_);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_ instanceof PigEntity) {
         PigEntity lvt_5_1_ = (PigEntity)p_111207_3_;
         if (lvt_5_1_.isAlive() && !lvt_5_1_.getSaddled() && !lvt_5_1_.isChild()) {
            lvt_5_1_.setSaddled(true);
            lvt_5_1_.world.playSound(p_111207_2_, lvt_5_1_.func_226277_ct_(), lvt_5_1_.func_226278_cu_(), lvt_5_1_.func_226281_cx_(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            p_111207_1_.shrink(1);
            return true;
         }
      }

      return false;
   }
}
