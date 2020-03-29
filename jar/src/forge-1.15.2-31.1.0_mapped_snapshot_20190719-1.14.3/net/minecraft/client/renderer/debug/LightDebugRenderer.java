package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public LightDebugRenderer(Minecraft p_i48765_1_) {
      this.minecraft = p_i48765_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      World lvt_9_1_ = this.minecraft.world;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos lvt_10_1_ = new BlockPos(p_225619_3_, p_225619_5_, p_225619_7_);
      LongSet lvt_11_1_ = new LongOpenHashSet();
      Iterator var12 = BlockPos.getAllInBoxMutable(lvt_10_1_.add(-10, -10, -10), lvt_10_1_.add(10, 10, 10)).iterator();

      while(var12.hasNext()) {
         BlockPos lvt_13_1_ = (BlockPos)var12.next();
         int lvt_14_1_ = lvt_9_1_.func_226658_a_(LightType.SKY, lvt_13_1_);
         float lvt_15_1_ = (float)(15 - lvt_14_1_) / 15.0F * 0.5F + 0.16F;
         int lvt_16_1_ = MathHelper.hsvToRGB(lvt_15_1_, 0.9F, 0.9F);
         long lvt_17_1_ = SectionPos.worldToSection(lvt_13_1_.toLong());
         if (lvt_11_1_.add(lvt_17_1_)) {
            DebugRenderer.func_217729_a(lvt_9_1_.getChunkProvider().getLightManager().func_215572_a(LightType.SKY, SectionPos.from(lvt_17_1_)), (double)(SectionPos.extractX(lvt_17_1_) * 16 + 8), (double)(SectionPos.extractY(lvt_17_1_) * 16 + 8), (double)(SectionPos.extractZ(lvt_17_1_) * 16 + 8), 16711680, 0.3F);
         }

         if (lvt_14_1_ != 15) {
            DebugRenderer.func_217732_a(String.valueOf(lvt_14_1_), (double)lvt_13_1_.getX() + 0.5D, (double)lvt_13_1_.getY() + 0.25D, (double)lvt_13_1_.getZ() + 0.5D, lvt_16_1_);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
