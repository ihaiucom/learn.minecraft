package net.minecraft.client.particle;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BarrierParticle extends SpriteTexturedParticle {
   private BarrierParticle(World p_i48192_1_, double p_i48192_2_, double p_i48192_4_, double p_i48192_6_, IItemProvider p_i48192_8_) {
      super(p_i48192_1_, p_i48192_2_, p_i48192_4_, p_i48192_6_);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(p_i48192_8_));
      this.particleGravity = 0.0F;
      this.maxAge = 80;
      this.canCollide = false;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   public float getScale(float p_217561_1_) {
      return 0.5F;
   }

   // $FF: synthetic method
   BarrierParticle(World p_i51053_1_, double p_i51053_2_, double p_i51053_4_, double p_i51053_6_, IItemProvider p_i51053_8_, Object p_i51053_9_) {
      this(p_i51053_1_, p_i51053_2_, p_i51053_4_, p_i51053_6_, p_i51053_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BarrierParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Blocks.BARRIER.asItem());
      }
   }
}
