package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
   public CarrotOnAStickItem(Item.Properties p_i48519_1_) {
      super(p_i48519_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (p_77659_1_.isRemote) {
         return ActionResult.func_226250_c_(lvt_4_1_);
      } else {
         if (p_77659_2_.isPassenger() && p_77659_2_.getRidingEntity() instanceof PigEntity) {
            PigEntity lvt_5_1_ = (PigEntity)p_77659_2_.getRidingEntity();
            if (lvt_4_1_.getMaxDamage() - lvt_4_1_.getDamage() >= 7 && lvt_5_1_.boost()) {
               lvt_4_1_.damageItem(7, p_77659_2_, (p_219991_1_) -> {
                  p_219991_1_.sendBreakAnimation(p_77659_3_);
               });
               if (lvt_4_1_.isEmpty()) {
                  ItemStack lvt_6_1_ = new ItemStack(Items.FISHING_ROD);
                  lvt_6_1_.setTag(lvt_4_1_.getTag());
                  return ActionResult.func_226248_a_(lvt_6_1_);
               }

               return ActionResult.func_226248_a_(lvt_4_1_);
            }
         }

         p_77659_2_.addStat(Stats.ITEM_USED.get(this));
         return ActionResult.func_226250_c_(lvt_4_1_);
      }
   }
}
