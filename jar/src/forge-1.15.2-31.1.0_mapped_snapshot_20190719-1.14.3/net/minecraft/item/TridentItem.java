package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem extends Item {
   public TridentItem(Item.Properties p_i48788_1_) {
      super(p_i48788_1_);
      this.addPropertyOverride(new ResourceLocation("throwing"), (p_210315_0_, p_210315_1_, p_210315_2_) -> {
         return p_210315_2_ != null && p_210315_2_.isHandActive() && p_210315_2_.getActiveItemStack() == p_210315_0_ ? 1.0F : 0.0F;
      });
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return UseAction.SPEAR;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
      if (p_77615_3_ instanceof PlayerEntity) {
         PlayerEntity lvt_5_1_ = (PlayerEntity)p_77615_3_;
         int lvt_6_1_ = this.getUseDuration(p_77615_1_) - p_77615_4_;
         if (lvt_6_1_ >= 10) {
            int lvt_7_1_ = EnchantmentHelper.getRiptideModifier(p_77615_1_);
            if (lvt_7_1_ <= 0 || lvt_5_1_.isWet()) {
               if (!p_77615_2_.isRemote) {
                  p_77615_1_.damageItem(1, lvt_5_1_, (p_220047_1_) -> {
                     p_220047_1_.sendBreakAnimation(p_77615_3_.getActiveHand());
                  });
                  if (lvt_7_1_ == 0) {
                     TridentEntity lvt_8_1_ = new TridentEntity(p_77615_2_, lvt_5_1_, p_77615_1_);
                     lvt_8_1_.shoot(lvt_5_1_, lvt_5_1_.rotationPitch, lvt_5_1_.rotationYaw, 0.0F, 2.5F + (float)lvt_7_1_ * 0.5F, 1.0F);
                     if (lvt_5_1_.abilities.isCreativeMode) {
                        lvt_8_1_.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                     }

                     p_77615_2_.addEntity(lvt_8_1_);
                     p_77615_2_.playMovingSound((PlayerEntity)null, lvt_8_1_, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                     if (!lvt_5_1_.abilities.isCreativeMode) {
                        lvt_5_1_.inventory.deleteStack(p_77615_1_);
                     }
                  }
               }

               lvt_5_1_.addStat(Stats.ITEM_USED.get(this));
               if (lvt_7_1_ > 0) {
                  float lvt_8_2_ = lvt_5_1_.rotationYaw;
                  float lvt_9_1_ = lvt_5_1_.rotationPitch;
                  float lvt_10_1_ = -MathHelper.sin(lvt_8_2_ * 0.017453292F) * MathHelper.cos(lvt_9_1_ * 0.017453292F);
                  float lvt_11_1_ = -MathHelper.sin(lvt_9_1_ * 0.017453292F);
                  float lvt_12_1_ = MathHelper.cos(lvt_8_2_ * 0.017453292F) * MathHelper.cos(lvt_9_1_ * 0.017453292F);
                  float lvt_13_1_ = MathHelper.sqrt(lvt_10_1_ * lvt_10_1_ + lvt_11_1_ * lvt_11_1_ + lvt_12_1_ * lvt_12_1_);
                  float lvt_14_1_ = 3.0F * ((1.0F + (float)lvt_7_1_) / 4.0F);
                  lvt_10_1_ *= lvt_14_1_ / lvt_13_1_;
                  lvt_11_1_ *= lvt_14_1_ / lvt_13_1_;
                  lvt_12_1_ *= lvt_14_1_ / lvt_13_1_;
                  lvt_5_1_.addVelocity((double)lvt_10_1_, (double)lvt_11_1_, (double)lvt_12_1_);
                  lvt_5_1_.startSpinAttack(20);
                  if (lvt_5_1_.onGround) {
                     float lvt_15_1_ = 1.1999999F;
                     lvt_5_1_.move(MoverType.SELF, new Vec3d(0.0D, 1.1999999284744263D, 0.0D));
                  }

                  SoundEvent lvt_15_4_;
                  if (lvt_7_1_ >= 3) {
                     lvt_15_4_ = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                  } else if (lvt_7_1_ == 2) {
                     lvt_15_4_ = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                  } else {
                     lvt_15_4_ = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                  }

                  p_77615_2_.playMovingSound((PlayerEntity)null, lvt_5_1_, lvt_15_4_, SoundCategory.PLAYERS, 1.0F, 1.0F);
               }

            }
         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (lvt_4_1_.getDamage() >= lvt_4_1_.getMaxDamage() - 1) {
         return ActionResult.func_226251_d_(lvt_4_1_);
      } else if (EnchantmentHelper.getRiptideModifier(lvt_4_1_) > 0 && !p_77659_2_.isWet()) {
         return ActionResult.func_226251_d_(lvt_4_1_);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         return ActionResult.func_226249_b_(lvt_4_1_);
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_, (p_220048_0_) -> {
         p_220048_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if ((double)p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0D) {
         p_179218_1_.damageItem(2, p_179218_5_, (p_220046_0_) -> {
            p_220046_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> lvt_2_1_ = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         lvt_2_1_.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
         lvt_2_1_.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.9000000953674316D, AttributeModifier.Operation.ADDITION));
      }

      return lvt_2_1_;
   }

   public int getItemEnchantability() {
      return 1;
   }
}
