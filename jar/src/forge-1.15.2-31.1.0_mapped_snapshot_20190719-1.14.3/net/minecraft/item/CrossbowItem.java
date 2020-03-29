package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrossbowItem extends ShootableItem {
   private boolean field_220034_c = false;
   private boolean field_220035_d = false;

   public CrossbowItem(Item.Properties p_i50052_1_) {
      super(p_i50052_1_);
      this.addPropertyOverride(new ResourceLocation("pull"), (p_220022_1_, p_220022_2_, p_220022_3_) -> {
         if (p_220022_3_ != null && p_220022_1_.getItem() == this) {
            return isCharged(p_220022_1_) ? 0.0F : (float)(p_220022_1_.getUseDuration() - p_220022_3_.getItemInUseCount()) / (float)getChargeTime(p_220022_1_);
         } else {
            return 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), (p_220033_0_, p_220033_1_, p_220033_2_) -> {
         return p_220033_2_ != null && p_220033_2_.isHandActive() && p_220033_2_.getActiveItemStack() == p_220033_0_ && !isCharged(p_220033_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("charged"), (p_220030_0_, p_220030_1_, p_220030_2_) -> {
         return p_220030_2_ != null && isCharged(p_220030_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("firework"), (p_220020_0_, p_220020_1_, p_220020_2_) -> {
         return p_220020_2_ != null && isCharged(p_220020_0_) && hasChargedProjectile(p_220020_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
   }

   public Predicate<ItemStack> getAmmoPredicate() {
      return ARROWS_OR_FIREWORKS;
   }

   public Predicate<ItemStack> getInventoryAmmoPredicate() {
      return ARROWS;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (isCharged(lvt_4_1_)) {
         fireProjectiles(p_77659_1_, p_77659_2_, p_77659_3_, lvt_4_1_, func_220013_l(lvt_4_1_), 1.0F);
         setCharged(lvt_4_1_, false);
         return ActionResult.func_226249_b_(lvt_4_1_);
      } else if (!p_77659_2_.findAmmo(lvt_4_1_).isEmpty()) {
         if (!isCharged(lvt_4_1_)) {
            this.field_220034_c = false;
            this.field_220035_d = false;
            p_77659_2_.setActiveHand(p_77659_3_);
         }

         return ActionResult.func_226249_b_(lvt_4_1_);
      } else {
         return ActionResult.func_226251_d_(lvt_4_1_);
      }
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
      int lvt_5_1_ = this.getUseDuration(p_77615_1_) - p_77615_4_;
      float lvt_6_1_ = getCharge(lvt_5_1_, p_77615_1_);
      if (lvt_6_1_ >= 1.0F && !isCharged(p_77615_1_) && hasAmmo(p_77615_3_, p_77615_1_)) {
         setCharged(p_77615_1_, true);
         SoundCategory lvt_7_1_ = p_77615_3_ instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         p_77615_2_.playSound((PlayerEntity)null, p_77615_3_.func_226277_ct_(), p_77615_3_.func_226278_cu_(), p_77615_3_.func_226281_cx_(), SoundEvents.ITEM_CROSSBOW_LOADING_END, lvt_7_1_, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
      }

   }

   private static boolean hasAmmo(LivingEntity p_220021_0_, ItemStack p_220021_1_) {
      int lvt_2_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, p_220021_1_);
      int lvt_3_1_ = lvt_2_1_ == 0 ? 1 : 3;
      boolean lvt_4_1_ = p_220021_0_ instanceof PlayerEntity && ((PlayerEntity)p_220021_0_).abilities.isCreativeMode;
      ItemStack lvt_5_1_ = p_220021_0_.findAmmo(p_220021_1_);
      ItemStack lvt_6_1_ = lvt_5_1_.copy();

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_3_1_; ++lvt_7_1_) {
         if (lvt_7_1_ > 0) {
            lvt_5_1_ = lvt_6_1_.copy();
         }

         if (lvt_5_1_.isEmpty() && lvt_4_1_) {
            lvt_5_1_ = new ItemStack(Items.ARROW);
            lvt_6_1_ = lvt_5_1_.copy();
         }

         if (!func_220023_a(p_220021_0_, p_220021_1_, lvt_5_1_, lvt_7_1_ > 0, lvt_4_1_)) {
            return false;
         }
      }

      return true;
   }

   private static boolean func_220023_a(LivingEntity p_220023_0_, ItemStack p_220023_1_, ItemStack p_220023_2_, boolean p_220023_3_, boolean p_220023_4_) {
      if (p_220023_2_.isEmpty()) {
         return false;
      } else {
         boolean lvt_5_1_ = p_220023_4_ && p_220023_2_.getItem() instanceof ArrowItem;
         ItemStack lvt_6_2_;
         if (!lvt_5_1_ && !p_220023_4_ && !p_220023_3_) {
            lvt_6_2_ = p_220023_2_.split(1);
            if (p_220023_2_.isEmpty() && p_220023_0_ instanceof PlayerEntity) {
               ((PlayerEntity)p_220023_0_).inventory.deleteStack(p_220023_2_);
            }
         } else {
            lvt_6_2_ = p_220023_2_.copy();
         }

         addChargedProjectile(p_220023_1_, lvt_6_2_);
         return true;
      }
   }

   public static boolean isCharged(ItemStack p_220012_0_) {
      CompoundNBT lvt_1_1_ = p_220012_0_.getTag();
      return lvt_1_1_ != null && lvt_1_1_.getBoolean("Charged");
   }

   public static void setCharged(ItemStack p_220011_0_, boolean p_220011_1_) {
      CompoundNBT lvt_2_1_ = p_220011_0_.getOrCreateTag();
      lvt_2_1_.putBoolean("Charged", p_220011_1_);
   }

   private static void addChargedProjectile(ItemStack p_220029_0_, ItemStack p_220029_1_) {
      CompoundNBT lvt_2_1_ = p_220029_0_.getOrCreateTag();
      ListNBT lvt_3_2_;
      if (lvt_2_1_.contains("ChargedProjectiles", 9)) {
         lvt_3_2_ = lvt_2_1_.getList("ChargedProjectiles", 10);
      } else {
         lvt_3_2_ = new ListNBT();
      }

      CompoundNBT lvt_4_1_ = new CompoundNBT();
      p_220029_1_.write(lvt_4_1_);
      lvt_3_2_.add(lvt_4_1_);
      lvt_2_1_.put("ChargedProjectiles", lvt_3_2_);
   }

   private static List<ItemStack> getChargedProjectiles(ItemStack p_220018_0_) {
      List<ItemStack> lvt_1_1_ = Lists.newArrayList();
      CompoundNBT lvt_2_1_ = p_220018_0_.getTag();
      if (lvt_2_1_ != null && lvt_2_1_.contains("ChargedProjectiles", 9)) {
         ListNBT lvt_3_1_ = lvt_2_1_.getList("ChargedProjectiles", 10);
         if (lvt_3_1_ != null) {
            for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_) {
               CompoundNBT lvt_5_1_ = lvt_3_1_.getCompound(lvt_4_1_);
               lvt_1_1_.add(ItemStack.read(lvt_5_1_));
            }
         }
      }

      return lvt_1_1_;
   }

   private static void clearProjectiles(ItemStack p_220027_0_) {
      CompoundNBT lvt_1_1_ = p_220027_0_.getTag();
      if (lvt_1_1_ != null) {
         ListNBT lvt_2_1_ = lvt_1_1_.getList("ChargedProjectiles", 9);
         lvt_2_1_.clear();
         lvt_1_1_.put("ChargedProjectiles", lvt_2_1_);
      }

   }

   private static boolean hasChargedProjectile(ItemStack p_220019_0_, Item p_220019_1_) {
      return getChargedProjectiles(p_220019_0_).stream().anyMatch((p_220010_1_) -> {
         return p_220010_1_.getItem() == p_220019_1_;
      });
   }

   private static void func_220016_a(World p_220016_0_, LivingEntity p_220016_1_, Hand p_220016_2_, ItemStack p_220016_3_, ItemStack p_220016_4_, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
      if (!p_220016_0_.isRemote) {
         boolean lvt_10_1_ = p_220016_4_.getItem() == Items.FIREWORK_ROCKET;
         Object lvt_11_2_;
         if (lvt_10_1_) {
            lvt_11_2_ = new FireworkRocketEntity(p_220016_0_, p_220016_4_, p_220016_1_.func_226277_ct_(), p_220016_1_.func_226280_cw_() - 0.15000000596046448D, p_220016_1_.func_226281_cx_(), true);
         } else {
            lvt_11_2_ = createArrow(p_220016_0_, p_220016_1_, p_220016_3_, p_220016_4_);
            if (p_220016_6_ || p_220016_9_ != 0.0F) {
               ((AbstractArrowEntity)lvt_11_2_).pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }
         }

         if (p_220016_1_ instanceof ICrossbowUser) {
            ICrossbowUser lvt_12_1_ = (ICrossbowUser)p_220016_1_;
            lvt_12_1_.shoot(lvt_12_1_.getAttackTarget(), p_220016_3_, (IProjectile)lvt_11_2_, p_220016_9_);
         } else {
            Vec3d lvt_12_2_ = p_220016_1_.func_213286_i(1.0F);
            Quaternion lvt_13_1_ = new Quaternion(new Vector3f(lvt_12_2_), p_220016_9_, true);
            Vec3d lvt_14_1_ = p_220016_1_.getLook(1.0F);
            Vector3f lvt_15_1_ = new Vector3f(lvt_14_1_);
            lvt_15_1_.func_214905_a(lvt_13_1_);
            ((IProjectile)lvt_11_2_).shoot((double)lvt_15_1_.getX(), (double)lvt_15_1_.getY(), (double)lvt_15_1_.getZ(), p_220016_7_, p_220016_8_);
         }

         p_220016_3_.damageItem(lvt_10_1_ ? 3 : 1, p_220016_1_, (p_220017_1_) -> {
            p_220017_1_.sendBreakAnimation(p_220016_2_);
         });
         p_220016_0_.addEntity((Entity)lvt_11_2_);
         p_220016_0_.playSound((PlayerEntity)null, p_220016_1_.func_226277_ct_(), p_220016_1_.func_226278_cu_(), p_220016_1_.func_226281_cx_(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_220016_5_);
      }
   }

   private static AbstractArrowEntity createArrow(World p_220024_0_, LivingEntity p_220024_1_, ItemStack p_220024_2_, ItemStack p_220024_3_) {
      ArrowItem lvt_4_1_ = (ArrowItem)((ArrowItem)(p_220024_3_.getItem() instanceof ArrowItem ? p_220024_3_.getItem() : Items.ARROW));
      AbstractArrowEntity lvt_5_1_ = lvt_4_1_.createArrow(p_220024_0_, p_220024_3_, p_220024_1_);
      if (p_220024_1_ instanceof PlayerEntity) {
         lvt_5_1_.setIsCritical(true);
      }

      lvt_5_1_.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
      lvt_5_1_.func_213865_o(true);
      int lvt_6_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, p_220024_2_);
      if (lvt_6_1_ > 0) {
         lvt_5_1_.func_213872_b((byte)lvt_6_1_);
      }

      return lvt_5_1_;
   }

   public static void fireProjectiles(World p_220014_0_, LivingEntity p_220014_1_, Hand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
      List<ItemStack> lvt_6_1_ = getChargedProjectiles(p_220014_3_);
      float[] lvt_7_1_ = func_220028_a(p_220014_1_.getRNG());

      for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_.size(); ++lvt_8_1_) {
         ItemStack lvt_9_1_ = (ItemStack)lvt_6_1_.get(lvt_8_1_);
         boolean lvt_10_1_ = p_220014_1_ instanceof PlayerEntity && ((PlayerEntity)p_220014_1_).abilities.isCreativeMode;
         if (!lvt_9_1_.isEmpty()) {
            if (lvt_8_1_ == 0) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, lvt_9_1_, lvt_7_1_[lvt_8_1_], lvt_10_1_, p_220014_4_, p_220014_5_, 0.0F);
            } else if (lvt_8_1_ == 1) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, lvt_9_1_, lvt_7_1_[lvt_8_1_], lvt_10_1_, p_220014_4_, p_220014_5_, -10.0F);
            } else if (lvt_8_1_ == 2) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, lvt_9_1_, lvt_7_1_[lvt_8_1_], lvt_10_1_, p_220014_4_, p_220014_5_, 10.0F);
            }
         }
      }

      func_220015_a(p_220014_0_, p_220014_1_, p_220014_3_);
   }

   private static float[] func_220028_a(Random p_220028_0_) {
      boolean lvt_1_1_ = p_220028_0_.nextBoolean();
      return new float[]{1.0F, func_220032_a(lvt_1_1_), func_220032_a(!lvt_1_1_)};
   }

   private static float func_220032_a(boolean p_220032_0_) {
      float lvt_1_1_ = p_220032_0_ ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + lvt_1_1_;
   }

   private static void func_220015_a(World p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
      if (p_220015_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity lvt_3_1_ = (ServerPlayerEntity)p_220015_1_;
         if (!p_220015_0_.isRemote) {
            CriteriaTriggers.SHOT_CROSSBOW.func_215111_a(lvt_3_1_, p_220015_2_);
         }

         lvt_3_1_.addStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
      }

      clearProjectiles(p_220015_2_);
   }

   public void func_219972_a(World p_219972_1_, LivingEntity p_219972_2_, ItemStack p_219972_3_, int p_219972_4_) {
      if (!p_219972_1_.isRemote) {
         int lvt_5_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, p_219972_3_);
         SoundEvent lvt_6_1_ = this.func_220025_a(lvt_5_1_);
         SoundEvent lvt_7_1_ = lvt_5_1_ == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
         float lvt_8_1_ = (float)(p_219972_3_.getUseDuration() - p_219972_4_) / (float)getChargeTime(p_219972_3_);
         if (lvt_8_1_ < 0.2F) {
            this.field_220034_c = false;
            this.field_220035_d = false;
         }

         if (lvt_8_1_ >= 0.2F && !this.field_220034_c) {
            this.field_220034_c = true;
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.func_226277_ct_(), p_219972_2_.func_226278_cu_(), p_219972_2_.func_226281_cx_(), lvt_6_1_, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }

         if (lvt_8_1_ >= 0.5F && lvt_7_1_ != null && !this.field_220035_d) {
            this.field_220035_d = true;
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.func_226277_ct_(), p_219972_2_.func_226278_cu_(), p_219972_2_.func_226281_cx_(), lvt_7_1_, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }
      }

   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return getChargeTime(p_77626_1_) + 3;
   }

   public static int getChargeTime(ItemStack p_220026_0_) {
      int lvt_1_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, p_220026_0_);
      return lvt_1_1_ == 0 ? 25 : 25 - 5 * lvt_1_1_;
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return UseAction.CROSSBOW;
   }

   private SoundEvent func_220025_a(int p_220025_1_) {
      switch(p_220025_1_) {
      case 1:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.ITEM_CROSSBOW_LOADING_START;
      }
   }

   private static float getCharge(int p_220031_0_, ItemStack p_220031_1_) {
      float lvt_2_1_ = (float)p_220031_0_ / (float)getChargeTime(p_220031_1_);
      if (lvt_2_1_ > 1.0F) {
         lvt_2_1_ = 1.0F;
      }

      return lvt_2_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      List<ItemStack> lvt_5_1_ = getChargedProjectiles(p_77624_1_);
      if (isCharged(p_77624_1_) && !lvt_5_1_.isEmpty()) {
         ItemStack lvt_6_1_ = (ItemStack)lvt_5_1_.get(0);
         p_77624_3_.add((new TranslationTextComponent("item.minecraft.crossbow.projectile", new Object[0])).appendText(" ").appendSibling(lvt_6_1_.getTextComponent()));
         if (p_77624_4_.isAdvanced() && lvt_6_1_.getItem() == Items.FIREWORK_ROCKET) {
            List<ITextComponent> lvt_7_1_ = Lists.newArrayList();
            Items.FIREWORK_ROCKET.addInformation(lvt_6_1_, p_77624_2_, lvt_7_1_, p_77624_4_);
            if (!lvt_7_1_.isEmpty()) {
               for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.size(); ++lvt_8_1_) {
                  lvt_7_1_.set(lvt_8_1_, (new StringTextComponent("  ")).appendSibling((ITextComponent)lvt_7_1_.get(lvt_8_1_)).applyTextStyle(TextFormatting.GRAY));
               }

               p_77624_3_.addAll(lvt_7_1_);
            }
         }

      }
   }

   private static float func_220013_l(ItemStack p_220013_0_) {
      return p_220013_0_.getItem() == Items.CROSSBOW && hasChargedProjectile(p_220013_0_, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }
}
