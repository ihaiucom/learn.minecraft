package net.minecraft.entity.player;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum PlayerModelPart {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   private final int partId;
   private final int partMask;
   private final String partName;
   private final ITextComponent name;

   private PlayerModelPart(int p_i45809_3_, String p_i45809_4_) {
      this.partId = p_i45809_3_;
      this.partMask = 1 << p_i45809_3_;
      this.partName = p_i45809_4_;
      this.name = new TranslationTextComponent("options.modelPart." + p_i45809_4_, new Object[0]);
   }

   public int getPartMask() {
      return this.partMask;
   }

   public String getPartName() {
      return this.partName;
   }

   public ITextComponent getName() {
      return this.name;
   }
}
