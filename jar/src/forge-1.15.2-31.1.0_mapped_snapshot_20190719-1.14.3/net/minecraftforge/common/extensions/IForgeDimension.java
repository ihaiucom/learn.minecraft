package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IForgeDimension {
   default Dimension getDimension() {
      return (Dimension)this;
   }

   World getWorld();

   default ICapabilityProvider initCapabilities() {
      return null;
   }

   default double getMovementFactor() {
      return this.getDimension() instanceof NetherDimension ? 8.0D : 1.0D;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   IRenderHandler getSkyRenderer();

   @OnlyIn(Dist.CLIENT)
   void setSkyRenderer(IRenderHandler var1);

   @OnlyIn(Dist.CLIENT)
   @Nullable
   IRenderHandler getCloudRenderer();

   @OnlyIn(Dist.CLIENT)
   void setCloudRenderer(IRenderHandler var1);

   @OnlyIn(Dist.CLIENT)
   @Nullable
   IRenderHandler getWeatherRenderer();

   @OnlyIn(Dist.CLIENT)
   void setWeatherRenderer(IRenderHandler var1);

   default void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, Vector3f colors) {
   }

   void resetRainAndThunder();

   default boolean canDoLightning(Chunk chunk) {
      return true;
   }

   default boolean canDoRainSnowIce(Chunk chunk) {
      return true;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   default MusicTicker.MusicType getMusicType() {
      return null;
   }

   default IForgeDimension.SleepResult canSleepAt(PlayerEntity player, BlockPos pos) {
      return this.getDimension().canRespawnHere() && this.getWorld().func_226691_t_(pos) != Biomes.NETHER ? IForgeDimension.SleepResult.ALLOW : IForgeDimension.SleepResult.BED_EXPLODES;
   }

   default boolean isDaytime() {
      return this.getDimension().getType() == DimensionType.OVERWORLD && this.getWorld().getSkylightSubtracted() < 4;
   }

   default float getCurrentMoonPhaseFactor(long time) {
      return Dimension.MOON_PHASE_FACTORS[this.getDimension().getMoonPhase(time)];
   }

   default void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
   }

   default void calculateInitialWeather() {
      this.getWorld().calculateInitialWeatherBody();
   }

   default void updateWeather(Runnable defaultLogic) {
      defaultLogic.run();
   }

   default long getSeed() {
      return this.getWorld().getWorldInfo().getSeed();
   }

   default long getWorldTime() {
      return this.getWorld().getWorldInfo().getDayTime();
   }

   default void setWorldTime(long time) {
      this.getWorld().getWorldInfo().setDayTime(time);
   }

   default BlockPos getSpawnPoint() {
      WorldInfo info = this.getWorld().getWorldInfo();
      return new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
   }

   default void setSpawnPoint(BlockPos pos) {
      this.getWorld().getWorldInfo().setSpawn(pos);
   }

   default boolean canMineBlock(PlayerEntity player, BlockPos pos) {
      return this.getWorld().canMineBlockBody(player, pos);
   }

   default boolean isHighHumidity(BlockPos pos) {
      return this.getWorld().func_226691_t_(pos).isHighHumidity();
   }

   default int getHeight() {
      return 256;
   }

   default int getActualHeight() {
      return this.getDimension().isNether() ? 128 : 256;
   }

   default int getSeaLevel() {
      return 63;
   }

   default boolean shouldMapSpin(String entity, double x, double z, double rotation) {
      return this.getDimension().getType() == DimensionType.THE_NETHER;
   }

   default DimensionType getRespawnDimension(ServerPlayerEntity player) {
      return player.getSpawnDimension();
   }

   public static enum SleepResult {
      ALLOW,
      DENY,
      BED_EXPLODES;
   }
}
