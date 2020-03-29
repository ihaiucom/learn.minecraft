package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable {
   private final AtlasTexture textureAtlas;
   private final String field_229298_b_;

   public SpriteUploader(TextureManager p_i50905_1_, ResourceLocation p_i50905_2_, String p_i50905_3_) {
      this.field_229298_b_ = p_i50905_3_;
      this.textureAtlas = new AtlasTexture(p_i50905_2_);
      p_i50905_1_.func_229263_a_(this.textureAtlas.func_229223_g_(), this.textureAtlas);
   }

   protected abstract Stream<ResourceLocation> func_225640_a_();

   protected TextureAtlasSprite getSprite(ResourceLocation p_215282_1_) {
      return this.textureAtlas.getSprite(this.func_229299_b_(p_215282_1_));
   }

   private ResourceLocation func_229299_b_(ResourceLocation p_229299_1_) {
      return new ResourceLocation(p_229299_1_.getNamespace(), this.field_229298_b_ + "/" + p_229299_1_.getPath());
   }

   protected AtlasTexture.SheetData prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      p_212854_2_.startTick();
      p_212854_2_.startSection("stitching");
      AtlasTexture.SheetData lvt_3_1_ = this.textureAtlas.func_229220_a_(p_212854_1_, this.func_225640_a_().map(this::func_229299_b_), p_212854_2_, 0);
      p_212854_2_.endSection();
      p_212854_2_.endTick();
      return lvt_3_1_;
   }

   protected void apply(AtlasTexture.SheetData p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_3_.startTick();
      p_212853_3_.startSection("upload");
      this.textureAtlas.upload(p_212853_1_);
      p_212853_3_.endSection();
      p_212853_3_.endTick();
   }

   public void close() {
      this.textureAtlas.clear();
   }

   // $FF: synthetic method
   protected Object prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      return this.prepare(p_212854_1_, p_212854_2_);
   }
}
