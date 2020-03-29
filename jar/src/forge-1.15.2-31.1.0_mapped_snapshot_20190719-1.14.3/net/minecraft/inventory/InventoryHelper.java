package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();

   public static void dropInventoryItems(World p_180175_0_, BlockPos p_180175_1_, IInventory p_180175_2_) {
      dropInventoryItems(p_180175_0_, (double)p_180175_1_.getX(), (double)p_180175_1_.getY(), (double)p_180175_1_.getZ(), p_180175_2_);
   }

   public static void dropInventoryItems(World p_180176_0_, Entity p_180176_1_, IInventory p_180176_2_) {
      dropInventoryItems(p_180176_0_, p_180176_1_.func_226277_ct_(), p_180176_1_.func_226278_cu_(), p_180176_1_.func_226281_cx_(), p_180176_2_);
   }

   private static void dropInventoryItems(World p_180174_0_, double p_180174_1_, double p_180174_3_, double p_180174_5_, IInventory p_180174_7_) {
      for(int lvt_8_1_ = 0; lvt_8_1_ < p_180174_7_.getSizeInventory(); ++lvt_8_1_) {
         spawnItemStack(p_180174_0_, p_180174_1_, p_180174_3_, p_180174_5_, p_180174_7_.getStackInSlot(lvt_8_1_));
      }

   }

   public static void dropItems(World p_219961_0_, BlockPos p_219961_1_, NonNullList<ItemStack> p_219961_2_) {
      p_219961_2_.forEach((p_219962_2_) -> {
         spawnItemStack(p_219961_0_, (double)p_219961_1_.getX(), (double)p_219961_1_.getY(), (double)p_219961_1_.getZ(), p_219962_2_);
      });
   }

   public static void spawnItemStack(World p_180173_0_, double p_180173_1_, double p_180173_3_, double p_180173_5_, ItemStack p_180173_7_) {
      double lvt_8_1_ = (double)EntityType.ITEM.getWidth();
      double lvt_10_1_ = 1.0D - lvt_8_1_;
      double lvt_12_1_ = lvt_8_1_ / 2.0D;
      double lvt_14_1_ = Math.floor(p_180173_1_) + RANDOM.nextDouble() * lvt_10_1_ + lvt_12_1_;
      double lvt_16_1_ = Math.floor(p_180173_3_) + RANDOM.nextDouble() * lvt_10_1_;
      double lvt_18_1_ = Math.floor(p_180173_5_) + RANDOM.nextDouble() * lvt_10_1_ + lvt_12_1_;

      while(!p_180173_7_.isEmpty()) {
         ItemEntity lvt_20_1_ = new ItemEntity(p_180173_0_, lvt_14_1_, lvt_16_1_, lvt_18_1_, p_180173_7_.split(RANDOM.nextInt(21) + 10));
         float lvt_21_1_ = 0.05F;
         lvt_20_1_.setMotion(RANDOM.nextGaussian() * 0.05000000074505806D, RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, RANDOM.nextGaussian() * 0.05000000074505806D);
         p_180173_0_.addEntity(lvt_20_1_);
      }

   }
}
