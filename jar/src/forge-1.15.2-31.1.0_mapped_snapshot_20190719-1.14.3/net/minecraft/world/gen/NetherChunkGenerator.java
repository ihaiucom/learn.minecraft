package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.Feature;

public class NetherChunkGenerator extends NoiseChunkGenerator<NetherGenSettings> {
   private final double[] field_222573_h = this.func_222572_j();

   public NetherChunkGenerator(World p_i48694_1_, BiomeProvider p_i48694_2_, NetherGenSettings p_i48694_3_) {
      super(p_i48694_1_, p_i48694_2_, 4, 8, 128, p_i48694_3_, false);
   }

   protected void func_222548_a(double[] p_222548_1_, int p_222548_2_, int p_222548_3_) {
      double lvt_4_1_ = 684.412D;
      double lvt_6_1_ = 2053.236D;
      double lvt_8_1_ = 8.555150000000001D;
      double lvt_10_1_ = 34.2206D;
      int lvt_12_1_ = true;
      int lvt_13_1_ = true;
      this.func_222546_a(p_222548_1_, p_222548_2_, p_222548_3_, 684.412D, 2053.236D, 8.555150000000001D, 34.2206D, 3, -10);
   }

   protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
      return new double[]{0.0D, 0.0D};
   }

   protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
      return this.field_222573_h[p_222545_5_];
   }

   private double[] func_222572_j() {
      double[] lvt_1_1_ = new double[this.func_222550_i()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.func_222550_i(); ++lvt_2_1_) {
         lvt_1_1_[lvt_2_1_] = Math.cos((double)lvt_2_1_ * 3.141592653589793D * 6.0D / (double)this.func_222550_i()) * 2.0D;
         double lvt_3_1_ = (double)lvt_2_1_;
         if (lvt_2_1_ > this.func_222550_i() / 2) {
            lvt_3_1_ = (double)(this.func_222550_i() - 1 - lvt_2_1_);
         }

         if (lvt_3_1_ < 4.0D) {
            lvt_3_1_ = 4.0D - lvt_3_1_;
            lvt_1_1_[lvt_2_1_] -= lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * 10.0D;
         }
      }

      return lvt_1_1_;
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      if (p_177458_1_ == EntityClassification.MONSTER) {
         if (Feature.NETHER_BRIDGE.isPositionInsideStructure(this.world, p_177458_2_)) {
            return Feature.NETHER_BRIDGE.getSpawnList();
         }

         if (Feature.NETHER_BRIDGE.isPositionInStructure(this.world, p_177458_2_) && this.world.getBlockState(p_177458_2_.down()).getBlock() == Blocks.NETHER_BRICKS) {
            return Feature.NETHER_BRIDGE.getSpawnList();
         }
      }

      return super.getPossibleCreatures(p_177458_1_, p_177458_2_);
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public int getMaxHeight() {
      return 128;
   }

   public int getSeaLevel() {
      return 32;
   }
}
