package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
   public SuspiciousStewItem(Item.Properties p_i50035_1_) {
      super(p_i50035_1_);
   }

   public static void addEffect(ItemStack p_220037_0_, Effect p_220037_1_, int p_220037_2_) {
      CompoundNBT lvt_3_1_ = p_220037_0_.getOrCreateTag();
      ListNBT lvt_4_1_ = lvt_3_1_.getList("Effects", 9);
      CompoundNBT lvt_5_1_ = new CompoundNBT();
      lvt_5_1_.putByte("EffectId", (byte)Effect.getId(p_220037_1_));
      lvt_5_1_.putInt("EffectDuration", p_220037_2_);
      lvt_4_1_.add(lvt_5_1_);
      lvt_3_1_.put("Effects", lvt_4_1_);
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      ItemStack lvt_4_1_ = super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
      CompoundNBT lvt_5_1_ = p_77654_1_.getTag();
      if (lvt_5_1_ != null && lvt_5_1_.contains("Effects", 9)) {
         ListNBT lvt_6_1_ = lvt_5_1_.getList("Effects", 10);

         for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.size(); ++lvt_7_1_) {
            int lvt_8_1_ = 160;
            CompoundNBT lvt_9_1_ = lvt_6_1_.getCompound(lvt_7_1_);
            if (lvt_9_1_.contains("EffectDuration", 3)) {
               lvt_8_1_ = lvt_9_1_.getInt("EffectDuration");
            }

            Effect lvt_10_1_ = Effect.get(lvt_9_1_.getByte("EffectId"));
            if (lvt_10_1_ != null) {
               p_77654_3_.addPotionEffect(new EffectInstance(lvt_10_1_, lvt_8_1_));
            }
         }
      }

      return p_77654_3_ instanceof PlayerEntity && ((PlayerEntity)p_77654_3_).abilities.isCreativeMode ? lvt_4_1_ : new ItemStack(Items.BOWL);
   }
}
