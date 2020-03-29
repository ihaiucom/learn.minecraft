package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DefaultGlyph implements IGlyphInfo {
   INSTANCE;

   private static final NativeImage NATIVE_IMAGE = (NativeImage)Util.make(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (p_211580_0_) -> {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 8; ++lvt_1_1_) {
         for(int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_) {
            boolean lvt_3_1_ = lvt_2_1_ == 0 || lvt_2_1_ + 1 == 5 || lvt_1_1_ == 0 || lvt_1_1_ + 1 == 8;
            p_211580_0_.setPixelRGBA(lvt_2_1_, lvt_1_1_, lvt_3_1_ ? -1 : 0);
         }
      }

      p_211580_0_.untrack();
   });

   public int getWidth() {
      return 5;
   }

   public int getHeight() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float getOversample() {
      return 1.0F;
   }

   public void uploadGlyph(int p_211573_1_, int p_211573_2_) {
      NATIVE_IMAGE.uploadTextureSub(0, p_211573_1_, p_211573_2_, false);
   }

   public boolean isColored() {
      return true;
   }
}
