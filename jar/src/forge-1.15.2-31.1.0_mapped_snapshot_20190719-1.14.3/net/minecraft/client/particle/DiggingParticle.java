package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiggingParticle extends SpriteTexturedParticle {
   private final BlockState sourceState;
   private BlockPos sourcePos;
   private final float field_217587_G;
   private final float field_217588_H;

   public DiggingParticle(World p_i46280_1_, double p_i46280_2_, double p_i46280_4_, double p_i46280_6_, double p_i46280_8_, double p_i46280_10_, double p_i46280_12_, BlockState p_i46280_14_) {
      super(p_i46280_1_, p_i46280_2_, p_i46280_4_, p_i46280_6_, p_i46280_8_, p_i46280_10_, p_i46280_12_);
      this.sourceState = p_i46280_14_;
      this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(p_i46280_14_));
      this.particleGravity = 1.0F;
      this.particleRed = 0.6F;
      this.particleGreen = 0.6F;
      this.particleBlue = 0.6F;
      this.particleScale /= 2.0F;
      this.field_217587_G = this.rand.nextFloat() * 3.0F;
      this.field_217588_H = this.rand.nextFloat() * 3.0F;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   public DiggingParticle setBlockPos(BlockPos p_174846_1_) {
      this.updateSprite(p_174846_1_);
      this.sourcePos = p_174846_1_;
      if (this.sourceState.getBlock() == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(p_174846_1_);
         return this;
      }
   }

   public DiggingParticle init() {
      this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
      Block block = this.sourceState.getBlock();
      if (block == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(this.sourcePos);
         return this;
      }
   }

   protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
      int i = Minecraft.getInstance().getBlockColors().func_228054_a_(this.sourceState, this.world, p_187154_1_, 0);
      this.particleRed *= (float)(i >> 16 & 255) / 255.0F;
      this.particleGreen *= (float)(i >> 8 & 255) / 255.0F;
      this.particleBlue *= (float)(i & 255) / 255.0F;
   }

   protected float getMinU() {
      return this.sprite.getInterpolatedU((double)((this.field_217587_G + 1.0F) / 4.0F * 16.0F));
   }

   protected float getMaxU() {
      return this.sprite.getInterpolatedU((double)(this.field_217587_G / 4.0F * 16.0F));
   }

   protected float getMinV() {
      return this.sprite.getInterpolatedV((double)(this.field_217588_H / 4.0F * 16.0F));
   }

   protected float getMaxV() {
      return this.sprite.getInterpolatedV((double)((this.field_217588_H + 1.0F) / 4.0F * 16.0F));
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = 0;
      if (this.world.isBlockLoaded(this.sourcePos)) {
         j = WorldRenderer.func_228421_a_(this.world, this.sourcePos);
      }

      return i == 0 ? j : i;
   }

   private Particle updateSprite(BlockPos p_updateSprite_1_) {
      if (p_updateSprite_1_ != null) {
         this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(this.sourceState, this.world, p_updateSprite_1_));
      }

      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      public Particle makeParticle(BlockParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         BlockState blockstate = p_199234_1_.getBlockState();
         return !blockstate.isAir() && blockstate.getBlock() != Blocks.MOVING_PISTON ? (new DiggingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, blockstate)).init().updateSprite(p_199234_1_.getPos()) : null;
      }
   }
}
