package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] ID_MAPPING = (Difficulty[])Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((p_199928_0_) -> {
      return new Difficulty[p_199928_0_];
   });
   private final int id;
   private final String translationKey;

   private Difficulty(int p_i45312_3_, String p_i45312_4_) {
      this.id = p_i45312_3_;
      this.translationKey = p_i45312_4_;
   }

   public int getId() {
      return this.id;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("options.difficulty." + this.translationKey, new Object[0]);
   }

   public static Difficulty byId(int p_151523_0_) {
      return ID_MAPPING[p_151523_0_ % ID_MAPPING.length];
   }

   @Nullable
   public static Difficulty byName(String p_219963_0_) {
      Difficulty[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Difficulty lvt_4_1_ = var1[var3];
         if (lvt_4_1_.translationKey.equals(p_219963_0_)) {
            return lvt_4_1_;
         }
      }

      return null;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }
}
