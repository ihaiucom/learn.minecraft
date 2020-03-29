package net.minecraft.item;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IExtensibleEnum;

public enum Rarity implements IExtensibleEnum {
   COMMON(TextFormatting.WHITE),
   UNCOMMON(TextFormatting.YELLOW),
   RARE(TextFormatting.AQUA),
   EPIC(TextFormatting.LIGHT_PURPLE);

   public final TextFormatting color;

   private Rarity(TextFormatting p_i48837_3_) {
      this.color = p_i48837_3_;
   }

   public static Rarity create(String p_create_0_, TextFormatting p_create_1_) {
      throw new IllegalStateException("Enum not extended");
   }
}
