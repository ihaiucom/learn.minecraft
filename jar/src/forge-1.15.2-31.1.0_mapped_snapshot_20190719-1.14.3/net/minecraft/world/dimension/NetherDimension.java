package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherDimension extends Dimension {
   private static final Vec3d field_227177_f_ = new Vec3d(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);

   public NetherDimension(World p_i49934_1_, DimensionType p_i49934_2_) {
      super(p_i49934_1_, p_i49934_2_, 0.1F);
      this.doesWaterVaporize = true;
      this.nether = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      return field_227177_f_;
   }

   public ChunkGenerator<?> createChunkGenerator() {
      NetherGenSettings nethergensettings = (NetherGenSettings)ChunkGeneratorType.CAVES.createSettings();
      nethergensettings.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
      nethergensettings.setDefaultFluid(Blocks.LAVA.getDefaultState());
      return ChunkGeneratorType.CAVES.create(this.world, BiomeProviderType.FIXED.create(((SingleBiomeProviderSettings)BiomeProviderType.FIXED.func_226840_a_(this.world.getWorldInfo())).setBiome(Biomes.NETHER)), nethergensettings);
   }

   public boolean isSurfaceWorld() {
      return false;
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos p_206920_1_, boolean p_206920_2_) {
      return null;
   }

   @Nullable
   public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      return null;
   }

   public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
      return 0.5F;
   }

   public boolean canRespawnHere() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_) {
      return true;
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder() {
         public double getCenterX() {
            return super.getCenterX() / 8.0D;
         }

         public double getCenterZ() {
            return super.getCenterZ() / 8.0D;
         }
      };
   }
}
