package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   public static List<EffectInstance> getEffectsFromStack(ItemStack p_185189_0_) {
      return getEffectsFromTag(p_185189_0_.getTag());
   }

   public static List<EffectInstance> mergeEffects(Potion p_185186_0_, Collection<EffectInstance> p_185186_1_) {
      List<EffectInstance> lvt_2_1_ = Lists.newArrayList();
      lvt_2_1_.addAll(p_185186_0_.getEffects());
      lvt_2_1_.addAll(p_185186_1_);
      return lvt_2_1_;
   }

   public static List<EffectInstance> getEffectsFromTag(@Nullable CompoundNBT p_185185_0_) {
      List<EffectInstance> lvt_1_1_ = Lists.newArrayList();
      lvt_1_1_.addAll(getPotionTypeFromNBT(p_185185_0_).getEffects());
      addCustomPotionEffectToList(p_185185_0_, lvt_1_1_);
      return lvt_1_1_;
   }

   public static List<EffectInstance> getFullEffectsFromItem(ItemStack p_185190_0_) {
      return getFullEffectsFromTag(p_185190_0_.getTag());
   }

   public static List<EffectInstance> getFullEffectsFromTag(@Nullable CompoundNBT p_185192_0_) {
      List<EffectInstance> lvt_1_1_ = Lists.newArrayList();
      addCustomPotionEffectToList(p_185192_0_, lvt_1_1_);
      return lvt_1_1_;
   }

   public static void addCustomPotionEffectToList(@Nullable CompoundNBT p_185193_0_, List<EffectInstance> p_185193_1_) {
      if (p_185193_0_ != null && p_185193_0_.contains("CustomPotionEffects", 9)) {
         ListNBT lvt_2_1_ = p_185193_0_.getList("CustomPotionEffects", 10);

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
            CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
            EffectInstance lvt_5_1_ = EffectInstance.read(lvt_4_1_);
            if (lvt_5_1_ != null) {
               p_185193_1_.add(lvt_5_1_);
            }
         }
      }

   }

   public static int getColor(ItemStack p_190932_0_) {
      CompoundNBT lvt_1_1_ = p_190932_0_.getTag();
      if (lvt_1_1_ != null && lvt_1_1_.contains("CustomPotionColor", 99)) {
         return lvt_1_1_.getInt("CustomPotionColor");
      } else {
         return getPotionFromItem(p_190932_0_) == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(getEffectsFromStack(p_190932_0_));
      }
   }

   public static int getPotionColor(Potion p_185183_0_) {
      return p_185183_0_ == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(p_185183_0_.getEffects());
   }

   public static int getPotionColorFromEffectList(Collection<EffectInstance> p_185181_0_) {
      int lvt_1_1_ = 3694022;
      if (p_185181_0_.isEmpty()) {
         return 3694022;
      } else {
         float lvt_2_1_ = 0.0F;
         float lvt_3_1_ = 0.0F;
         float lvt_4_1_ = 0.0F;
         int lvt_5_1_ = 0;
         Iterator var6 = p_185181_0_.iterator();

         while(var6.hasNext()) {
            EffectInstance lvt_7_1_ = (EffectInstance)var6.next();
            if (lvt_7_1_.doesShowParticles()) {
               int lvt_8_1_ = lvt_7_1_.getPotion().getLiquidColor();
               int lvt_9_1_ = lvt_7_1_.getAmplifier() + 1;
               lvt_2_1_ += (float)(lvt_9_1_ * (lvt_8_1_ >> 16 & 255)) / 255.0F;
               lvt_3_1_ += (float)(lvt_9_1_ * (lvt_8_1_ >> 8 & 255)) / 255.0F;
               lvt_4_1_ += (float)(lvt_9_1_ * (lvt_8_1_ >> 0 & 255)) / 255.0F;
               lvt_5_1_ += lvt_9_1_;
            }
         }

         if (lvt_5_1_ == 0) {
            return 0;
         } else {
            lvt_2_1_ = lvt_2_1_ / (float)lvt_5_1_ * 255.0F;
            lvt_3_1_ = lvt_3_1_ / (float)lvt_5_1_ * 255.0F;
            lvt_4_1_ = lvt_4_1_ / (float)lvt_5_1_ * 255.0F;
            return (int)lvt_2_1_ << 16 | (int)lvt_3_1_ << 8 | (int)lvt_4_1_;
         }
      }
   }

   public static Potion getPotionFromItem(ItemStack p_185191_0_) {
      return getPotionTypeFromNBT(p_185191_0_.getTag());
   }

   public static Potion getPotionTypeFromNBT(@Nullable CompoundNBT p_185187_0_) {
      return p_185187_0_ == null ? Potions.EMPTY : Potion.getPotionTypeForName(p_185187_0_.getString("Potion"));
   }

   public static ItemStack addPotionToItemStack(ItemStack p_185188_0_, Potion p_185188_1_) {
      ResourceLocation lvt_2_1_ = Registry.POTION.getKey(p_185188_1_);
      if (p_185188_1_ == Potions.EMPTY) {
         p_185188_0_.removeChildTag("Potion");
      } else {
         p_185188_0_.getOrCreateTag().putString("Potion", lvt_2_1_.toString());
      }

      return p_185188_0_;
   }

   public static ItemStack appendEffects(ItemStack p_185184_0_, Collection<EffectInstance> p_185184_1_) {
      if (p_185184_1_.isEmpty()) {
         return p_185184_0_;
      } else {
         CompoundNBT lvt_2_1_ = p_185184_0_.getOrCreateTag();
         ListNBT lvt_3_1_ = lvt_2_1_.getList("CustomPotionEffects", 9);
         Iterator var4 = p_185184_1_.iterator();

         while(var4.hasNext()) {
            EffectInstance lvt_5_1_ = (EffectInstance)var4.next();
            lvt_3_1_.add(lvt_5_1_.write(new CompoundNBT()));
         }

         lvt_2_1_.put("CustomPotionEffects", lvt_3_1_);
         return p_185184_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack p_185182_0_, List<ITextComponent> p_185182_1_, float p_185182_2_) {
      List<EffectInstance> lvt_3_1_ = getEffectsFromStack(p_185182_0_);
      List<Tuple<String, AttributeModifier>> lvt_4_1_ = Lists.newArrayList();
      Iterator var5;
      TranslationTextComponent lvt_7_1_;
      Effect lvt_8_1_;
      if (lvt_3_1_.isEmpty()) {
         p_185182_1_.add((new TranslationTextComponent("effect.none", new Object[0])).applyTextStyle(TextFormatting.GRAY));
      } else {
         for(var5 = lvt_3_1_.iterator(); var5.hasNext(); p_185182_1_.add(lvt_7_1_.applyTextStyle(lvt_8_1_.getEffectType().getColor()))) {
            EffectInstance lvt_6_1_ = (EffectInstance)var5.next();
            lvt_7_1_ = new TranslationTextComponent(lvt_6_1_.getEffectName(), new Object[0]);
            lvt_8_1_ = lvt_6_1_.getPotion();
            Map<IAttribute, AttributeModifier> lvt_9_1_ = lvt_8_1_.getAttributeModifierMap();
            if (!lvt_9_1_.isEmpty()) {
               Iterator var10 = lvt_9_1_.entrySet().iterator();

               while(var10.hasNext()) {
                  Entry<IAttribute, AttributeModifier> lvt_11_1_ = (Entry)var10.next();
                  AttributeModifier lvt_12_1_ = (AttributeModifier)lvt_11_1_.getValue();
                  AttributeModifier lvt_13_1_ = new AttributeModifier(lvt_12_1_.getName(), lvt_8_1_.getAttributeModifierAmount(lvt_6_1_.getAmplifier(), lvt_12_1_), lvt_12_1_.getOperation());
                  lvt_4_1_.add(new Tuple(((IAttribute)lvt_11_1_.getKey()).getName(), lvt_13_1_));
               }
            }

            if (lvt_6_1_.getAmplifier() > 0) {
               lvt_7_1_.appendText(" ").appendSibling(new TranslationTextComponent("potion.potency." + lvt_6_1_.getAmplifier(), new Object[0]));
            }

            if (lvt_6_1_.getDuration() > 20) {
               lvt_7_1_.appendText(" (").appendText(EffectUtils.getPotionDurationString(lvt_6_1_, p_185182_2_)).appendText(")");
            }
         }
      }

      if (!lvt_4_1_.isEmpty()) {
         p_185182_1_.add(new StringTextComponent(""));
         p_185182_1_.add((new TranslationTextComponent("potion.whenDrank", new Object[0])).applyTextStyle(TextFormatting.DARK_PURPLE));
         var5 = lvt_4_1_.iterator();

         while(var5.hasNext()) {
            Tuple<String, AttributeModifier> lvt_6_2_ = (Tuple)var5.next();
            AttributeModifier lvt_7_2_ = (AttributeModifier)lvt_6_2_.getB();
            double lvt_8_2_ = lvt_7_2_.getAmount();
            double lvt_10_2_;
            if (lvt_7_2_.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && lvt_7_2_.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               lvt_10_2_ = lvt_7_2_.getAmount();
            } else {
               lvt_10_2_ = lvt_7_2_.getAmount() * 100.0D;
            }

            if (lvt_8_2_ > 0.0D) {
               p_185182_1_.add((new TranslationTextComponent("attribute.modifier.plus." + lvt_7_2_.getOperation().getId(), new Object[]{ItemStack.DECIMALFORMAT.format(lvt_10_2_), new TranslationTextComponent("attribute.name." + (String)lvt_6_2_.getA(), new Object[0])})).applyTextStyle(TextFormatting.BLUE));
            } else if (lvt_8_2_ < 0.0D) {
               lvt_10_2_ *= -1.0D;
               p_185182_1_.add((new TranslationTextComponent("attribute.modifier.take." + lvt_7_2_.getOperation().getId(), new Object[]{ItemStack.DECIMALFORMAT.format(lvt_10_2_), new TranslationTextComponent("attribute.name." + (String)lvt_6_2_.getA(), new Object[0])})).applyTextStyle(TextFormatting.RED));
            }
         }
      }

   }
}
