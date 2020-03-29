package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties p_i48496_1_) {
      super(p_i48496_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      CompoundNBT lvt_5_1_ = p_77624_1_.getChildTag("Explosion");
      if (lvt_5_1_ != null) {
         func_195967_a(lvt_5_1_, p_77624_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void func_195967_a(CompoundNBT p_195967_0_, List<ITextComponent> p_195967_1_) {
      FireworkRocketItem.Shape lvt_2_1_ = FireworkRocketItem.Shape.func_196070_a(p_195967_0_.getByte("Type"));
      p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.shape." + lvt_2_1_.func_196068_b(), new Object[0])).applyTextStyle(TextFormatting.GRAY));
      int[] lvt_3_1_ = p_195967_0_.getIntArray("Colors");
      if (lvt_3_1_.length > 0) {
         p_195967_1_.add(func_200298_a((new StringTextComponent("")).applyTextStyle(TextFormatting.GRAY), lvt_3_1_));
      }

      int[] lvt_4_1_ = p_195967_0_.getIntArray("FadeColors");
      if (lvt_4_1_.length > 0) {
         p_195967_1_.add(func_200298_a((new TranslationTextComponent("item.minecraft.firework_star.fade_to", new Object[0])).appendText(" ").applyTextStyle(TextFormatting.GRAY), lvt_4_1_));
      }

      if (p_195967_0_.getBoolean("Trail")) {
         p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.trail", new Object[0])).applyTextStyle(TextFormatting.GRAY));
      }

      if (p_195967_0_.getBoolean("Flicker")) {
         p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.flicker", new Object[0])).applyTextStyle(TextFormatting.GRAY));
      }

   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent func_200298_a(ITextComponent p_200298_0_, int[] p_200298_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_200298_1_.length; ++lvt_2_1_) {
         if (lvt_2_1_ > 0) {
            p_200298_0_.appendText(", ");
         }

         p_200298_0_.appendSibling(func_200297_a(p_200298_1_[lvt_2_1_]));
      }

      return p_200298_0_;
   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent func_200297_a(int p_200297_0_) {
      DyeColor lvt_1_1_ = DyeColor.byFireworkColor(p_200297_0_);
      return lvt_1_1_ == null ? new TranslationTextComponent("item.minecraft.firework_star.custom_color", new Object[0]) : new TranslationTextComponent("item.minecraft.firework_star." + lvt_1_1_.getTranslationKey(), new Object[0]);
   }
}
