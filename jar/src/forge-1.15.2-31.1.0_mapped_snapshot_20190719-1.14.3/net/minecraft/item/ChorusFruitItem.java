package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties p_i50053_1_) {
      super(p_i50053_1_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      ItemStack lvt_4_1_ = super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
      if (!p_77654_2_.isRemote) {
         double lvt_5_1_ = p_77654_3_.func_226277_ct_();
         double lvt_7_1_ = p_77654_3_.func_226278_cu_();
         double lvt_9_1_ = p_77654_3_.func_226281_cx_();

         for(int lvt_11_1_ = 0; lvt_11_1_ < 16; ++lvt_11_1_) {
            double lvt_12_1_ = p_77654_3_.func_226277_ct_() + (p_77654_3_.getRNG().nextDouble() - 0.5D) * 16.0D;
            double lvt_14_1_ = MathHelper.clamp(p_77654_3_.func_226278_cu_() + (double)(p_77654_3_.getRNG().nextInt(16) - 8), 0.0D, (double)(p_77654_2_.getActualHeight() - 1));
            double lvt_16_1_ = p_77654_3_.func_226281_cx_() + (p_77654_3_.getRNG().nextDouble() - 0.5D) * 16.0D;
            if (p_77654_3_.isPassenger()) {
               p_77654_3_.stopRiding();
            }

            if (p_77654_3_.attemptTeleport(lvt_12_1_, lvt_14_1_, lvt_16_1_, true)) {
               p_77654_2_.playSound((PlayerEntity)null, lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               p_77654_3_.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               break;
            }
         }

         if (p_77654_3_ instanceof PlayerEntity) {
            ((PlayerEntity)p_77654_3_).getCooldownTracker().setCooldown(this, 20);
         }
      }

      return lvt_4_1_;
   }
}
