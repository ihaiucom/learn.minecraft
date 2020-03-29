package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ShipwreckStructure extends ScatteredStructure<ShipwreckConfig> {
   public ShipwreckStructure(Function<Dynamic<?>, ? extends ShipwreckConfig> p_i51440_1_) {
      super(p_i51440_1_);
   }

   public String getStructureName() {
      return "Shipwreck";
   }

   public int getSize() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return ShipwreckStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 165745295;
   }

   protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.getSettings().getShipwreckDistance();
   }

   protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().getShipwreckSeparation();
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225817_1_, int p_i225817_2_, int p_i225817_3_, MutableBoundingBox p_i225817_4_, int p_i225817_5_, long p_i225817_6_) {
         super(p_i225817_1_, p_i225817_2_, p_i225817_3_, p_i225817_4_, p_i225817_5_, p_i225817_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         ShipwreckConfig lvt_6_1_ = (ShipwreckConfig)p_214625_1_.getStructureConfig(p_214625_5_, Feature.SHIPWRECK);
         Rotation lvt_7_1_ = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         BlockPos lvt_8_1_ = new BlockPos(p_214625_3_ * 16, 90, p_214625_4_ * 16);
         ShipwreckPieces.func_204760_a(p_214625_2_, lvt_8_1_, lvt_7_1_, this.components, this.rand, lvt_6_1_);
         this.recalculateStructureSize();
      }
   }
}
