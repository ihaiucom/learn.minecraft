package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IEntityReader, IWorldReader, IWorldGenerationReader {
   long getSeed();

   default float getCurrentMoonPhaseFactor() {
      return this.getDimension().getCurrentMoonPhaseFactor(this.getWorld().getDayTime());
   }

   default float getCelestialAngle(float p_72826_1_) {
      return this.getDimension().calculateCelestialAngle(this.getWorld().getDayTime(), p_72826_1_);
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getWorld().getDayTime());
   }

   ITickList<Block> getPendingBlockTicks();

   ITickList<Fluid> getPendingFluidTicks();

   World getWorld();

   WorldInfo getWorldInfo();

   DifficultyInstance getDifficultyForLocation(BlockPos var1);

   default Difficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   AbstractChunkProvider getChunkProvider();

   default boolean chunkExists(int p_217354_1_, int p_217354_2_) {
      return this.getChunkProvider().chunkExists(p_217354_1_, p_217354_2_);
   }

   Random getRandom();

   void notifyNeighbors(BlockPos var1, Block var2);

   @OnlyIn(Dist.CLIENT)
   BlockPos getSpawnPoint();

   void playSound(@Nullable PlayerEntity var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

   void addParticle(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12);

   void playEvent(@Nullable PlayerEntity var1, int var2, BlockPos var3, int var4);

   default void playEvent(int p_217379_1_, BlockPos p_217379_2_, int p_217379_3_) {
      this.playEvent((PlayerEntity)null, p_217379_1_, p_217379_2_, p_217379_3_);
   }

   default Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
      return IEntityReader.super.getEmptyCollisionShapes(p_223439_1_, p_223439_2_, p_223439_3_);
   }

   default boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return IEntityReader.super.checkNoEntityCollision(p_195585_1_, p_195585_2_);
   }

   default BlockPos getHeight(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return IWorldReader.super.getHeight(p_205770_1_, p_205770_2_);
   }
}
