package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityAIDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private final Map<Integer, List<EntityAIDebugRenderer.Entry>> field_217685_b = Maps.newHashMap();

   public void func_217675_a() {
      this.field_217685_b.clear();
   }

   public void func_217682_a(int p_217682_1_, List<EntityAIDebugRenderer.Entry> p_217682_2_) {
      this.field_217685_b.put(p_217682_1_, p_217682_2_);
   }

   public EntityAIDebugRenderer(Minecraft p_i50977_1_) {
      this.client = p_i50977_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      ActiveRenderInfo lvt_9_1_ = this.client.gameRenderer.getActiveRenderInfo();
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos lvt_10_1_ = new BlockPos(lvt_9_1_.getProjectedView().x, 0.0D, lvt_9_1_.getProjectedView().z);
      this.field_217685_b.forEach((p_217683_1_, p_217683_2_) -> {
         for(int lvt_3_1_ = 0; lvt_3_1_ < p_217683_2_.size(); ++lvt_3_1_) {
            EntityAIDebugRenderer.Entry lvt_4_1_ = (EntityAIDebugRenderer.Entry)p_217683_2_.get(lvt_3_1_);
            if (lvt_10_1_.withinDistance(lvt_4_1_.field_217723_a, 160.0D)) {
               double lvt_5_1_ = (double)lvt_4_1_.field_217723_a.getX() + 0.5D;
               double lvt_7_1_ = (double)lvt_4_1_.field_217723_a.getY() + 2.0D + (double)lvt_3_1_ * 0.25D;
               double lvt_9_1_ = (double)lvt_4_1_.field_217723_a.getZ() + 0.5D;
               int lvt_11_1_ = lvt_4_1_.field_217726_d ? -16711936 : -3355444;
               DebugRenderer.func_217732_a(lvt_4_1_.field_217725_c, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_11_1_);
            }
         }

      });
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Entry {
      public final BlockPos field_217723_a;
      public final int field_217724_b;
      public final String field_217725_c;
      public final boolean field_217726_d;

      public Entry(BlockPos p_i50834_1_, int p_i50834_2_, String p_i50834_3_, boolean p_i50834_4_) {
         this.field_217723_a = p_i50834_1_;
         this.field_217724_b = p_i50834_2_;
         this.field_217725_c = p_i50834_3_;
         this.field_217726_d = p_i50834_4_;
      }
   }
}
