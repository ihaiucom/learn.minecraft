package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreakingParticle extends SpriteTexturedParticle {
   private final float field_217571_C;
   private final float field_217572_F;

   private BreakingParticle(World p_i47644_1_, double p_i47644_2_, double p_i47644_4_, double p_i47644_6_, double p_i47644_8_, double p_i47644_10_, double p_i47644_12_, ItemStack p_i47644_14_) {
      this(p_i47644_1_, p_i47644_2_, p_i47644_4_, p_i47644_6_, p_i47644_14_);
      this.motionX *= 0.10000000149011612D;
      this.motionY *= 0.10000000149011612D;
      this.motionZ *= 0.10000000149011612D;
      this.motionX += p_i47644_8_;
      this.motionY += p_i47644_10_;
      this.motionZ += p_i47644_12_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   protected BreakingParticle(World p_i47645_1_, double p_i47645_2_, double p_i47645_4_, double p_i47645_6_, ItemStack p_i47645_8_) {
      super(p_i47645_1_, p_i47645_2_, p_i47645_4_, p_i47645_6_, 0.0D, 0.0D, 0.0D);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(p_i47645_8_, p_i47645_1_, (LivingEntity)null).getParticleTexture());
      this.particleGravity = 1.0F;
      this.particleScale /= 2.0F;
      this.field_217571_C = this.rand.nextFloat() * 3.0F;
      this.field_217572_F = this.rand.nextFloat() * 3.0F;
   }

   protected float getMinU() {
      return this.sprite.getInterpolatedU((double)((this.field_217571_C + 1.0F) / 4.0F * 16.0F));
   }

   protected float getMaxU() {
      return this.sprite.getInterpolatedU((double)(this.field_217571_C / 4.0F * 16.0F));
   }

   protected float getMinV() {
      return this.sprite.getInterpolatedV((double)(this.field_217572_F / 4.0F * 16.0F));
   }

   protected float getMaxV() {
      return this.sprite.getInterpolatedV((double)((this.field_217572_F + 1.0F) / 4.0F * 16.0F));
   }

   // $FF: synthetic method
   BreakingParticle(World p_i51052_1_, double p_i51052_2_, double p_i51052_4_, double p_i51052_6_, double p_i51052_8_, double p_i51052_10_, double p_i51052_12_, ItemStack p_i51052_14_, Object p_i51052_15_) {
      this(p_i51052_1_, p_i51052_2_, p_i51052_4_, p_i51052_6_, p_i51052_8_, p_i51052_10_, p_i51052_12_, p_i51052_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class SnowballFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BreakingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, new ItemStack(Items.SNOWBALL));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SlimeFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BreakingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, new ItemStack(Items.SLIME_BALL));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<ItemParticleData> {
      public Particle makeParticle(ItemParticleData p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BreakingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, p_199234_1_.getItemStack());
      }
   }
}
