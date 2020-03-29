package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RainParticle extends SpriteTexturedParticle {
   protected RainParticle(World p_i1235_1_, double p_i1235_2_, double p_i1235_4_, double p_i1235_6_) {
      super(p_i1235_1_, p_i1235_2_, p_i1235_4_, p_i1235_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= 0.30000001192092896D;
      this.motionY = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.motionZ *= 0.30000001192092896D;
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      } else {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9800000190734863D;
         this.motionY *= 0.9800000190734863D;
         this.motionZ *= 0.9800000190734863D;
         if (this.onGround) {
            if (Math.random() < 0.5D) {
               this.setExpired();
            }

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
         }

         BlockPos lvt_1_1_ = new BlockPos(this.posX, this.posY, this.posZ);
         double lvt_2_1_ = Math.max(this.world.getBlockState(lvt_1_1_).getCollisionShape(this.world, lvt_1_1_).max(Direction.Axis.Y, this.posX - (double)lvt_1_1_.getX(), this.posZ - (double)lvt_1_1_.getZ()), (double)this.world.getFluidState(lvt_1_1_).func_215679_a(this.world, lvt_1_1_));
         if (lvt_2_1_ > 0.0D && this.posY < (double)lvt_1_1_.getY() + lvt_2_1_) {
            this.setExpired();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50836_1_) {
         this.spriteSet = p_i50836_1_;
      }

      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         RainParticle lvt_15_1_ = new RainParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lvt_15_1_.selectSpriteRandomly(this.spriteSet);
         return lvt_15_1_;
      }
   }
}
