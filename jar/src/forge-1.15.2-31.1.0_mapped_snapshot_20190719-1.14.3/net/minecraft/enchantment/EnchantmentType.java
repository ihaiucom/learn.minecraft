package net.minecraft.enchantment;

import java.util.function.Predicate;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraftforge.common.IExtensibleEnum;

public enum EnchantmentType implements IExtensibleEnum {
   ALL {
      public boolean canEnchantItem(Item p_77557_1_) {
         EnchantmentType[] var2 = EnchantmentType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnchantmentType enchantmenttype = var2[var4];
            if (enchantmenttype != EnchantmentType.ALL && enchantmenttype.canEnchantItem(p_77557_1_)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getEquipmentSlot() == EquipmentSlotType.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getEquipmentSlot() == EquipmentSlotType.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getEquipmentSlot() == EquipmentSlotType.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getEquipmentSlot() == EquipmentSlotType.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof ToolItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_.isDamageable();
      }
   },
   BOW {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof BowItem;
      }
   },
   WEARABLE {
      public boolean canEnchantItem(Item p_77557_1_) {
         Block block = Block.getBlockFromItem(p_77557_1_);
         return p_77557_1_ instanceof ArmorItem || p_77557_1_ instanceof ElytraItem || block instanceof AbstractSkullBlock || block instanceof CarvedPumpkinBlock;
      }
   },
   CROSSBOW {
      public boolean canEnchantItem(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem;
      }
   };

   private Predicate<Item> delegate;

   private EnchantmentType() {
   }

   private EnchantmentType(Predicate<Item> p_i230069_3_) {
      this.delegate = p_i230069_3_;
   }

   public static EnchantmentType create(String p_create_0_, Predicate<Item> p_create_1_) {
      throw new IllegalStateException("Enum not extended");
   }

   public boolean canEnchantItem(Item p_77557_1_) {
      return this.delegate == null ? false : this.delegate.test(p_77557_1_);
   }

   // $FF: synthetic method
   EnchantmentType(Object p_i47253_3_) {
      this();
   }
}
