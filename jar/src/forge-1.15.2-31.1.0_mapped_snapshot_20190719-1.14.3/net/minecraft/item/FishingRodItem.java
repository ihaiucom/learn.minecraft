package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
   public FishingRodItem(Item.Properties p_i48494_1_) {
      super(p_i48494_1_);
      this.addPropertyOverride(new ResourceLocation("cast"), (p_210313_0_, p_210313_1_, p_210313_2_) -> {
         if (p_210313_2_ == null) {
            return 0.0F;
         } else {
            boolean lvt_3_1_ = p_210313_2_.getHeldItemMainhand() == p_210313_0_;
            boolean lvt_4_1_ = p_210313_2_.getHeldItemOffhand() == p_210313_0_;
            if (p_210313_2_.getHeldItemMainhand().getItem() instanceof FishingRodItem) {
               lvt_4_1_ = false;
            }

            return (lvt_3_1_ || lvt_4_1_) && p_210313_2_ instanceof PlayerEntity && ((PlayerEntity)p_210313_2_).fishingBobber != null ? 1.0F : 0.0F;
         }
      });
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      int lvt_5_1_;
      if (p_77659_2_.fishingBobber != null) {
         if (!p_77659_1_.isRemote) {
            lvt_5_1_ = p_77659_2_.fishingBobber.handleHookRetraction(lvt_4_1_);
            lvt_4_1_.damageItem(lvt_5_1_, p_77659_2_, (p_220000_1_) -> {
               p_220000_1_.sendBreakAnimation(p_77659_3_);
            });
         }

         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!p_77659_1_.isRemote) {
            lvt_5_1_ = EnchantmentHelper.getFishingSpeedBonus(lvt_4_1_);
            int lvt_6_1_ = EnchantmentHelper.getFishingLuckBonus(lvt_4_1_);
            p_77659_1_.addEntity(new FishingBobberEntity(p_77659_2_, p_77659_1_, lvt_6_1_, lvt_5_1_));
         }

         p_77659_2_.addStat(Stats.ITEM_USED.get(this));
      }

      return ActionResult.func_226248_a_(lvt_4_1_);
   }

   public int getItemEnchantability() {
      return 1;
   }
}
