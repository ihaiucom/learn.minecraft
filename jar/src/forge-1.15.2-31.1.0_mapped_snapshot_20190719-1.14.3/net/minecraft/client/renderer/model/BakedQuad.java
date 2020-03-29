package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import net.minecraftforge.client.model.pipeline.LightUtil;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements IVertexProducer {
   protected final int[] vertexData;
   protected final int tintIndex;
   protected final Direction face;
   protected final TextureAtlasSprite sprite;
   protected final boolean applyDiffuseLighting;

   /** @deprecated */
   @Deprecated
   public BakedQuad(int[] p_i46574_1_, int p_i46574_2_, Direction p_i46574_3_, TextureAtlasSprite p_i46574_4_) {
      this(p_i46574_1_, p_i46574_2_, p_i46574_3_, p_i46574_4_, true);
   }

   public BakedQuad(int[] p_i230090_1_, int p_i230090_2_, Direction p_i230090_3_, TextureAtlasSprite p_i230090_4_, boolean p_i230090_5_) {
      this.applyDiffuseLighting = p_i230090_5_;
      this.vertexData = p_i230090_1_;
      this.tintIndex = p_i230090_2_;
      this.face = p_i230090_3_;
      this.sprite = p_i230090_4_;
   }

   public int[] getVertexData() {
      return this.vertexData;
   }

   public boolean hasTintIndex() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getFace() {
      return this.face;
   }

   public void pipe(IVertexConsumer p_pipe_1_) {
      LightUtil.putBakedQuad(p_pipe_1_, this);
   }

   public TextureAtlasSprite getSprite() {
      return this.sprite;
   }

   public boolean shouldApplyDiffuseLighting() {
      return this.applyDiffuseLighting;
   }
}
