package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PhantomSpawner;

public class FlatChunkGenerator extends ChunkGenerator<FlatGenerationSettings> {
   private final Biome biome = this.func_202099_e();
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final CatSpawner field_222544_g = new CatSpawner();

   public FlatChunkGenerator(IWorld p_i48958_1_, BiomeProvider p_i48958_2_, FlatGenerationSettings p_i48958_3_) {
      super(p_i48958_1_, p_i48958_2_, p_i48958_3_);
   }

   private Biome func_202099_e() {
      Biome lvt_1_1_ = ((FlatGenerationSettings)this.settings).getBiome();
      FlatChunkGenerator.WrapperBiome lvt_2_1_ = new FlatChunkGenerator.WrapperBiome(lvt_1_1_.getSurfaceBuilder(), lvt_1_1_.getPrecipitation(), lvt_1_1_.getCategory(), lvt_1_1_.getDepth(), lvt_1_1_.getScale(), lvt_1_1_.getDefaultTemperature(), lvt_1_1_.getDownfall(), lvt_1_1_.getWaterColor(), lvt_1_1_.getWaterFogColor(), lvt_1_1_.getParent());
      Map<String, Map<String, String>> lvt_3_1_ = ((FlatGenerationSettings)this.settings).getWorldFeatures();
      Iterator var4 = lvt_3_1_.keySet().iterator();

      while(true) {
         ConfiguredFeature[] lvt_6_1_;
         int var8;
         ConfiguredFeature lvt_11_2_;
         do {
            if (!var4.hasNext()) {
               boolean lvt_4_1_ = (!((FlatGenerationSettings)this.settings).isAllAir() || lvt_1_1_ == Biomes.THE_VOID) && lvt_3_1_.containsKey("decoration");
               if (lvt_4_1_) {
                  List<GenerationStage.Decoration> lvt_5_2_ = Lists.newArrayList();
                  lvt_5_2_.add(GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
                  lvt_5_2_.add(GenerationStage.Decoration.SURFACE_STRUCTURES);
                  GenerationStage.Decoration[] var18 = GenerationStage.Decoration.values();
                  int var20 = var18.length;

                  for(var8 = 0; var8 < var20; ++var8) {
                     GenerationStage.Decoration lvt_9_1_ = var18[var8];
                     if (!lvt_5_2_.contains(lvt_9_1_)) {
                        Iterator var23 = lvt_1_1_.getFeatures(lvt_9_1_).iterator();

                        while(var23.hasNext()) {
                           lvt_11_2_ = (ConfiguredFeature)var23.next();
                           lvt_2_1_.addFeature(lvt_9_1_, lvt_11_2_);
                        }
                     }
                  }
               }

               BlockState[] lvt_5_3_ = ((FlatGenerationSettings)this.settings).getStates();

               for(int lvt_6_2_ = 0; lvt_6_2_ < lvt_5_3_.length; ++lvt_6_2_) {
                  BlockState lvt_7_1_ = lvt_5_3_[lvt_6_2_];
                  if (lvt_7_1_ != null && !Heightmap.Type.MOTION_BLOCKING.func_222684_d().test(lvt_7_1_)) {
                     ((FlatGenerationSettings)this.settings).func_214990_a(lvt_6_2_);
                     lvt_2_1_.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.func_225566_b_(new FillLayerConfig(lvt_6_2_, lvt_7_1_)).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
                  }
               }

               return lvt_2_1_;
            }

            String lvt_5_1_ = (String)var4.next();
            lvt_6_1_ = (ConfiguredFeature[])FlatGenerationSettings.STRUCTURES.get(lvt_5_1_);
         } while(lvt_6_1_ == null);

         ConfiguredFeature[] var7 = lvt_6_1_;
         var8 = lvt_6_1_.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            ConfiguredFeature<?, ?> lvt_10_1_ = var7[var9];
            lvt_2_1_.addFeature((GenerationStage.Decoration)FlatGenerationSettings.FEATURE_STAGES.get(lvt_10_1_), lvt_10_1_);
            lvt_11_2_ = ((DecoratedFeatureConfig)lvt_10_1_.config).feature;
            if (lvt_11_2_.feature instanceof Structure) {
               Structure<IFeatureConfig> lvt_12_1_ = (Structure)lvt_11_2_.feature;
               IFeatureConfig lvt_13_1_ = lvt_1_1_.getStructureConfig(lvt_12_1_);
               IFeatureConfig lvt_14_1_ = lvt_13_1_ != null ? lvt_13_1_ : (IFeatureConfig)FlatGenerationSettings.FEATURE_CONFIGS.get(lvt_10_1_);
               lvt_2_1_.func_226711_a_(lvt_12_1_.func_225566_b_(lvt_14_1_));
            }
         }
      }
   }

   public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
   }

   public int getGroundHeight() {
      IChunk lvt_1_1_ = this.world.getChunk(0, 0);
      return lvt_1_1_.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 8, 8);
   }

   protected Biome func_225552_a_(BiomeManager p_225552_1_, BlockPos p_225552_2_) {
      return this.biome;
   }

   public void makeBase(IWorld p_222537_1_, IChunk p_222537_2_) {
      BlockState[] lvt_3_1_ = ((FlatGenerationSettings)this.settings).getStates();
      BlockPos.Mutable lvt_4_1_ = new BlockPos.Mutable();
      Heightmap lvt_5_1_ = p_222537_2_.func_217303_b(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap lvt_6_1_ = p_222537_2_.func_217303_b(Heightmap.Type.WORLD_SURFACE_WG);

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_3_1_.length; ++lvt_7_1_) {
         BlockState lvt_8_1_ = lvt_3_1_[lvt_7_1_];
         if (lvt_8_1_ != null) {
            for(int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
               for(int lvt_10_1_ = 0; lvt_10_1_ < 16; ++lvt_10_1_) {
                  p_222537_2_.setBlockState(lvt_4_1_.setPos(lvt_9_1_, lvt_7_1_, lvt_10_1_), lvt_8_1_, false);
                  lvt_5_1_.update(lvt_9_1_, lvt_7_1_, lvt_10_1_, lvt_8_1_);
                  lvt_6_1_.update(lvt_9_1_, lvt_7_1_, lvt_10_1_, lvt_8_1_);
               }
            }
         }
      }

   }

   public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      BlockState[] lvt_4_1_ = ((FlatGenerationSettings)this.settings).getStates();

      for(int lvt_5_1_ = lvt_4_1_.length - 1; lvt_5_1_ >= 0; --lvt_5_1_) {
         BlockState lvt_6_1_ = lvt_4_1_[lvt_5_1_];
         if (lvt_6_1_ != null && p_222529_3_.func_222684_d().test(lvt_6_1_)) {
            return lvt_5_1_ + 1;
         }
      }

      return 0;
   }

   public void spawnMobs(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      this.phantomSpawner.tick(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_222544_g.tick(p_203222_1_, p_203222_2_, p_203222_3_);
   }

   public boolean hasStructure(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return this.biome.hasStructure(p_202094_2_);
   }

   @Nullable
   public <C extends IFeatureConfig> C getStructureConfig(Biome p_202087_1_, Structure<C> p_202087_2_) {
      return this.biome.getStructureConfig(p_202087_2_);
   }

   @Nullable
   public BlockPos findNearestStructure(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      return !((FlatGenerationSettings)this.settings).getWorldFeatures().keySet().contains(p_211403_2_.toLowerCase(Locale.ROOT)) ? null : super.findNearestStructure(p_211403_1_, p_211403_2_, p_211403_3_, p_211403_4_, p_211403_5_);
   }

   class WrapperBiome extends Biome {
      protected WrapperBiome(ConfiguredSurfaceBuilder<?> p_i51092_2_, Biome.RainType p_i51092_3_, Biome.Category p_i51092_4_, float p_i51092_5_, float p_i51092_6_, float p_i51092_7_, float p_i51092_8_, int p_i51092_9_, int p_i51092_10_, @Nullable String p_i51092_11_) {
         super((new Biome.Builder()).surfaceBuilder(p_i51092_2_).precipitation(p_i51092_3_).category(p_i51092_4_).depth(p_i51092_5_).scale(p_i51092_6_).temperature(p_i51092_7_).downfall(p_i51092_8_).waterColor(p_i51092_9_).waterFogColor(p_i51092_10_).parent(p_i51092_11_));
      }
   }
}
