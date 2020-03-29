package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends ScatteredStructure<OceanRuinConfig> {
   public OceanRuinStructure(Function<Dynamic<?>, ? extends OceanRuinConfig> p_i51348_1_) {
      super(p_i51348_1_);
   }

   public String getStructureName() {
      return "Ocean_Ruin";
   }

   public int getSize() {
      return 3;
   }

   protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.getSettings().getOceanRuinDistance();
   }

   protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().getOceanRuinSeparation();
   }

   public Structure.IStartFactory getStartFactory() {
      return OceanRuinStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 14357621;
   }

   public static enum Type {
      WARM("warm"),
      COLD("cold");

      private static final Map<String, OceanRuinStructure.Type> field_215137_c = (Map)Arrays.stream(values()).collect(Collectors.toMap(OceanRuinStructure.Type::func_215135_a, (p_215134_0_) -> {
         return p_215134_0_;
      }));
      private final String field_215138_d;

      private Type(String p_i50621_3_) {
         this.field_215138_d = p_i50621_3_;
      }

      public String func_215135_a() {
         return this.field_215138_d;
      }

      public static OceanRuinStructure.Type func_215136_a(String p_215136_0_) {
         return (OceanRuinStructure.Type)field_215137_c.get(p_215136_0_);
      }
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225875_1_, int p_i225875_2_, int p_i225875_3_, MutableBoundingBox p_i225875_4_, int p_i225875_5_, long p_i225875_6_) {
         super(p_i225875_1_, p_i225875_2_, p_i225875_3_, p_i225875_4_, p_i225875_5_, p_i225875_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         OceanRuinConfig lvt_6_1_ = (OceanRuinConfig)p_214625_1_.getStructureConfig(p_214625_5_, Feature.OCEAN_RUIN);
         int lvt_7_1_ = p_214625_3_ * 16;
         int lvt_8_1_ = p_214625_4_ * 16;
         BlockPos lvt_9_1_ = new BlockPos(lvt_7_1_, 90, lvt_8_1_);
         Rotation lvt_10_1_ = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         OceanRuinPieces.func_204041_a(p_214625_2_, lvt_9_1_, lvt_10_1_, this.components, this.rand, lvt_6_1_);
         this.recalculateStructureSize();
      }
   }
}
