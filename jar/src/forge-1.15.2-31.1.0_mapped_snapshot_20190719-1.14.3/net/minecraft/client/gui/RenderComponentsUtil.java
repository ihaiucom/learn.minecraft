package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderComponentsUtil {
   public static String removeTextColorsIfConfigured(String p_178909_0_, boolean p_178909_1_) {
      return !p_178909_1_ && !Minecraft.getInstance().gameSettings.chatColor ? TextFormatting.getTextWithoutFormattingCodes(p_178909_0_) : p_178909_0_;
   }

   public static List<ITextComponent> splitText(ITextComponent p_178908_0_, int p_178908_1_, FontRenderer p_178908_2_, boolean p_178908_3_, boolean p_178908_4_) {
      int i = 0;
      ITextComponent itextcomponent = new StringTextComponent("");
      List<ITextComponent> list = Lists.newArrayList();
      List<ITextComponent> list1 = Lists.newArrayList(p_178908_0_);

      for(int j = 0; j < list1.size(); ++j) {
         ITextComponent itextcomponent1 = (ITextComponent)list1.get(j);
         String s = itextcomponent1.getUnformattedComponentText();
         boolean flag = false;
         String s5;
         if (s.contains("\n")) {
            int k = s.indexOf(10);
            s5 = s.substring(k + 1);
            s = s.substring(0, k + 1);
            ITextComponent itextcomponent2 = (new StringTextComponent(s5)).setStyle(itextcomponent1.getStyle().createShallowCopy());
            list1.add(j + 1, itextcomponent2);
            flag = true;
         }

         String s4 = removeTextColorsIfConfigured(itextcomponent1.getStyle().getFormattingCode() + s, p_178908_4_);
         s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
         int i1 = p_178908_2_.getStringWidth(s5);
         ITextComponent itextcomponent3 = (new StringTextComponent(s5)).setStyle(itextcomponent1.getStyle().createShallowCopy());
         if (i + i1 > p_178908_1_) {
            String s2 = p_178908_2_.trimStringToWidth(s4, p_178908_1_ - i, false);
            String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;
            if (s3 != null && !s3.isEmpty()) {
               int l = s3.charAt(0) != ' ' ? s2.lastIndexOf(32) : s2.length();
               if (l >= 0 && p_178908_2_.getStringWidth(s4.substring(0, l)) > 0) {
                  s2 = s4.substring(0, l);
                  if (p_178908_3_) {
                     ++l;
                  }

                  s3 = s4.substring(l);
               } else if (i > 0 && !s4.contains(" ")) {
                  s2 = "";
                  s3 = s4;
               }

               s3 = TextFormatting.getFormatString(s2) + s3;
               ITextComponent itextcomponent4 = (new StringTextComponent(s3)).setStyle(itextcomponent1.getStyle().createShallowCopy());
               list1.add(j + 1, itextcomponent4);
            }

            i1 = p_178908_2_.getStringWidth(s2);
            itextcomponent3 = new StringTextComponent(s2);
            ((ITextComponent)itextcomponent3).setStyle(itextcomponent1.getStyle().createShallowCopy());
            flag = true;
         }

         if (i + i1 <= p_178908_1_) {
            i += i1;
            itextcomponent.appendSibling((ITextComponent)itextcomponent3);
         } else {
            flag = true;
         }

         if (flag) {
            list.add(itextcomponent);
            i = 0;
            itextcomponent = new StringTextComponent("");
         }
      }

      list.add(itextcomponent);
      return list;
   }
}
