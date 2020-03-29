package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.EndGenerationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndDimension extends Dimension {
   public static final BlockPos SPAWN = new BlockPos(100, 50, 0);
   private final DragonFightManager dragonFightManager;

   public EndDimension(World p_i49932_1_, DimensionType p_i49932_2_) {
      super(p_i49932_1_, p_i49932_2_, 0.0F);
      CompoundNBT compoundnbt = p_i49932_1_.getWorldInfo().getDimensionData(p_i49932_2_);
      this.dragonFightManager = p_i49932_1_ instanceof ServerWorld ? new DragonFightManager((ServerWorld)p_i49932_1_, compoundnbt.getCompound("DragonFight"), this) : null;
   }

   public ChunkGenerator<?> createChunkGenerator() {
      EndGenerationSettings endgenerationsettings = (EndGenerationSettings)ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
      endgenerationsettings.setDefaultBlock(Blocks.END_STONE.getDefaultState());
      endgenerationsettings.setDefaultFluid(Blocks.AIR.getDefaultState());
      endgenerationsettings.setSpawnPos(this.getSpawnCoordinate());
      return ChunkGeneratorType.FLOATING_ISLANDS.create(this.world, BiomeProviderType.THE_END.create(BiomeProviderType.THE_END.func_226840_a_(this.world.getWorldInfo())), endgenerationsettings);
   }

   public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
      return 0.0F;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_) {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      int i = 10518688;
      float f = MathHelper.cos(p_76562_1_ * 6.2831855F) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.627451F;
      float f2 = 0.5019608F;
      float f3 = 0.627451F;
      f1 *= f * 0.0F + 0.15F;
      f2 *= f * 0.0F + 0.15F;
      f3 *= f * 0.0F + 0.15F;
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSkyColored() {
      return false;
   }

   public boolean canRespawnHere() {
      return false;
   }

   public boolean isSurfaceWorld() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float getCloudHeight() {
      return 8.0F;
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos p_206920_1_, boolean p_206920_2_) {
      Random random = new Random(this.world.getSeed());
      BlockPos blockpos = new BlockPos(p_206920_1_.getXStart() + random.nextInt(15), 0, p_206920_1_.getZEnd() + random.nextInt(15));
      return this.world.getGroundAboveSeaLevel(blockpos).getMaterial().blocksMovement() ? blockpos : null;
   }

   public BlockPos getSpawnCoordinate() {
      return SPAWN;
   }

   @Nullable
   public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      return this.findSpawn(new ChunkPos(p_206921_1_ >> 4, p_206921_2_ >> 4), p_206921_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_) {
      return false;
   }

   public void onWorldSave() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (this.dragonFightManager != null) {
         compoundnbt.put("DragonFight", this.dragonFightManager.write());
      }

      this.world.getWorldInfo().setDimensionData(this.world.getDimension().getType(), compoundnbt);
   }

   public void tick() {
      if (this.dragonFightManager != null) {
         this.dragonFightManager.tick();
      }

   }

   @Nullable
   public DragonFightManager getDragonFightManager() {
      return this.dragonFightManager;
   }
}
