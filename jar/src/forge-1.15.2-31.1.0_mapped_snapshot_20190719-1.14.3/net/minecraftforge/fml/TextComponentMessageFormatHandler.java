package net.minecraftforge.fml;

import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TextComponentMessageFormatHandler {
   public static int handle(TranslationTextComponent parent, List<ITextComponent> children, Object[] formatArgs, String format) {
      try {
         StringTextComponent component = new StringTextComponent(ForgeI18n.parseFormat(format, formatArgs));
         component.getStyle().setParentStyle(parent.getStyle());
         children.add(component);
         return format.length();
      } catch (IllegalArgumentException var5) {
         return 0;
      }
   }
}
