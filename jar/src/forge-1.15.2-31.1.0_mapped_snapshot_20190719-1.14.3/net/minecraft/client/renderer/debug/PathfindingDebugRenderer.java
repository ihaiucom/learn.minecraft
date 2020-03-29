package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDistance = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();

   public void addPath(int p_188289_1_, Path p_188289_2_, float p_188289_3_) {
      this.pathMap.put(p_188289_1_, p_188289_2_);
      this.creationMap.put(p_188289_1_, Util.milliTime());
      this.pathMaxDistance.put(p_188289_1_, p_188289_3_);
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      if (!this.pathMap.isEmpty()) {
         long lvt_9_1_ = Util.milliTime();
         Iterator var11 = this.pathMap.keySet().iterator();

         while(var11.hasNext()) {
            Integer lvt_12_1_ = (Integer)var11.next();
            Path lvt_13_1_ = (Path)this.pathMap.get(lvt_12_1_);
            float lvt_14_1_ = (Float)this.pathMaxDistance.get(lvt_12_1_);
            func_229032_a_(lvt_13_1_, lvt_14_1_, true, true, p_225619_3_, p_225619_5_, p_225619_7_);
         }

         Integer[] var15 = (Integer[])this.creationMap.keySet().toArray(new Integer[0]);
         int var16 = var15.length;

         for(int var17 = 0; var17 < var16; ++var17) {
            Integer lvt_14_2_ = var15[var17];
            if (lvt_9_1_ - (Long)this.creationMap.get(lvt_14_2_) > 5000L) {
               this.pathMap.remove(lvt_14_2_);
               this.creationMap.remove(lvt_14_2_);
            }
         }

      }
   }

   public static void func_229032_a_(Path p_229032_0_, float p_229032_1_, boolean p_229032_2_, boolean p_229032_3_, double p_229032_4_, double p_229032_6_, double p_229032_8_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);
      func_229034_b_(p_229032_0_, p_229032_1_, p_229032_2_, p_229032_3_, p_229032_4_, p_229032_6_, p_229032_8_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
   }

   private static void func_229034_b_(Path p_229034_0_, float p_229034_1_, boolean p_229034_2_, boolean p_229034_3_, double p_229034_4_, double p_229034_6_, double p_229034_8_) {
      func_229031_a_(p_229034_0_, p_229034_4_, p_229034_6_, p_229034_8_);
      BlockPos lvt_10_1_ = p_229034_0_.func_224770_k();
      int lvt_11_2_;
      PathPoint lvt_12_2_;
      if (func_229033_a_(lvt_10_1_, p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0F) {
         DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)lvt_10_1_.getX() + 0.25F), (double)((float)lvt_10_1_.getY() + 0.25F), (double)lvt_10_1_.getZ() + 0.25D, (double)((float)lvt_10_1_.getX() + 0.75F), (double)((float)lvt_10_1_.getY() + 0.75F), (double)((float)lvt_10_1_.getZ() + 0.75F))).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 0.0F, 1.0F, 0.0F, 0.5F);

         for(lvt_11_2_ = 0; lvt_11_2_ < p_229034_0_.getCurrentPathLength(); ++lvt_11_2_) {
            lvt_12_2_ = p_229034_0_.getPathPointFromIndex(lvt_11_2_);
            if (func_229033_a_(lvt_12_2_.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0F) {
               float lvt_13_1_ = lvt_11_2_ == p_229034_0_.getCurrentPathIndex() ? 1.0F : 0.0F;
               float lvt_14_1_ = lvt_11_2_ == p_229034_0_.getCurrentPathIndex() ? 0.0F : 1.0F;
               DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)lvt_12_2_.x + 0.5F - p_229034_1_), (double)((float)lvt_12_2_.y + 0.01F * (float)lvt_11_2_), (double)((float)lvt_12_2_.z + 0.5F - p_229034_1_), (double)((float)lvt_12_2_.x + 0.5F + p_229034_1_), (double)((float)lvt_12_2_.y + 0.25F + 0.01F * (float)lvt_11_2_), (double)((float)lvt_12_2_.z + 0.5F + p_229034_1_))).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), lvt_13_1_, 0.0F, lvt_14_1_, 0.5F);
            }
         }
      }

      if (p_229034_2_) {
         PathPoint[] var15 = p_229034_0_.getClosedSet();
         int var16 = var15.length;

         int var17;
         PathPoint lvt_14_3_;
         for(var17 = 0; var17 < var16; ++var17) {
            lvt_14_3_ = var15[var17];
            if (func_229033_a_(lvt_14_3_.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0F) {
               DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)lvt_14_3_.x + 0.5F - p_229034_1_ / 2.0F), (double)((float)lvt_14_3_.y + 0.01F), (double)((float)lvt_14_3_.z + 0.5F - p_229034_1_ / 2.0F), (double)((float)lvt_14_3_.x + 0.5F + p_229034_1_ / 2.0F), (double)lvt_14_3_.y + 0.1D, (double)((float)lvt_14_3_.z + 0.5F + p_229034_1_ / 2.0F))).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 1.0F, 0.8F, 0.8F, 0.5F);
            }
         }

         var15 = p_229034_0_.getOpenSet();
         var16 = var15.length;

         for(var17 = 0; var17 < var16; ++var17) {
            lvt_14_3_ = var15[var17];
            if (func_229033_a_(lvt_14_3_.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0F) {
               DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)lvt_14_3_.x + 0.5F - p_229034_1_ / 2.0F), (double)((float)lvt_14_3_.y + 0.01F), (double)((float)lvt_14_3_.z + 0.5F - p_229034_1_ / 2.0F), (double)((float)lvt_14_3_.x + 0.5F + p_229034_1_ / 2.0F), (double)lvt_14_3_.y + 0.1D, (double)((float)lvt_14_3_.z + 0.5F + p_229034_1_ / 2.0F))).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 0.8F, 1.0F, 1.0F, 0.5F);
            }
         }
      }

      if (p_229034_3_) {
         for(lvt_11_2_ = 0; lvt_11_2_ < p_229034_0_.getCurrentPathLength(); ++lvt_11_2_) {
            lvt_12_2_ = p_229034_0_.getPathPointFromIndex(lvt_11_2_);
            if (func_229033_a_(lvt_12_2_.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0F) {
               DebugRenderer.func_217732_a(String.format("%s", lvt_12_2_.nodeType), (double)lvt_12_2_.x + 0.5D, (double)lvt_12_2_.y + 0.75D, (double)lvt_12_2_.z + 0.5D, -1);
               DebugRenderer.func_217732_a(String.format(Locale.ROOT, "%.2f", lvt_12_2_.costMalus), (double)lvt_12_2_.x + 0.5D, (double)lvt_12_2_.y + 0.25D, (double)lvt_12_2_.z + 0.5D, -1);
            }
         }
      }

   }

   public static void func_229031_a_(Path p_229031_0_, double p_229031_1_, double p_229031_3_, double p_229031_5_) {
      Tessellator lvt_7_1_ = Tessellator.getInstance();
      BufferBuilder lvt_8_1_ = lvt_7_1_.getBuffer();
      lvt_8_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);

      for(int lvt_9_1_ = 0; lvt_9_1_ < p_229031_0_.getCurrentPathLength(); ++lvt_9_1_) {
         PathPoint lvt_10_1_ = p_229031_0_.getPathPointFromIndex(lvt_9_1_);
         if (func_229033_a_(lvt_10_1_.func_224759_a(), p_229031_1_, p_229031_3_, p_229031_5_) <= 80.0F) {
            float lvt_11_1_ = (float)lvt_9_1_ / (float)p_229031_0_.getCurrentPathLength() * 0.33F;
            int lvt_12_1_ = lvt_9_1_ == 0 ? 0 : MathHelper.hsvToRGB(lvt_11_1_, 0.9F, 0.9F);
            int lvt_13_1_ = lvt_12_1_ >> 16 & 255;
            int lvt_14_1_ = lvt_12_1_ >> 8 & 255;
            int lvt_15_1_ = lvt_12_1_ & 255;
            lvt_8_1_.func_225582_a_((double)lvt_10_1_.x - p_229031_1_ + 0.5D, (double)lvt_10_1_.y - p_229031_3_ + 0.5D, (double)lvt_10_1_.z - p_229031_5_ + 0.5D).func_225586_a_(lvt_13_1_, lvt_14_1_, lvt_15_1_, 255).endVertex();
         }
      }

      lvt_7_1_.draw();
   }

   private static float func_229033_a_(BlockPos p_229033_0_, double p_229033_1_, double p_229033_3_, double p_229033_5_) {
      return (float)(Math.abs((double)p_229033_0_.getX() - p_229033_1_) + Math.abs((double)p_229033_0_.getY() - p_229033_3_) + Math.abs((double)p_229033_0_.getZ() - p_229033_5_));
   }
}
