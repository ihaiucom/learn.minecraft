package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<BlockPos, BlockPos> subCaves = Maps.newHashMap();
   private final Map<BlockPos, Float> sizes = Maps.newHashMap();
   private final List<BlockPos> caves = Lists.newArrayList();

   public void addCave(BlockPos p_201742_1_, List<BlockPos> p_201742_2_, List<Float> p_201742_3_) {
      for(int lvt_4_1_ = 0; lvt_4_1_ < p_201742_2_.size(); ++lvt_4_1_) {
         this.subCaves.put(p_201742_2_.get(lvt_4_1_), p_201742_1_);
         this.sizes.put(p_201742_2_.get(lvt_4_1_), p_201742_3_.get(lvt_4_1_));
      }

      this.caves.add(p_201742_1_);
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos lvt_9_1_ = new BlockPos(p_225619_3_, 0.0D, p_225619_7_);
      Tessellator lvt_10_1_ = Tessellator.getInstance();
      BufferBuilder lvt_11_1_ = lvt_10_1_.getBuffer();
      lvt_11_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);
      Iterator var12 = this.subCaves.entrySet().iterator();

      while(var12.hasNext()) {
         Entry<BlockPos, BlockPos> lvt_13_1_ = (Entry)var12.next();
         BlockPos lvt_14_1_ = (BlockPos)lvt_13_1_.getKey();
         BlockPos lvt_15_1_ = (BlockPos)lvt_13_1_.getValue();
         float lvt_16_1_ = (float)(lvt_15_1_.getX() * 128 % 256) / 256.0F;
         float lvt_17_1_ = (float)(lvt_15_1_.getY() * 128 % 256) / 256.0F;
         float lvt_18_1_ = (float)(lvt_15_1_.getZ() * 128 % 256) / 256.0F;
         float lvt_19_1_ = (Float)this.sizes.get(lvt_14_1_);
         if (lvt_9_1_.withinDistance(lvt_14_1_, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(lvt_11_1_, (double)((float)lvt_14_1_.getX() + 0.5F) - p_225619_3_ - (double)lvt_19_1_, (double)((float)lvt_14_1_.getY() + 0.5F) - p_225619_5_ - (double)lvt_19_1_, (double)((float)lvt_14_1_.getZ() + 0.5F) - p_225619_7_ - (double)lvt_19_1_, (double)((float)lvt_14_1_.getX() + 0.5F) - p_225619_3_ + (double)lvt_19_1_, (double)((float)lvt_14_1_.getY() + 0.5F) - p_225619_5_ + (double)lvt_19_1_, (double)((float)lvt_14_1_.getZ() + 0.5F) - p_225619_7_ + (double)lvt_19_1_, lvt_16_1_, lvt_17_1_, lvt_18_1_, 0.5F);
         }
      }

      var12 = this.caves.iterator();

      while(var12.hasNext()) {
         BlockPos lvt_13_2_ = (BlockPos)var12.next();
         if (lvt_9_1_.withinDistance(lvt_13_2_, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(lvt_11_1_, (double)lvt_13_2_.getX() - p_225619_3_, (double)lvt_13_2_.getY() - p_225619_5_, (double)lvt_13_2_.getZ() - p_225619_7_, (double)((float)lvt_13_2_.getX() + 1.0F) - p_225619_3_, (double)((float)lvt_13_2_.getY() + 1.0F) - p_225619_5_, (double)((float)lvt_13_2_.getZ() + 1.0F) - p_225619_7_, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      lvt_10_1_.draw();
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
