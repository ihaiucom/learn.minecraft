package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Item.Properties p_i48505_1_) {
      super(p_i48505_1_);
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return false;
   }

   public static ListNBT getEnchantments(ItemStack p_92110_0_) {
      CompoundNBT lvt_1_1_ = p_92110_0_.getTag();
      return lvt_1_1_ != null ? lvt_1_1_.getList("StoredEnchantments", 10) : new ListNBT();
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      ItemStack.addEnchantmentTooltips(p_77624_3_, getEnchantments(p_77624_1_));
   }

   public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData p_92115_1_) {
      ListNBT lvt_2_1_ = getEnchantments(p_92115_0_);
      boolean lvt_3_1_ = true;
      ResourceLocation lvt_4_1_ = Registry.ENCHANTMENT.getKey(p_92115_1_.enchantment);

      for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_2_1_.size(); ++lvt_5_1_) {
         CompoundNBT lvt_6_1_ = lvt_2_1_.getCompound(lvt_5_1_);
         ResourceLocation lvt_7_1_ = ResourceLocation.tryCreate(lvt_6_1_.getString("id"));
         if (lvt_7_1_ != null && lvt_7_1_.equals(lvt_4_1_)) {
            if (lvt_6_1_.getInt("lvl") < p_92115_1_.enchantmentLevel) {
               lvt_6_1_.putShort("lvl", (short)p_92115_1_.enchantmentLevel);
            }

            lvt_3_1_ = false;
            break;
         }
      }

      if (lvt_3_1_) {
         CompoundNBT lvt_5_2_ = new CompoundNBT();
         lvt_5_2_.putString("id", String.valueOf(lvt_4_1_));
         lvt_5_2_.putShort("lvl", (short)p_92115_1_.enchantmentLevel);
         lvt_2_1_.add(lvt_5_2_);
      }

      p_92115_0_.getOrCreateTag().put("StoredEnchantments", lvt_2_1_);
   }

   public static ItemStack getEnchantedItemStack(EnchantmentData p_92111_0_) {
      ItemStack lvt_1_1_ = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(lvt_1_1_, p_92111_0_);
      return lvt_1_1_;
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      Iterator var3;
      Enchantment lvt_4_1_;
      if (p_150895_1_ == ItemGroup.SEARCH) {
         var3 = Registry.ENCHANTMENT.iterator();

         while(true) {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               lvt_4_1_ = (Enchantment)var3.next();
            } while(lvt_4_1_.type == null);

            for(int lvt_5_1_ = lvt_4_1_.getMinLevel(); lvt_5_1_ <= lvt_4_1_.getMaxLevel(); ++lvt_5_1_) {
               p_150895_2_.add(getEnchantedItemStack(new EnchantmentData(lvt_4_1_, lvt_5_1_)));
            }
         }
      } else if (p_150895_1_.getRelevantEnchantmentTypes().length != 0) {
         var3 = Registry.ENCHANTMENT.iterator();

         while(var3.hasNext()) {
            lvt_4_1_ = (Enchantment)var3.next();
            if (p_150895_1_.hasRelevantEnchantmentType(lvt_4_1_.type)) {
               p_150895_2_.add(getEnchantedItemStack(new EnchantmentData(lvt_4_1_, lvt_4_1_.getMaxLevel())));
            }
         }
      }

   }
}
