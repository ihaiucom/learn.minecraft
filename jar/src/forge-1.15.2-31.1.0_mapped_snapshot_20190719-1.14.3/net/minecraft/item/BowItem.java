package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class BowItem extends ShootableItem {
   public BowItem(Item.Properties p_i48522_1_) {
      super(p_i48522_1_);
      this.addPropertyOverride(new ResourceLocation("pull"), (p_lambda$new$0_0_, p_lambda$new$0_1_, p_lambda$new$0_2_) -> {
         if (p_lambda$new$0_2_ == null) {
            return 0.0F;
         } else {
            return !(p_lambda$new$0_2_.getActiveItemStack().getItem() instanceof BowItem) ? 0.0F : (float)(p_lambda$new$0_0_.getUseDuration() - p_lambda$new$0_2_.getItemInUseCount()) / 20.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), (p_lambda$new$1_0_, p_lambda$new$1_1_, p_lambda$new$1_2_) -> {
         return p_lambda$new$1_2_ != null && p_lambda$new$1_2_.isHandActive() && p_lambda$new$1_2_.getActiveItemStack() == p_lambda$new$1_0_ ? 1.0F : 0.0F;
      });
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
      if (p_77615_3_ instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)p_77615_3_;
         boolean flag = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, p_77615_1_) > 0;
         ItemStack itemstack = playerentity.findAmmo(p_77615_1_);
         int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
         i = ForgeEventFactory.onArrowLoose(p_77615_1_, p_77615_2_, playerentity, i, !itemstack.isEmpty() || flag);
         if (i < 0) {
            return;
         }

         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(Items.ARROW);
            }

            float f = getArrowVelocity(i);
            if ((double)f >= 0.1D) {
               boolean flag1 = playerentity.abilities.isCreativeMode || itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, p_77615_1_, playerentity);
               if (!p_77615_2_.isRemote) {
                  ArrowItem arrowitem = (ArrowItem)((ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW));
                  AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_77615_2_, itemstack, playerentity);
                  abstractarrowentity = this.customeArrow(abstractarrowentity);
                  abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                  if (f == 1.0F) {
                     abstractarrowentity.setIsCritical(true);
                  }

                  int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, p_77615_1_);
                  if (j > 0) {
                     abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double)j * 0.5D + 0.5D);
                  }

                  int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, p_77615_1_);
                  if (k > 0) {
                     abstractarrowentity.setKnockbackStrength(k);
                  }

                  if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, p_77615_1_) > 0) {
                     abstractarrowentity.setFire(100);
                  }

                  p_77615_1_.damageItem(1, playerentity, (p_lambda$onPlayerStoppedUsing$2_1_) -> {
                     p_lambda$onPlayerStoppedUsing$2_1_.sendBreakAnimation(playerentity.getActiveHand());
                  });
                  if (flag1 || playerentity.abilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                     abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                  }

                  p_77615_2_.addEntity(abstractarrowentity);
               }

               p_77615_2_.playSound((PlayerEntity)null, playerentity.func_226277_ct_(), playerentity.func_226278_cu_(), playerentity.func_226281_cx_(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               if (!flag1 && !playerentity.abilities.isCreativeMode) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     playerentity.inventory.deleteStack(itemstack);
                  }
               }

               playerentity.addStat(Stats.ITEM_USED.get(this));
            }
         }
      }

   }

   public static float getArrowVelocity(int p_185059_0_) {
      float f = (float)p_185059_0_ / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return UseAction.BOW;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      boolean flag = !p_77659_2_.findAmmo(itemstack).isEmpty();
      ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemstack, p_77659_1_, p_77659_2_, p_77659_3_, flag);
      if (ret != null) {
         return ret;
      } else if (!p_77659_2_.abilities.isCreativeMode && !flag) {
         return ActionResult.func_226251_d_(itemstack);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         return ActionResult.func_226249_b_(itemstack);
      }
   }

   public Predicate<ItemStack> getInventoryAmmoPredicate() {
      return ARROWS;
   }

   public AbstractArrowEntity customeArrow(AbstractArrowEntity p_customeArrow_1_) {
      return p_customeArrow_1_;
   }
}
