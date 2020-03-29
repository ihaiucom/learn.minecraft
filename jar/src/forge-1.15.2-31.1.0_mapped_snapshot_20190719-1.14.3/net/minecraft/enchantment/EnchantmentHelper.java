package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int getEnchantmentLevel(Enchantment p_77506_0_, ItemStack p_77506_1_) {
      if (p_77506_1_.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(p_77506_0_);
         ListNBT listnbt = p_77506_1_.getEnchantmentTagList();

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack p_82781_0_) {
      ListNBT listnbt = p_82781_0_.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(p_82781_0_) : p_82781_0_.getEnchantmentTagList();
      return func_226652_a_(listnbt);
   }

   public static Map<Enchantment, Integer> func_226652_a_(ListNBT p_226652_0_) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

      for(int i = 0; i < p_226652_0_.size(); ++i) {
         CompoundNBT compoundnbt = p_226652_0_.getCompound(i);
         Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(compoundnbt.getString("id"))).ifPresent((p_lambda$func_226652_a_$0_2_) -> {
            Integer integer = (Integer)map.put(p_lambda$func_226652_a_$0_2_, compoundnbt.getInt("lvl"));
         });
      }

      return map;
   }

   public static void setEnchantments(Map<Enchantment, Integer> p_82782_0_, ItemStack p_82782_1_) {
      ListNBT listnbt = new ListNBT();
      Iterator var3 = p_82782_0_.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Enchantment, Integer> entry = (Entry)var3.next();
         Enchantment enchantment = (Enchantment)entry.getKey();
         if (enchantment != null) {
            int i = (Integer)entry.getValue();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
            compoundnbt.putShort("lvl", (short)i);
            listnbt.add(compoundnbt);
            if (p_82782_1_.getItem() == Items.ENCHANTED_BOOK) {
               EnchantedBookItem.addEnchantment(p_82782_1_, new EnchantmentData(enchantment, i));
            }
         }
      }

      if (listnbt.isEmpty()) {
         p_82782_1_.removeChildTag("Enchantments");
      } else if (p_82782_1_.getItem() != Items.ENCHANTED_BOOK) {
         p_82782_1_.setTagInfo("Enchantments", listnbt);
      }

   }

   private static void applyEnchantmentModifier(EnchantmentHelper.IEnchantmentVisitor p_77518_0_, ItemStack p_77518_1_) {
      if (!p_77518_1_.isEmpty()) {
         ListNBT listnbt = p_77518_1_.getEnchantmentTagList();

         for(int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getCompound(i).getString("id");
            int j = listnbt.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(s)).ifPresent((p_lambda$applyEnchantmentModifier$1_2_) -> {
               p_77518_0_.accept(p_lambda$applyEnchantmentModifier$1_2_, j);
            });
         }
      }

   }

   private static void applyEnchantmentModifierArray(EnchantmentHelper.IEnchantmentVisitor p_77516_0_, Iterable<ItemStack> p_77516_1_) {
      Iterator var2 = p_77516_1_.iterator();

      while(var2.hasNext()) {
         ItemStack itemstack = (ItemStack)var2.next();
         applyEnchantmentModifier(p_77516_0_, itemstack);
      }

   }

   public static int getEnchantmentModifierDamage(Iterable<ItemStack> p_77508_0_, DamageSource p_77508_1_) {
      MutableInt mutableint = new MutableInt();
      applyEnchantmentModifierArray((p_lambda$getEnchantmentModifierDamage$2_2_, p_lambda$getEnchantmentModifierDamage$2_3_) -> {
         mutableint.add(p_lambda$getEnchantmentModifierDamage$2_2_.calcModifierDamage(p_lambda$getEnchantmentModifierDamage$2_3_, p_77508_1_));
      }, p_77508_0_);
      return mutableint.intValue();
   }

   public static float getModifierForCreature(ItemStack p_152377_0_, CreatureAttribute p_152377_1_) {
      MutableFloat mutablefloat = new MutableFloat();
      applyEnchantmentModifier((p_lambda$getModifierForCreature$3_2_, p_lambda$getModifierForCreature$3_3_) -> {
         mutablefloat.add(p_lambda$getModifierForCreature$3_2_.calcDamageByCreature(p_lambda$getModifierForCreature$3_3_, p_152377_1_));
      }, p_152377_0_);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity p_191527_0_) {
      int i = getMaxEnchantmentLevel(Enchantments.SWEEPING, p_191527_0_);
      return i > 0 ? SweepingEnchantment.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void applyThornEnchantments(LivingEntity p_151384_0_, Entity p_151384_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_lambda$applyThornEnchantments$4_2_, p_lambda$applyThornEnchantments$4_3_) -> {
         p_lambda$applyThornEnchantments$4_2_.onUserHurt(p_151384_0_, p_151384_1_, p_lambda$applyThornEnchantments$4_3_);
      };
      if (p_151384_0_ != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getEquipmentAndArmor());
      }

      if (p_151384_1_ instanceof PlayerEntity) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getHeldItemMainhand());
      }

   }

   public static void applyArthropodEnchantments(LivingEntity p_151385_0_, Entity p_151385_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_lambda$applyArthropodEnchantments$5_2_, p_lambda$applyArthropodEnchantments$5_3_) -> {
         p_lambda$applyArthropodEnchantments$5_2_.onEntityDamaged(p_151385_0_, p_151385_1_, p_lambda$applyArthropodEnchantments$5_3_);
      };
      if (p_151385_0_ != null) {
         applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getEquipmentAndArmor());
      }

      if (p_151385_0_ instanceof PlayerEntity) {
         applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getHeldItemMainhand());
      }

   }

   public static int getMaxEnchantmentLevel(Enchantment p_185284_0_, LivingEntity p_185284_1_) {
      Iterable<ItemStack> iterable = p_185284_0_.getEntityEquipment(p_185284_1_).values();
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;
         Iterator var4 = iterable.iterator();

         while(var4.hasNext()) {
            ItemStack itemstack = (ItemStack)var4.next();
            int j = getEnchantmentLevel(p_185284_0_, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   public static int getKnockbackModifier(LivingEntity p_77501_0_) {
      return getMaxEnchantmentLevel(Enchantments.KNOCKBACK, p_77501_0_);
   }

   public static int getFireAspectModifier(LivingEntity p_90036_0_) {
      return getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, p_90036_0_);
   }

   public static int getRespirationModifier(LivingEntity p_185292_0_) {
      return getMaxEnchantmentLevel(Enchantments.RESPIRATION, p_185292_0_);
   }

   public static int getDepthStriderModifier(LivingEntity p_185294_0_) {
      return getMaxEnchantmentLevel(Enchantments.DEPTH_STRIDER, p_185294_0_);
   }

   public static int getEfficiencyModifier(LivingEntity p_185293_0_) {
      return getMaxEnchantmentLevel(Enchantments.EFFICIENCY, p_185293_0_);
   }

   public static int getFishingLuckBonus(ItemStack p_191529_0_) {
      return getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, p_191529_0_);
   }

   public static int getFishingSpeedBonus(ItemStack p_191528_0_) {
      return getEnchantmentLevel(Enchantments.LURE, p_191528_0_);
   }

   public static int getLootingModifier(LivingEntity p_185283_0_) {
      return getMaxEnchantmentLevel(Enchantments.LOOTING, p_185283_0_);
   }

   public static boolean hasAquaAffinity(LivingEntity p_185287_0_) {
      return getMaxEnchantmentLevel(Enchantments.AQUA_AFFINITY, p_185287_0_) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity p_189869_0_) {
      return getMaxEnchantmentLevel(Enchantments.FROST_WALKER, p_189869_0_) > 0;
   }

   public static boolean hasBindingCurse(ItemStack p_190938_0_) {
      return getEnchantmentLevel(Enchantments.BINDING_CURSE, p_190938_0_) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack p_190939_0_) {
      return getEnchantmentLevel(Enchantments.VANISHING_CURSE, p_190939_0_) > 0;
   }

   public static int getLoyaltyModifier(ItemStack p_203191_0_) {
      return getEnchantmentLevel(Enchantments.LOYALTY, p_203191_0_);
   }

   public static int getRiptideModifier(ItemStack p_203190_0_) {
      return getEnchantmentLevel(Enchantments.RIPTIDE, p_203190_0_);
   }

   public static boolean hasChanneling(ItemStack p_203192_0_) {
      return getEnchantmentLevel(Enchantments.CHANNELING, p_203192_0_) > 0;
   }

   @Nullable
   public static Entry<EquipmentSlotType, ItemStack> func_222189_b(Enchantment p_222189_0_, LivingEntity p_222189_1_) {
      Map<EquipmentSlotType, ItemStack> map = p_222189_0_.getEntityEquipment(p_222189_1_);
      if (map.isEmpty()) {
         return null;
      } else {
         List<Entry<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();
         Iterator var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<EquipmentSlotType, ItemStack> entry = (Entry)var4.next();
            ItemStack itemstack = (ItemStack)entry.getValue();
            if (!itemstack.isEmpty() && getEnchantmentLevel(p_222189_0_, itemstack) > 0) {
               list.add(entry);
            }
         }

         return list.isEmpty() ? null : (Entry)list.get(p_222189_1_.getRNG().nextInt(list.size()));
      }
   }

   public static int calcItemStackEnchantability(Random p_77514_0_, int p_77514_1_, int p_77514_2_, ItemStack p_77514_3_) {
      Item item = p_77514_3_.getItem();
      int i = p_77514_3_.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (p_77514_2_ > 15) {
            p_77514_2_ = 15;
         }

         int j = p_77514_0_.nextInt(8) + 1 + (p_77514_2_ >> 1) + p_77514_0_.nextInt(p_77514_2_ + 1);
         if (p_77514_1_ == 0) {
            return Math.max(j / 3, 1);
         } else {
            return p_77514_1_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_77514_2_ * 2);
         }
      }
   }

   public static ItemStack addRandomEnchantment(Random p_77504_0_, ItemStack p_77504_1_, int p_77504_2_, boolean p_77504_3_) {
      List<EnchantmentData> list = buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_, p_77504_3_);
      boolean flag = p_77504_1_.getItem() == Items.BOOK;
      if (flag) {
         p_77504_1_ = new ItemStack(Items.ENCHANTED_BOOK);
      }

      Iterator var6 = list.iterator();

      while(var6.hasNext()) {
         EnchantmentData enchantmentdata = (EnchantmentData)var6.next();
         if (flag) {
            EnchantedBookItem.addEnchantment(p_77504_1_, enchantmentdata);
         } else {
            p_77504_1_.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
         }
      }

      return p_77504_1_;
   }

   public static List<EnchantmentData> buildEnchantmentList(Random p_77513_0_, ItemStack p_77513_1_, int p_77513_2_, boolean p_77513_3_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_77513_1_.getItem();
      int i = p_77513_1_.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         p_77513_2_ = p_77513_2_ + 1 + p_77513_0_.nextInt(i / 4 + 1) + p_77513_0_.nextInt(i / 4 + 1);
         float f = (p_77513_0_.nextFloat() + p_77513_0_.nextFloat() - 1.0F) * 0.15F;
         p_77513_2_ = MathHelper.clamp(Math.round((float)p_77513_2_ + (float)p_77513_2_ * f), 1, Integer.MAX_VALUE);
         List<EnchantmentData> list1 = getEnchantmentDatas(p_77513_2_, p_77513_1_, p_77513_3_);
         if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));

            while(p_77513_0_.nextInt(50) <= p_77513_2_) {
               removeIncompatible(list1, (EnchantmentData)Util.func_223378_a(list));
               if (list1.isEmpty()) {
                  break;
               }

               list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));
               p_77513_2_ /= 2;
            }
         }

         return list;
      }
   }

   public static void removeIncompatible(List<EnchantmentData> p_185282_0_, EnchantmentData p_185282_1_) {
      Iterator iterator = p_185282_0_.iterator();

      while(iterator.hasNext()) {
         if (!p_185282_1_.enchantment.isCompatibleWith(((EnchantmentData)iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean areAllCompatibleWith(Collection<Enchantment> p_201840_0_, Enchantment p_201840_1_) {
      Iterator var2 = p_201840_0_.iterator();

      Enchantment enchantment;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         enchantment = (Enchantment)var2.next();
      } while(enchantment.isCompatibleWith(p_201840_1_));

      return false;
   }

   public static List<EnchantmentData> getEnchantmentDatas(int p_185291_0_, ItemStack p_185291_1_, boolean p_185291_2_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_185291_1_.getItem();
      boolean flag = p_185291_1_.getItem() == Items.BOOK;
      Iterator var6 = Registry.ENCHANTMENT.iterator();

      while(true) {
         while(true) {
            Enchantment enchantment;
            do {
               do {
                  if (!var6.hasNext()) {
                     return list;
                  }

                  enchantment = (Enchantment)var6.next();
               } while(enchantment.isTreasureEnchantment() && !p_185291_2_);
            } while(!enchantment.canApplyAtEnchantingTable(p_185291_1_) && (!flag || !enchantment.isAllowedOnBooks()));

            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_185291_0_ >= enchantment.getMinEnchantability(i) && p_185291_0_ <= enchantment.getMaxEnchantability(i)) {
                  list.add(new EnchantmentData(enchantment, i));
                  break;
               }
            }
         }
      }
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment var1, int var2);
   }
}
