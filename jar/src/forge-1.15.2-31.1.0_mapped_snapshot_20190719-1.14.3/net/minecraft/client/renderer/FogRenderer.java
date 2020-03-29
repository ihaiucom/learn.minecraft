package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private static float red;
   private static float green;
   private static float blue;
   private static int lastWaterFogColor = -1;
   private static int waterFogColor = -1;
   private static long waterFogUpdateTime = -1L;

   public static void func_228371_a_(ActiveRenderInfo p_228371_0_, float p_228371_1_, ClientWorld p_228371_2_, int p_228371_3_, float p_228371_4_) {
      IFluidState ifluidstate = p_228371_0_.getFluidState();
      int i2;
      float f6;
      float f9;
      if (ifluidstate.isTagged(FluidTags.WATER)) {
         long i = Util.milliTime();
         i2 = p_228371_2_.func_226691_t_(new BlockPos(p_228371_0_.getProjectedView())).getWaterFogColor();
         if (waterFogUpdateTime < 0L) {
            lastWaterFogColor = i2;
            waterFogColor = i2;
            waterFogUpdateTime = i;
         }

         int k = lastWaterFogColor >> 16 & 255;
         int l = lastWaterFogColor >> 8 & 255;
         int i1 = lastWaterFogColor & 255;
         int j1 = waterFogColor >> 16 & 255;
         int k1 = waterFogColor >> 8 & 255;
         int l1 = waterFogColor & 255;
         float f = MathHelper.clamp((float)(i - waterFogUpdateTime) / 5000.0F, 0.0F, 1.0F);
         float f1 = MathHelper.lerp(f, (float)j1, (float)k);
         float f2 = MathHelper.lerp(f, (float)k1, (float)l);
         float f3 = MathHelper.lerp(f, (float)l1, (float)i1);
         red = f1 / 255.0F;
         green = f2 / 255.0F;
         blue = f3 / 255.0F;
         if (lastWaterFogColor != i2) {
            lastWaterFogColor = i2;
            waterFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
            waterFogUpdateTime = i;
         }
      } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
         red = 0.6F;
         green = 0.1F;
         blue = 0.0F;
         waterFogUpdateTime = -1L;
      } else {
         float f4 = 0.25F + 0.75F * (float)p_228371_3_ / 32.0F;
         f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
         Vec3d vec3d = p_228371_2_.func_228318_a_(p_228371_0_.getBlockPos(), p_228371_1_);
         f6 = (float)vec3d.x;
         f9 = (float)vec3d.y;
         float f11 = (float)vec3d.z;
         Vec3d vec3d1 = p_228371_2_.func_228329_i_(p_228371_1_);
         red = (float)vec3d1.x;
         green = (float)vec3d1.y;
         blue = (float)vec3d1.z;
         float f13;
         float f16;
         if (p_228371_3_ >= 4) {
            f13 = MathHelper.sin(p_228371_2_.getCelestialAngleRadians(p_228371_1_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f13, 0.0F, 0.0F);
            f16 = p_228371_0_.func_227996_l_().dot(vector3f);
            if (f16 < 0.0F) {
               f16 = 0.0F;
            }

            if (f16 > 0.0F) {
               float[] afloat = p_228371_2_.dimension.calcSunriseSunsetColors(p_228371_2_.getCelestialAngle(p_228371_1_), p_228371_1_);
               if (afloat != null) {
                  f16 *= afloat[3];
                  red = red * (1.0F - f16) + afloat[0] * f16;
                  green = green * (1.0F - f16) + afloat[1] * f16;
                  blue = blue * (1.0F - f16) + afloat[2] * f16;
               }
            }
         }

         red += (f6 - red) * f4;
         green += (f9 - green) * f4;
         blue += (f11 - blue) * f4;
         f13 = p_228371_2_.getRainStrength(p_228371_1_);
         float f15;
         if (f13 > 0.0F) {
            f15 = 1.0F - f13 * 0.5F;
            f16 = 1.0F - f13 * 0.4F;
            red *= f15;
            green *= f15;
            blue *= f16;
         }

         f15 = p_228371_2_.getThunderStrength(p_228371_1_);
         if (f15 > 0.0F) {
            f16 = 1.0F - f15 * 0.5F;
            red *= f16;
            green *= f16;
            blue *= f16;
         }

         waterFogUpdateTime = -1L;
      }

      double d0 = p_228371_0_.getProjectedView().y * p_228371_2_.dimension.getVoidFogYFactor();
      if (p_228371_0_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getRenderViewEntity()).isPotionActive(Effects.BLINDNESS)) {
         i2 = ((LivingEntity)p_228371_0_.getRenderViewEntity()).getActivePotionEffect(Effects.BLINDNESS).getDuration();
         if (i2 < 20) {
            d0 *= (double)(1.0F - (float)i2 / 20.0F);
         } else {
            d0 = 0.0D;
         }
      }

      if (d0 < 1.0D) {
         if (d0 < 0.0D) {
            d0 = 0.0D;
         }

         d0 *= d0;
         red = (float)((double)red * d0);
         green = (float)((double)green * d0);
         blue = (float)((double)blue * d0);
      }

      if (p_228371_4_ > 0.0F) {
         red = red * (1.0F - p_228371_4_) + red * 0.7F * p_228371_4_;
         green = green * (1.0F - p_228371_4_) + green * 0.6F * p_228371_4_;
         blue = blue * (1.0F - p_228371_4_) + blue * 0.6F * p_228371_4_;
      }

      if (ifluidstate.isTagged(FluidTags.WATER)) {
         f6 = 0.0F;
         if (p_228371_0_.getRenderViewEntity() instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)p_228371_0_.getRenderViewEntity();
            f6 = clientplayerentity.getWaterBrightness();
         }

         f9 = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
         if (Float.isInfinite(f9)) {
            f9 = Math.nextAfter(f9, 0.0D);
         }

         red = red * (1.0F - f6) + red * f9 * f6;
         green = green * (1.0F - f6) + green * f9 * f6;
         blue = blue * (1.0F - f6) + blue * f9 * f6;
      } else if (p_228371_0_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getRenderViewEntity()).isPotionActive(Effects.NIGHT_VISION)) {
         f6 = GameRenderer.getNightVisionBrightness((LivingEntity)p_228371_0_.getRenderViewEntity(), p_228371_1_);
         f9 = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
         if (Float.isInfinite(f9)) {
            f9 = Math.nextAfter(f9, 0.0D);
         }

         red = red * (1.0F - f6) + red * f9 * f6;
         green = green * (1.0F - f6) + green * f9 * f6;
         blue = blue * (1.0F - f6) + blue * f9 * f6;
      }

      EntityViewRenderEvent.FogColors event = new EntityViewRenderEvent.FogColors(p_228371_0_, p_228371_1_, red, green, blue);
      MinecraftForge.EVENT_BUS.post(event);
      red = event.getRed();
      green = event.getGreen();
      blue = event.getBlue();
      RenderSystem.clearColor(red, green, blue, 0.0F);
   }

   public static void func_228370_a_() {
      RenderSystem.fogDensity(0.0F);
      RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
   }

   /** @deprecated */
   @Deprecated
   public static void func_228372_a_(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_) {
      setupFog(p_228372_0_, p_228372_1_, p_228372_2_, p_228372_3_, 0.0F);
   }

   public static void setupFog(ActiveRenderInfo p_setupFog_0_, FogRenderer.FogType p_setupFog_1_, float p_setupFog_2_, boolean p_setupFog_3_, float p_setupFog_4_) {
      IFluidState ifluidstate = p_setupFog_0_.getFluidState();
      Entity entity = p_setupFog_0_.getRenderViewEntity();
      boolean flag = ifluidstate.getFluid() != Fluids.EMPTY;
      float hook = ForgeHooksClient.getFogDensity(p_setupFog_1_, p_setupFog_0_, p_setupFog_4_, 0.1F);
      if (hook >= 0.0F) {
         RenderSystem.fogDensity(hook);
      } else {
         float f2;
         if (flag) {
            f2 = 1.0F;
            if (ifluidstate.isTagged(FluidTags.WATER)) {
               f2 = 0.05F;
               if (entity instanceof ClientPlayerEntity) {
                  ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)entity;
                  f2 -= clientplayerentity.getWaterBrightness() * clientplayerentity.getWaterBrightness() * 0.03F;
                  Biome biome = clientplayerentity.world.func_226691_t_(new BlockPos(clientplayerentity));
                  if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                     f2 += 0.005F;
                  }
               }
            } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
               f2 = 2.0F;
            }

            RenderSystem.fogDensity(f2);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
         } else {
            float f3;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).isPotionActive(Effects.BLINDNESS)) {
               int i = ((LivingEntity)entity).getActivePotionEffect(Effects.BLINDNESS).getDuration();
               float f1 = MathHelper.lerp(Math.min(1.0F, (float)i / 20.0F), p_setupFog_2_, 5.0F);
               if (p_setupFog_1_ == FogRenderer.FogType.FOG_SKY) {
                  f2 = 0.0F;
                  f3 = f1 * 0.8F;
               } else {
                  f2 = f1 * 0.25F;
                  f3 = f1;
               }
            } else if (p_setupFog_3_) {
               f2 = p_setupFog_2_ * 0.05F;
               f3 = Math.min(p_setupFog_2_, 192.0F) * 0.5F;
            } else if (p_setupFog_1_ == FogRenderer.FogType.FOG_SKY) {
               f2 = 0.0F;
               f3 = p_setupFog_2_;
            } else {
               f2 = p_setupFog_2_ * 0.75F;
               f3 = p_setupFog_2_;
            }

            RenderSystem.fogStart(f2);
            RenderSystem.fogEnd(f3);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
            ForgeHooksClient.onFogRender(p_setupFog_1_, p_setupFog_0_, p_setupFog_4_, f3);
         }
      }

   }

   public static void func_228373_b_() {
      RenderSystem.fog(2918, red, green, blue, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogType {
      FOG_SKY,
      FOG_TERRAIN;
   }
}
