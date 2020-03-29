package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.extensions.IForgeDimension;

public abstract class Dimension implements IForgeDimension {
   public static final float[] MOON_PHASE_FACTORS = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected final World world;
   private final DimensionType type;
   protected boolean doesWaterVaporize;
   protected boolean nether;
   protected final float[] lightBrightnessTable = new float[16];
   private final float[] colorsSunriseSunset = new float[4];
   private IRenderHandler skyRenderer = null;
   private IRenderHandler cloudRenderer = null;
   private IRenderHandler weatherRenderer = null;

   public Dimension(World p_i225788_1_, DimensionType p_i225788_2_, float p_i225788_3_) {
      this.world = p_i225788_1_;
      this.type = p_i225788_2_;

      for(int i = 0; i <= 15; ++i) {
         float f = (float)i / 15.0F;
         float f1 = f / (4.0F - 3.0F * f);
         this.lightBrightnessTable[i] = MathHelper.lerp(p_i225788_3_, f1, 1.0F);
      }

   }

   public int getMoonPhase(long p_76559_1_) {
      return (int)(p_76559_1_ / 24000L % 8L + 8L) % 8;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {
      float f = 0.4F;
      float f1 = MathHelper.cos(p_76560_1_ * 6.2831855F) - 0.0F;
      float f2 = -0.0F;
      if (f1 >= -0.4F && f1 <= 0.4F) {
         float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
         float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * 3.1415927F)) * 0.99F;
         f4 *= f4;
         this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = f4;
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getCloudHeight() {
      return this.getWorld().getWorldInfo().getGenerator().getCloudHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSkyColored() {
      return true;
   }

   @Nullable
   public BlockPos getSpawnCoordinate() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public double getVoidFogYFactor() {
      return this.world.getWorldInfo().getGenerator().voidFadeMagnitude();
   }

   public boolean doesWaterVaporize() {
      return this.doesWaterVaporize;
   }

   public boolean hasSkyLight() {
      return this.type.func_218272_d();
   }

   public boolean isNether() {
      return this.nether;
   }

   public float func_227174_a_(int p_227174_1_) {
      return this.lightBrightnessTable[p_227174_1_];
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder();
   }

   public void onWorldSave() {
   }

   public void tick() {
   }

   /** @deprecated */
   @Deprecated
   public abstract ChunkGenerator<?> createChunkGenerator();

   @Nullable
   public abstract BlockPos findSpawn(ChunkPos var1, boolean var2);

   @Nullable
   public abstract BlockPos findSpawn(int var1, int var2, boolean var3);

   public abstract float calculateCelestialAngle(long var1, float var3);

   public abstract boolean isSurfaceWorld();

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getFogColor(float var1, float var2);

   public abstract boolean canRespawnHere();

   @OnlyIn(Dist.CLIENT)
   public abstract boolean doesXZShowFog(int var1, int var2);

   public DimensionType getType() {
      return this.type;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IRenderHandler getSkyRenderer() {
      return this.skyRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSkyRenderer(IRenderHandler p_setSkyRenderer_1_) {
      this.skyRenderer = p_setSkyRenderer_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IRenderHandler getCloudRenderer() {
      return this.cloudRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   public void setCloudRenderer(IRenderHandler p_setCloudRenderer_1_) {
      this.cloudRenderer = p_setCloudRenderer_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IRenderHandler getWeatherRenderer() {
      return this.weatherRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWeatherRenderer(IRenderHandler p_setWeatherRenderer_1_) {
      this.weatherRenderer = p_setWeatherRenderer_1_;
   }

   public void resetRainAndThunder() {
      this.world.getWorldInfo().setRainTime(0);
      this.world.getWorldInfo().setRaining(false);
      this.world.getWorldInfo().setThunderTime(0);
      this.world.getWorldInfo().setThundering(false);
   }

   public World getWorld() {
      return this.world;
   }
}
