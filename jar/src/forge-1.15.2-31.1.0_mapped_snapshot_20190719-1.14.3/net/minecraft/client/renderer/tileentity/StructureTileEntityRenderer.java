package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureTileEntityRenderer extends TileEntityRenderer<StructureBlockTileEntity> {
   public StructureTileEntityRenderer(TileEntityRendererDispatcher p_i226017_1_) {
      super(p_i226017_1_);
   }

   public void func_225616_a_(StructureBlockTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      if (Minecraft.getInstance().player.canUseCommandBlock() || Minecraft.getInstance().player.isSpectator()) {
         BlockPos lvt_7_1_ = p_225616_1_.getPosition();
         BlockPos lvt_8_1_ = p_225616_1_.getStructureSize();
         if (lvt_8_1_.getX() >= 1 && lvt_8_1_.getY() >= 1 && lvt_8_1_.getZ() >= 1) {
            if (p_225616_1_.getMode() == StructureMode.SAVE || p_225616_1_.getMode() == StructureMode.LOAD) {
               double lvt_9_1_ = (double)lvt_7_1_.getX();
               double lvt_11_1_ = (double)lvt_7_1_.getZ();
               double lvt_19_1_ = (double)lvt_7_1_.getY();
               double lvt_25_1_ = lvt_19_1_ + (double)lvt_8_1_.getY();
               double lvt_13_3_;
               double lvt_15_3_;
               switch(p_225616_1_.getMirror()) {
               case LEFT_RIGHT:
                  lvt_13_3_ = (double)lvt_8_1_.getX();
                  lvt_15_3_ = (double)(-lvt_8_1_.getZ());
                  break;
               case FRONT_BACK:
                  lvt_13_3_ = (double)(-lvt_8_1_.getX());
                  lvt_15_3_ = (double)lvt_8_1_.getZ();
                  break;
               default:
                  lvt_13_3_ = (double)lvt_8_1_.getX();
                  lvt_15_3_ = (double)lvt_8_1_.getZ();
               }

               double lvt_17_4_;
               double lvt_21_4_;
               double lvt_23_4_;
               double lvt_27_4_;
               switch(p_225616_1_.getRotation()) {
               case CLOCKWISE_90:
                  lvt_17_4_ = lvt_15_3_ < 0.0D ? lvt_9_1_ : lvt_9_1_ + 1.0D;
                  lvt_21_4_ = lvt_13_3_ < 0.0D ? lvt_11_1_ + 1.0D : lvt_11_1_;
                  lvt_23_4_ = lvt_17_4_ - lvt_15_3_;
                  lvt_27_4_ = lvt_21_4_ + lvt_13_3_;
                  break;
               case CLOCKWISE_180:
                  lvt_17_4_ = lvt_13_3_ < 0.0D ? lvt_9_1_ : lvt_9_1_ + 1.0D;
                  lvt_21_4_ = lvt_15_3_ < 0.0D ? lvt_11_1_ : lvt_11_1_ + 1.0D;
                  lvt_23_4_ = lvt_17_4_ - lvt_13_3_;
                  lvt_27_4_ = lvt_21_4_ - lvt_15_3_;
                  break;
               case COUNTERCLOCKWISE_90:
                  lvt_17_4_ = lvt_15_3_ < 0.0D ? lvt_9_1_ + 1.0D : lvt_9_1_;
                  lvt_21_4_ = lvt_13_3_ < 0.0D ? lvt_11_1_ : lvt_11_1_ + 1.0D;
                  lvt_23_4_ = lvt_17_4_ + lvt_15_3_;
                  lvt_27_4_ = lvt_21_4_ - lvt_13_3_;
                  break;
               default:
                  lvt_17_4_ = lvt_13_3_ < 0.0D ? lvt_9_1_ + 1.0D : lvt_9_1_;
                  lvt_21_4_ = lvt_15_3_ < 0.0D ? lvt_11_1_ + 1.0D : lvt_11_1_;
                  lvt_23_4_ = lvt_17_4_ + lvt_13_3_;
                  lvt_27_4_ = lvt_21_4_ + lvt_15_3_;
               }

               float lvt_29_1_ = 1.0F;
               float lvt_30_1_ = 0.9F;
               float lvt_31_1_ = 0.5F;
               IVertexBuilder lvt_32_1_ = p_225616_4_.getBuffer(RenderType.func_228659_m_());
               if (p_225616_1_.getMode() == StructureMode.SAVE || p_225616_1_.showsBoundingBox()) {
                  WorldRenderer.func_228428_a_(p_225616_3_, lvt_32_1_, lvt_17_4_, lvt_19_1_, lvt_21_4_, lvt_23_4_, lvt_25_1_, lvt_27_4_, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
               }

               if (p_225616_1_.getMode() == StructureMode.SAVE && p_225616_1_.showsAir()) {
                  this.func_228880_a_(p_225616_1_, lvt_32_1_, lvt_7_1_, true, p_225616_3_);
                  this.func_228880_a_(p_225616_1_, lvt_32_1_, lvt_7_1_, false, p_225616_3_);
               }

            }
         }
      }
   }

   private void func_228880_a_(StructureBlockTileEntity p_228880_1_, IVertexBuilder p_228880_2_, BlockPos p_228880_3_, boolean p_228880_4_, MatrixStack p_228880_5_) {
      IBlockReader lvt_6_1_ = p_228880_1_.getWorld();
      BlockPos lvt_7_1_ = p_228880_1_.getPos();
      BlockPos lvt_8_1_ = lvt_7_1_.add(p_228880_3_);
      Iterator var9 = BlockPos.getAllInBoxMutable(lvt_8_1_, lvt_8_1_.add(p_228880_1_.getStructureSize()).add(-1, -1, -1)).iterator();

      while(true) {
         BlockPos lvt_10_1_;
         boolean lvt_12_1_;
         boolean lvt_13_1_;
         do {
            if (!var9.hasNext()) {
               return;
            }

            lvt_10_1_ = (BlockPos)var9.next();
            BlockState lvt_11_1_ = lvt_6_1_.getBlockState(lvt_10_1_);
            lvt_12_1_ = lvt_11_1_.isAir();
            lvt_13_1_ = lvt_11_1_.getBlock() == Blocks.STRUCTURE_VOID;
         } while(!lvt_12_1_ && !lvt_13_1_);

         float lvt_14_1_ = lvt_12_1_ ? 0.05F : 0.0F;
         double lvt_15_1_ = (double)((float)(lvt_10_1_.getX() - lvt_7_1_.getX()) + 0.45F - lvt_14_1_);
         double lvt_17_1_ = (double)((float)(lvt_10_1_.getY() - lvt_7_1_.getY()) + 0.45F - lvt_14_1_);
         double lvt_19_1_ = (double)((float)(lvt_10_1_.getZ() - lvt_7_1_.getZ()) + 0.45F - lvt_14_1_);
         double lvt_21_1_ = (double)((float)(lvt_10_1_.getX() - lvt_7_1_.getX()) + 0.55F + lvt_14_1_);
         double lvt_23_1_ = (double)((float)(lvt_10_1_.getY() - lvt_7_1_.getY()) + 0.55F + lvt_14_1_);
         double lvt_25_1_ = (double)((float)(lvt_10_1_.getZ() - lvt_7_1_.getZ()) + 0.55F + lvt_14_1_);
         if (p_228880_4_) {
            WorldRenderer.func_228428_a_(p_228880_5_, p_228880_2_, lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_21_1_, lvt_23_1_, lvt_25_1_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
         } else if (lvt_12_1_) {
            WorldRenderer.func_228428_a_(p_228880_5_, p_228880_2_, lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_21_1_, lvt_23_1_, lvt_25_1_, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
         } else {
            WorldRenderer.func_228428_a_(p_228880_5_, p_228880_2_, lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_21_1_, lvt_23_1_, lvt_25_1_, 1.0F, 0.25F, 0.25F, 1.0F, 1.0F, 0.25F, 0.25F);
         }
      }
   }

   public boolean isGlobalRenderer(StructureBlockTileEntity p_188185_1_) {
      return true;
   }
}
