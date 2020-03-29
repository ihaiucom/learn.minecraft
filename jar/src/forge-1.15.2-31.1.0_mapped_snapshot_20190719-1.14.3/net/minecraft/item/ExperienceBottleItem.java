package net.minecraft.item;

import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ExperienceBottleItem extends Item {
   public ExperienceBottleItem(Item.Properties p_i48500_1_) {
      super(p_i48500_1_);
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!p_77659_1_.isRemote) {
         ExperienceBottleEntity lvt_5_1_ = new ExperienceBottleEntity(p_77659_1_, p_77659_2_);
         lvt_5_1_.func_213884_b(lvt_4_1_);
         lvt_5_1_.shoot(p_77659_2_, p_77659_2_.rotationPitch, p_77659_2_.rotationYaw, -20.0F, 0.7F, 1.0F);
         p_77659_1_.addEntity(lvt_5_1_);
      }

      p_77659_2_.addStat(Stats.ITEM_USED.get(this));
      if (!p_77659_2_.abilities.isCreativeMode) {
         lvt_4_1_.shrink(1);
      }

      return ActionResult.func_226248_a_(lvt_4_1_);
   }
}
