package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private Collection<BlockPos> field_222909_b = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft p_i51517_1_) {
      this.client = p_i51517_1_;
   }

   public void func_222906_a(Collection<BlockPos> p_222906_1_) {
      this.field_222909_b = p_222906_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      BlockPos lvt_9_1_ = this.func_222904_c().getBlockPos();
      Iterator var10 = this.field_222909_b.iterator();

      while(var10.hasNext()) {
         BlockPos lvt_11_1_ = (BlockPos)var10.next();
         if (lvt_9_1_.withinDistance(lvt_11_1_, 160.0D)) {
            func_222903_a(lvt_11_1_);
         }
      }

   }

   private static void func_222903_a(BlockPos p_222903_0_) {
      DebugRenderer.func_217735_a(p_222903_0_.add(-0.5D, -0.5D, -0.5D), p_222903_0_.add(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
      int lvt_1_1_ = -65536;
      func_222905_a("Raid center", p_222903_0_, -65536);
   }

   private static void func_222905_a(String p_222905_0_, BlockPos p_222905_1_, int p_222905_2_) {
      double lvt_3_1_ = (double)p_222905_1_.getX() + 0.5D;
      double lvt_5_1_ = (double)p_222905_1_.getY() + 1.3D;
      double lvt_7_1_ = (double)p_222905_1_.getZ() + 0.5D;
      DebugRenderer.func_217734_a(p_222905_0_, lvt_3_1_, lvt_5_1_, lvt_7_1_, p_222905_2_, 0.04F, true, 0.0F, true);
   }

   private ActiveRenderInfo func_222904_c() {
      return this.client.gameRenderer.getActiveRenderInfo();
   }
}
