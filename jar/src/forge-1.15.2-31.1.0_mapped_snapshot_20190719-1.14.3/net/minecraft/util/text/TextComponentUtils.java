package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent mergeStyles(ITextComponent p_211401_0_, Style p_211401_1_) {
      if (p_211401_1_.isEmpty()) {
         return p_211401_0_;
      } else {
         return p_211401_0_.getStyle().isEmpty() ? p_211401_0_.setStyle(p_211401_1_.createShallowCopy()) : (new StringTextComponent("")).appendSibling(p_211401_0_).setStyle(p_211401_1_.createShallowCopy());
      }
   }

   public static ITextComponent updateForEntity(@Nullable CommandSource p_197680_0_, ITextComponent p_197680_1_, @Nullable Entity p_197680_2_, int p_197680_3_) throws CommandSyntaxException {
      if (p_197680_3_ > 100) {
         return p_197680_1_;
      } else {
         ++p_197680_3_;
         ITextComponent lvt_4_1_ = p_197680_1_ instanceof ITargetedTextComponent ? ((ITargetedTextComponent)p_197680_1_).createNames(p_197680_0_, p_197680_2_, p_197680_3_) : p_197680_1_.shallowCopy();
         Iterator var5 = p_197680_1_.getSiblings().iterator();

         while(var5.hasNext()) {
            ITextComponent lvt_6_1_ = (ITextComponent)var5.next();
            lvt_4_1_.appendSibling(updateForEntity(p_197680_0_, lvt_6_1_, p_197680_2_, p_197680_3_));
         }

         return mergeStyles(lvt_4_1_, p_197680_1_.getStyle());
      }
   }

   public static ITextComponent getDisplayName(GameProfile p_197679_0_) {
      if (p_197679_0_.getName() != null) {
         return new StringTextComponent(p_197679_0_.getName());
      } else {
         return p_197679_0_.getId() != null ? new StringTextComponent(p_197679_0_.getId().toString()) : new StringTextComponent("(unknown)");
      }
   }

   public static ITextComponent makeGreenSortedList(Collection<String> p_197678_0_) {
      return makeSortedList(p_197678_0_, (p_197681_0_) -> {
         return (new StringTextComponent(p_197681_0_)).applyTextStyle(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent makeSortedList(Collection<T> p_197675_0_, Function<T, ITextComponent> p_197675_1_) {
      if (p_197675_0_.isEmpty()) {
         return new StringTextComponent("");
      } else if (p_197675_0_.size() == 1) {
         return (ITextComponent)p_197675_1_.apply(p_197675_0_.iterator().next());
      } else {
         List<T> lvt_2_1_ = Lists.newArrayList(p_197675_0_);
         lvt_2_1_.sort(Comparable::compareTo);
         return makeList(lvt_2_1_, p_197675_1_);
      }
   }

   public static <T> ITextComponent makeList(Collection<T> p_197677_0_, Function<T, ITextComponent> p_197677_1_) {
      if (p_197677_0_.isEmpty()) {
         return new StringTextComponent("");
      } else if (p_197677_0_.size() == 1) {
         return (ITextComponent)p_197677_1_.apply(p_197677_0_.iterator().next());
      } else {
         ITextComponent lvt_2_1_ = new StringTextComponent("");
         boolean lvt_3_1_ = true;

         for(Iterator var4 = p_197677_0_.iterator(); var4.hasNext(); lvt_3_1_ = false) {
            T lvt_5_1_ = var4.next();
            if (!lvt_3_1_) {
               lvt_2_1_.appendSibling((new StringTextComponent(", ")).applyTextStyle(TextFormatting.GRAY));
            }

            lvt_2_1_.appendSibling((ITextComponent)p_197677_1_.apply(lvt_5_1_));
         }

         return lvt_2_1_;
      }
   }

   public static ITextComponent wrapInSquareBrackets(ITextComponent p_197676_0_) {
      return (new StringTextComponent("[")).appendSibling(p_197676_0_).appendText("]");
   }

   public static ITextComponent toTextComponent(Message p_202465_0_) {
      return (ITextComponent)(p_202465_0_ instanceof ITextComponent ? (ITextComponent)p_202465_0_ : new StringTextComponent(p_202465_0_.getString()));
   }
}
