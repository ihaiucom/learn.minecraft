package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class NameTagItem extends Item {
   public NameTagItem(Item.Properties p_i48479_1_) {
      super(p_i48479_1_);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_1_.hasDisplayName() && !(p_111207_3_ instanceof PlayerEntity)) {
         if (p_111207_3_.isAlive()) {
            p_111207_3_.setCustomName(p_111207_1_.getDisplayName());
            if (p_111207_3_ instanceof MobEntity) {
               ((MobEntity)p_111207_3_).enablePersistence();
            }

            p_111207_1_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
