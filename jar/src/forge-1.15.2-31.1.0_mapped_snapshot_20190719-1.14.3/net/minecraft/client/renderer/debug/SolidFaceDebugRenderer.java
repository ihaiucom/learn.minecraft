package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolidFaceDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceDebugRenderer(Minecraft p_i47478_1_) {
      this.minecraft = p_i47478_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      IBlockReader lvt_9_1_ = this.minecraft.player.world;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.disableTexture();
      RenderSystem.depthMask(false);
      BlockPos lvt_10_1_ = new BlockPos(p_225619_3_, p_225619_5_, p_225619_7_);
      Iterator var11 = BlockPos.getAllInBoxMutable(lvt_10_1_.add(-6, -6, -6), lvt_10_1_.add(6, 6, 6)).iterator();

      while(true) {
         BlockPos lvt_12_1_;
         BlockState lvt_13_1_;
         do {
            if (!var11.hasNext()) {
               RenderSystem.depthMask(true);
               RenderSystem.enableTexture();
               RenderSystem.disableBlend();
               return;
            }

            lvt_12_1_ = (BlockPos)var11.next();
            lvt_13_1_ = lvt_9_1_.getBlockState(lvt_12_1_);
         } while(lvt_13_1_.getBlock() == Blocks.AIR);

         VoxelShape lvt_14_1_ = lvt_13_1_.getShape(lvt_9_1_, lvt_12_1_);
         Iterator var15 = lvt_14_1_.toBoundingBoxList().iterator();

         while(var15.hasNext()) {
            AxisAlignedBB lvt_16_1_ = (AxisAlignedBB)var15.next();
            AxisAlignedBB lvt_17_1_ = lvt_16_1_.offset(lvt_12_1_).grow(0.002D).offset(-p_225619_3_, -p_225619_5_, -p_225619_7_);
            double lvt_18_1_ = lvt_17_1_.minX;
            double lvt_20_1_ = lvt_17_1_.minY;
            double lvt_22_1_ = lvt_17_1_.minZ;
            double lvt_24_1_ = lvt_17_1_.maxX;
            double lvt_26_1_ = lvt_17_1_.maxY;
            double lvt_28_1_ = lvt_17_1_.maxZ;
            float lvt_30_1_ = 1.0F;
            float lvt_31_1_ = 0.0F;
            float lvt_32_1_ = 0.0F;
            float lvt_33_1_ = 0.5F;
            Tessellator lvt_34_6_;
            BufferBuilder lvt_35_6_;
            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.WEST)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }

            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.SOUTH)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }

            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.EAST)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }

            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.NORTH)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }

            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.DOWN)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_20_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }

            if (lvt_13_1_.func_224755_d(lvt_9_1_, lvt_12_1_, Direction.UP)) {
               lvt_34_6_ = Tessellator.getInstance();
               lvt_35_6_ = lvt_34_6_.getBuffer();
               lvt_35_6_.begin(5, DefaultVertexFormats.POSITION_COLOR);
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_18_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_22_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_35_6_.func_225582_a_(lvt_24_1_, lvt_26_1_, lvt_28_1_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               lvt_34_6_.draw();
            }
         }
      }
   }
}
