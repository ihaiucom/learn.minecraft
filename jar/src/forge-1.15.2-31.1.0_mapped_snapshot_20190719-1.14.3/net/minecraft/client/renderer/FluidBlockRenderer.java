package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class FluidBlockRenderer {
   private final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
   private TextureAtlasSprite atlasSpriteWaterOverlay;

   protected void initAtlasSprites() {
      this.atlasSpritesLava[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.LAVA.getDefaultState()).getParticleTexture();
      this.atlasSpritesLava[1] = ModelBakery.LOCATION_LAVA_FLOW.func_229314_c_();
      this.atlasSpritesWater[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.WATER.getDefaultState()).getParticleTexture();
      this.atlasSpritesWater[1] = ModelBakery.LOCATION_WATER_FLOW.func_229314_c_();
      this.atlasSpriteWaterOverlay = ModelBakery.LOCATION_WATER_OVERLAY.func_229314_c_();
   }

   private static boolean isAdjacentFluidSameAs(IBlockReader p_209557_0_, BlockPos p_209557_1_, Direction p_209557_2_, IFluidState p_209557_3_) {
      BlockPos blockpos = p_209557_1_.offset(p_209557_2_);
      IFluidState ifluidstate = p_209557_0_.getFluidState(blockpos);
      return ifluidstate.getFluid().isEquivalentTo(p_209557_3_.getFluid());
   }

   private static boolean func_209556_a(IBlockReader p_209556_0_, BlockPos p_209556_1_, Direction p_209556_2_, float p_209556_3_) {
      BlockPos blockpos = p_209556_1_.offset(p_209556_2_);
      BlockState blockstate = p_209556_0_.getBlockState(blockpos);
      if (blockstate.isSolid()) {
         VoxelShape voxelshape = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)p_209556_3_, 1.0D);
         VoxelShape voxelshape1 = blockstate.getRenderShape(p_209556_0_, blockpos);
         return VoxelShapes.isCubeSideCovered(voxelshape, voxelshape1, p_209556_2_);
      } else {
         return false;
      }
   }

   public boolean func_228796_a_(ILightReader p_228796_1_, BlockPos p_228796_2_, IVertexBuilder p_228796_3_, IFluidState p_228796_4_) {
      boolean flag = p_228796_4_.isTagged(FluidTags.LAVA);
      TextureAtlasSprite[] atextureatlassprite = ForgeHooksClient.getFluidSprites(p_228796_1_, p_228796_2_, p_228796_4_);
      int i = p_228796_4_.getFluid().getAttributes().getColor(p_228796_1_, p_228796_2_);
      float alpha = (float)(i >> 24 & 255) / 255.0F;
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      boolean flag1 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.UP, p_228796_4_);
      boolean flag2 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.DOWN, p_228796_4_) && !func_209556_a(p_228796_1_, p_228796_2_, Direction.DOWN, 0.8888889F);
      boolean flag3 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.NORTH, p_228796_4_);
      boolean flag4 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.SOUTH, p_228796_4_);
      boolean flag5 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.WEST, p_228796_4_);
      boolean flag6 = !isAdjacentFluidSameAs(p_228796_1_, p_228796_2_, Direction.EAST, p_228796_4_);
      if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
         return false;
      } else {
         boolean flag7 = false;
         float f3 = 0.5F;
         float f4 = 1.0F;
         float f5 = 0.8F;
         float f6 = 0.6F;
         float f7 = this.getFluidHeight(p_228796_1_, p_228796_2_, p_228796_4_.getFluid());
         float f8 = this.getFluidHeight(p_228796_1_, p_228796_2_.south(), p_228796_4_.getFluid());
         float f9 = this.getFluidHeight(p_228796_1_, p_228796_2_.east().south(), p_228796_4_.getFluid());
         float f10 = this.getFluidHeight(p_228796_1_, p_228796_2_.east(), p_228796_4_.getFluid());
         double d0 = (double)(p_228796_2_.getX() & 15);
         double d1 = (double)(p_228796_2_.getY() & 15);
         double d2 = (double)(p_228796_2_.getZ() & 15);
         float f11 = 0.001F;
         float f12 = flag2 ? 0.001F : 0.0F;
         float f36;
         float f38;
         float f15;
         float f17;
         float f18;
         float f19;
         float f48;
         float f49;
         float f50;
         if (flag1 && !func_209556_a(p_228796_1_, p_228796_2_, Direction.UP, Math.min(Math.min(f7, f8), Math.min(f9, f10)))) {
            flag7 = true;
            f7 -= 0.001F;
            f8 -= 0.001F;
            f9 -= 0.001F;
            f10 -= 0.001F;
            Vec3d vec3d = p_228796_4_.getFlow(p_228796_1_, p_228796_2_);
            float f16;
            float f20;
            TextureAtlasSprite textureatlassprite;
            float f44;
            float f45;
            float f46;
            float f47;
            if (vec3d.x == 0.0D && vec3d.z == 0.0D) {
               textureatlassprite = atextureatlassprite[0];
               f36 = textureatlassprite.getInterpolatedU(0.0D);
               f17 = textureatlassprite.getInterpolatedV(0.0D);
               f38 = f36;
               f18 = textureatlassprite.getInterpolatedV(16.0D);
               f15 = textureatlassprite.getInterpolatedU(16.0D);
               f19 = f18;
               f16 = f15;
               f20 = f17;
            } else {
               textureatlassprite = atextureatlassprite[1];
               f44 = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964F;
               f45 = MathHelper.sin(f44) * 0.25F;
               f46 = MathHelper.cos(f44) * 0.25F;
               f47 = 8.0F;
               f36 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f46 - f45) * 16.0F));
               f17 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f46 + f45) * 16.0F));
               f38 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f46 + f45) * 16.0F));
               f18 = textureatlassprite.getInterpolatedV((double)(8.0F + (f46 + f45) * 16.0F));
               f15 = textureatlassprite.getInterpolatedU((double)(8.0F + (f46 + f45) * 16.0F));
               f19 = textureatlassprite.getInterpolatedV((double)(8.0F + (f46 - f45) * 16.0F));
               f16 = textureatlassprite.getInterpolatedU((double)(8.0F + (f46 - f45) * 16.0F));
               f20 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f46 - f45) * 16.0F));
            }

            float f43 = (f36 + f38 + f15 + f16) / 4.0F;
            f44 = (f17 + f18 + f19 + f20) / 4.0F;
            f45 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getMaxU() - atextureatlassprite[0].getMinU());
            f46 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getMaxV() - atextureatlassprite[0].getMinV());
            f47 = 4.0F / Math.max(f46, f45);
            f36 = MathHelper.lerp(f47, f36, f43);
            f38 = MathHelper.lerp(f47, f38, f43);
            f15 = MathHelper.lerp(f47, f15, f43);
            f16 = MathHelper.lerp(f47, f16, f43);
            f17 = MathHelper.lerp(f47, f17, f44);
            f18 = MathHelper.lerp(f47, f18, f44);
            f19 = MathHelper.lerp(f47, f19, f44);
            f20 = MathHelper.lerp(f47, f20, f44);
            int j = this.func_228795_a_(p_228796_1_, p_228796_2_);
            f48 = 1.0F * f;
            f49 = 1.0F * f1;
            f50 = 1.0F * f2;
            this.func_228797_a_(p_228796_3_, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f48, f49, f50, alpha, f36, f17, j);
            this.func_228797_a_(p_228796_3_, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f48, f49, f50, alpha, f38, f18, j);
            this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f48, f49, f50, alpha, f15, f19, j);
            this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f48, f49, f50, alpha, f16, f20, j);
            if (p_228796_4_.shouldRenderSides(p_228796_1_, p_228796_2_.up())) {
               this.func_228797_a_(p_228796_3_, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f48, f49, f50, alpha, f36, f17, j);
               this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f48, f49, f50, alpha, f16, f20, j);
               this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f48, f49, f50, alpha, f15, f19, j);
               this.func_228797_a_(p_228796_3_, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f48, f49, f50, alpha, f38, f18, j);
            }
         }

         if (flag2) {
            float f34 = atextureatlassprite[0].getMinU();
            f36 = atextureatlassprite[0].getMaxU();
            f38 = atextureatlassprite[0].getMinV();
            f15 = atextureatlassprite[0].getMaxV();
            int i1 = this.func_228795_a_(p_228796_1_, p_228796_2_.down());
            f17 = 0.5F * f;
            f18 = 0.5F * f1;
            f19 = 0.5F * f2;
            this.func_228797_a_(p_228796_3_, d0, d1 + (double)f12, d2 + 1.0D, f17, f18, f19, alpha, f34, f15, i1);
            this.func_228797_a_(p_228796_3_, d0, d1 + (double)f12, d2, f17, f18, f19, alpha, f34, f38, i1);
            this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f12, d2, f17, f18, f19, alpha, f36, f38, i1);
            this.func_228797_a_(p_228796_3_, d0 + 1.0D, d1 + (double)f12, d2 + 1.0D, f17, f18, f19, alpha, f36, f15, i1);
            flag7 = true;
         }

         for(int l = 0; l < 4; ++l) {
            double d3;
            double d4;
            double d5;
            double d6;
            Direction direction;
            boolean flag8;
            if (l == 0) {
               f36 = f7;
               f38 = f10;
               d3 = d0;
               d5 = d0 + 1.0D;
               d4 = d2 + 0.0010000000474974513D;
               d6 = d2 + 0.0010000000474974513D;
               direction = Direction.NORTH;
               flag8 = flag3;
            } else if (l == 1) {
               f36 = f9;
               f38 = f8;
               d3 = d0 + 1.0D;
               d5 = d0;
               d4 = d2 + 1.0D - 0.0010000000474974513D;
               d6 = d2 + 1.0D - 0.0010000000474974513D;
               direction = Direction.SOUTH;
               flag8 = flag4;
            } else if (l == 2) {
               f36 = f8;
               f38 = f7;
               d3 = d0 + 0.0010000000474974513D;
               d5 = d0 + 0.0010000000474974513D;
               d4 = d2 + 1.0D;
               d6 = d2;
               direction = Direction.WEST;
               flag8 = flag5;
            } else {
               f36 = f10;
               f38 = f9;
               d3 = d0 + 1.0D - 0.0010000000474974513D;
               d5 = d0 + 1.0D - 0.0010000000474974513D;
               d4 = d2;
               d6 = d2 + 1.0D;
               direction = Direction.EAST;
               flag8 = flag6;
            }

            if (flag8 && !func_209556_a(p_228796_1_, p_228796_2_, direction, Math.max(f36, f38))) {
               flag7 = true;
               BlockPos blockpos = p_228796_2_.offset(direction);
               TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
               if (atextureatlassprite[2] != null) {
                  Block block = p_228796_1_.getBlockState(blockpos).getBlock();
                  if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
                     textureatlassprite2 = atextureatlassprite[2];
                  }
               }

               f48 = textureatlassprite2.getInterpolatedU(0.0D);
               f49 = textureatlassprite2.getInterpolatedU(8.0D);
               f50 = textureatlassprite2.getInterpolatedV((double)((1.0F - f36) * 16.0F * 0.5F));
               float f28 = textureatlassprite2.getInterpolatedV((double)((1.0F - f38) * 16.0F * 0.5F));
               float f29 = textureatlassprite2.getInterpolatedV(8.0D);
               int k = this.func_228795_a_(p_228796_1_, blockpos);
               float f30 = l < 2 ? 0.8F : 0.6F;
               float f31 = 1.0F * f30 * f;
               float f32 = 1.0F * f30 * f1;
               float f33 = 1.0F * f30 * f2;
               this.func_228797_a_(p_228796_3_, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
               this.func_228797_a_(p_228796_3_, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
               this.func_228797_a_(p_228796_3_, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
               this.func_228797_a_(p_228796_3_, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
               if (textureatlassprite2 != atextureatlassprite[2]) {
                  this.func_228797_a_(p_228796_3_, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
                  this.func_228797_a_(p_228796_3_, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
                  this.func_228797_a_(p_228796_3_, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
                  this.func_228797_a_(p_228796_3_, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
               }
            }
         }

         return flag7;
      }
   }

   /** @deprecated */
   @Deprecated
   private void func_228797_a_(IVertexBuilder p_228797_1_, double p_228797_2_, double p_228797_4_, double p_228797_6_, float p_228797_8_, float p_228797_9_, float p_228797_10_, float p_228797_11_, float p_228797_12_, int p_228797_13_) {
      this.func_228797_a_(p_228797_1_, p_228797_2_, p_228797_4_, p_228797_6_, p_228797_8_, p_228797_9_, p_228797_10_, 1.0F, p_228797_11_, p_228797_12_, p_228797_13_);
   }

   private void func_228797_a_(IVertexBuilder p_228797_1_, double p_228797_2_, double p_228797_4_, double p_228797_6_, float p_228797_8_, float p_228797_9_, float p_228797_10_, float p_228797_11_, float p_228797_12_, float p_228797_13_, int p_228797_14_) {
      p_228797_1_.func_225582_a_(p_228797_2_, p_228797_4_, p_228797_6_).func_227885_a_(p_228797_8_, p_228797_9_, p_228797_10_, p_228797_11_).func_225583_a_(p_228797_12_, p_228797_13_).func_227886_a_(p_228797_14_).func_225584_a_(0.0F, 1.0F, 0.0F).endVertex();
   }

   private int func_228795_a_(ILightReader p_228795_1_, BlockPos p_228795_2_) {
      int i = WorldRenderer.func_228421_a_(p_228795_1_, p_228795_2_);
      int j = WorldRenderer.func_228421_a_(p_228795_1_, p_228795_2_.up());
      int k = i & 255;
      int l = j & 255;
      int i1 = i >> 16 & 255;
      int j1 = j >> 16 & 255;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }

   private float getFluidHeight(IBlockReader p_217640_1_, BlockPos p_217640_2_, Fluid p_217640_3_) {
      int i = 0;
      float f = 0.0F;

      for(int j = 0; j < 4; ++j) {
         BlockPos blockpos = p_217640_2_.add(-(j & 1), 0, -(j >> 1 & 1));
         if (p_217640_1_.getFluidState(blockpos.up()).getFluid().isEquivalentTo(p_217640_3_)) {
            return 1.0F;
         }

         IFluidState ifluidstate = p_217640_1_.getFluidState(blockpos);
         if (ifluidstate.getFluid().isEquivalentTo(p_217640_3_)) {
            float f1 = ifluidstate.func_215679_a(p_217640_1_, blockpos);
            if (f1 >= 0.8F) {
               f += f1 * 10.0F;
               i += 10;
            } else {
               f += f1;
               ++i;
            }
         } else if (!p_217640_1_.getBlockState(blockpos).getMaterial().isSolid()) {
            ++i;
         }
      }

      return f / (float)i;
   }
}
