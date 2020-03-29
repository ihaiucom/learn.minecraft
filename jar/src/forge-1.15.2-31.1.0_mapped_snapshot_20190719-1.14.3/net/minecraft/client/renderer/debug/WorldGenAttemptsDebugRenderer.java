package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldGenAttemptsDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final List<BlockPos> locations = Lists.newArrayList();
   private final List<Float> sizes = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public void addAttempt(BlockPos p_201734_1_, float p_201734_2_, float p_201734_3_, float p_201734_4_, float p_201734_5_, float p_201734_6_) {
      this.locations.add(p_201734_1_);
      this.sizes.add(p_201734_2_);
      this.alphas.add(p_201734_6_);
      this.reds.add(p_201734_3_);
      this.greens.add(p_201734_4_);
      this.blues.add(p_201734_5_);
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      Tessellator lvt_9_1_ = Tessellator.getInstance();
      BufferBuilder lvt_10_1_ = lvt_9_1_.getBuffer();
      lvt_10_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int lvt_11_1_ = 0; lvt_11_1_ < this.locations.size(); ++lvt_11_1_) {
         BlockPos lvt_12_1_ = (BlockPos)this.locations.get(lvt_11_1_);
         Float lvt_13_1_ = (Float)this.sizes.get(lvt_11_1_);
         float lvt_14_1_ = lvt_13_1_ / 2.0F;
         WorldRenderer.addChainedFilledBoxVertices(lvt_10_1_, (double)((float)lvt_12_1_.getX() + 0.5F - lvt_14_1_) - p_225619_3_, (double)((float)lvt_12_1_.getY() + 0.5F - lvt_14_1_) - p_225619_5_, (double)((float)lvt_12_1_.getZ() + 0.5F - lvt_14_1_) - p_225619_7_, (double)((float)lvt_12_1_.getX() + 0.5F + lvt_14_1_) - p_225619_3_, (double)((float)lvt_12_1_.getY() + 0.5F + lvt_14_1_) - p_225619_5_, (double)((float)lvt_12_1_.getZ() + 0.5F + lvt_14_1_) - p_225619_7_, (Float)this.reds.get(lvt_11_1_), (Float)this.greens.get(lvt_11_1_), (Float)this.blues.get(lvt_11_1_), (Float)this.alphas.get(lvt_11_1_));
      }

      lvt_9_1_.draw();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
