package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MissingTextureSprite extends TextureAtlasSprite {
   private static final ResourceLocation LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture dynamicTexture;
   private static final LazyValue<NativeImage> IMAGE = new LazyValue(() -> {
      NativeImage lvt_0_1_ = new NativeImage(16, 16, false);
      int lvt_1_1_ = -16777216;
      int lvt_2_1_ = -524040;

      for(int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_) {
         for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {
            if (lvt_3_1_ < 8 ^ lvt_4_1_ < 8) {
               lvt_0_1_.setPixelRGBA(lvt_4_1_, lvt_3_1_, -524040);
            } else {
               lvt_0_1_.setPixelRGBA(lvt_4_1_, lvt_3_1_, -16777216);
            }
         }
      }

      lvt_0_1_.untrack();
      return lvt_0_1_;
   });
   private static final TextureAtlasSprite.Info field_229175_e_;

   private MissingTextureSprite(AtlasTexture p_i226044_1_, int p_i226044_2_, int p_i226044_3_, int p_i226044_4_, int p_i226044_5_, int p_i226044_6_) {
      super(p_i226044_1_, field_229175_e_, p_i226044_2_, p_i226044_3_, p_i226044_4_, p_i226044_5_, p_i226044_6_, (NativeImage)IMAGE.getValue());
   }

   public static MissingTextureSprite func_229176_a_(AtlasTexture p_229176_0_, int p_229176_1_, int p_229176_2_, int p_229176_3_, int p_229176_4_, int p_229176_5_) {
      return new MissingTextureSprite(p_229176_0_, p_229176_1_, p_229176_2_, p_229176_3_, p_229176_4_, p_229176_5_);
   }

   public static ResourceLocation getLocation() {
      return LOCATION;
   }

   public static TextureAtlasSprite.Info func_229177_b_() {
      return field_229175_e_;
   }

   public void close() {
      for(int lvt_1_1_ = 1; lvt_1_1_ < this.frames.length; ++lvt_1_1_) {
         this.frames[lvt_1_1_].close();
      }

   }

   public static DynamicTexture getDynamicTexture() {
      if (dynamicTexture == null) {
         dynamicTexture = new DynamicTexture((NativeImage)IMAGE.getValue());
         Minecraft.getInstance().getTextureManager().func_229263_a_(LOCATION, dynamicTexture);
      }

      return dynamicTexture;
   }

   static {
      field_229175_e_ = new TextureAtlasSprite.Info(LOCATION, 16, 16, new AnimationMetadataSection(Lists.newArrayList(new AnimationFrame[]{new AnimationFrame(0, -1)}), 16, 16, 1, false));
   }
}
