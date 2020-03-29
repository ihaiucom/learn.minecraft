package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeightMapDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapDebugRenderer(Minecraft p_i47133_1_) {
      this.minecraft = p_i47133_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      IWorld lvt_9_1_ = this.minecraft.world;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos lvt_10_1_ = new BlockPos(p_225619_3_, 0.0D, p_225619_7_);
      Tessellator lvt_11_1_ = Tessellator.getInstance();
      BufferBuilder lvt_12_1_ = lvt_11_1_.getBuffer();
      lvt_12_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);
      Iterator var13 = BlockPos.getAllInBoxMutable(lvt_10_1_.add(-40, 0, -40), lvt_10_1_.add(40, 0, 40)).iterator();

      while(var13.hasNext()) {
         BlockPos lvt_14_1_ = (BlockPos)var13.next();
         int lvt_15_1_ = lvt_9_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, lvt_14_1_.getX(), lvt_14_1_.getZ());
         if (lvt_9_1_.getBlockState(lvt_14_1_.add(0, lvt_15_1_, 0).down()).isAir()) {
            WorldRenderer.addChainedFilledBoxVertices(lvt_12_1_, (double)((float)lvt_14_1_.getX() + 0.25F) - p_225619_3_, (double)lvt_15_1_ - p_225619_5_, (double)((float)lvt_14_1_.getZ() + 0.25F) - p_225619_7_, (double)((float)lvt_14_1_.getX() + 0.75F) - p_225619_3_, (double)lvt_15_1_ + 0.09375D - p_225619_5_, (double)((float)lvt_14_1_.getZ() + 0.75F) - p_225619_7_, 0.0F, 0.0F, 1.0F, 0.5F);
         } else {
            WorldRenderer.addChainedFilledBoxVertices(lvt_12_1_, (double)((float)lvt_14_1_.getX() + 0.25F) - p_225619_3_, (double)lvt_15_1_ - p_225619_5_, (double)((float)lvt_14_1_.getZ() + 0.25F) - p_225619_7_, (double)((float)lvt_14_1_.getX() + 0.75F) - p_225619_3_, (double)lvt_15_1_ + 0.09375D - p_225619_5_, (double)((float)lvt_14_1_.getZ() + 0.75F) - p_225619_7_, 0.0F, 1.0F, 0.0F, 0.5F);
         }
      }

      lvt_11_1_.draw();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
