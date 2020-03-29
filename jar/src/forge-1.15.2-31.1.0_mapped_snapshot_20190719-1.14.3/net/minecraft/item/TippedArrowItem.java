package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties p_i48457_1_) {
      super(p_i48457_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.POISON);
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         Iterator var3 = Registry.POTION.iterator();

         while(var3.hasNext()) {
            Potion lvt_4_1_ = (Potion)var3.next();
            if (!lvt_4_1_.getEffects().isEmpty()) {
               p_150895_2_.add(PotionUtils.addPotionToItemStack(new ItemStack(this), lvt_4_1_));
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 0.125F);
   }

   public String getTranslationKey(ItemStack p_77667_1_) {
      return PotionUtils.getPotionFromItem(p_77667_1_).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }
}
