package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JunglePyramidStructure extends ScatteredStructure<NoFeatureConfig> {
   public JunglePyramidStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51489_1_) {
      super(p_i51489_1_);
   }

   public String getStructureName() {
      return "Jungle_Pyramid";
   }

   public int getSize() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return JunglePyramidStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 14357619;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225807_1_, int p_i225807_2_, int p_i225807_3_, MutableBoundingBox p_i225807_4_, int p_i225807_5_, long p_i225807_6_) {
         super(p_i225807_1_, p_i225807_2_, p_i225807_3_, p_i225807_4_, p_i225807_5_, p_i225807_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         JunglePyramidPiece lvt_6_1_ = new JunglePyramidPiece(this.rand, p_214625_3_ * 16, p_214625_4_ * 16);
         this.components.add(lvt_6_1_);
         this.recalculateStructureSize();
      }
   }
}
