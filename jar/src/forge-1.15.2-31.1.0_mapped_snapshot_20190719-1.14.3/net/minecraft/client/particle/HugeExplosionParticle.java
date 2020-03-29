package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HugeExplosionParticle extends MetaParticle {
   private int timeSinceStart;
   private final int maximumTime;

   private HugeExplosionParticle(World p_i51026_1_, double p_i51026_2_, double p_i51026_4_, double p_i51026_6_) {
      super(p_i51026_1_, p_i51026_2_, p_i51026_4_, p_i51026_6_, 0.0D, 0.0D, 0.0D);
      this.maximumTime = 8;
   }

   public void tick() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 6; ++lvt_1_1_) {
         double lvt_2_1_ = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         double lvt_4_1_ = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         double lvt_6_1_ = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
         this.world.addParticle(ParticleTypes.EXPLOSION, lvt_2_1_, lvt_4_1_, lvt_6_1_, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
      }

      ++this.timeSinceStart;
      if (this.timeSinceStart == this.maximumTime) {
         this.setExpired();
      }

   }

   // $FF: synthetic method
   HugeExplosionParticle(World p_i51027_1_, double p_i51027_2_, double p_i51027_4_, double p_i51027_6_, Object p_i51027_8_) {
      this(p_i51027_1_, p_i51027_2_, p_i51027_4_, p_i51027_6_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new HugeExplosionParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
      }
   }
}
