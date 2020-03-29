package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public class FossilsFeature extends Feature<NoFeatureConfig> {
   private static final ResourceLocation STRUCTURE_SPINE_01 = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation STRUCTURE_SPINE_02 = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation STRUCTURE_SPINE_03 = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation STRUCTURE_SPINE_04 = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation STRUCTURE_SPINE_01_COAL = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation STRUCTURE_SPINE_02_COAL = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation STRUCTURE_SPINE_03_COAL = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation STRUCTURE_SPINE_04_COAL = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation STRUCTURE_SKULL_01 = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation STRUCTURE_SKULL_02 = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation STRUCTURE_SKULL_03 = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation STRUCTURE_SKULL_04 = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation STRUCTURE_SKULL_01_COAL = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation STRUCTURE_SKULL_02_COAL = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation STRUCTURE_SKULL_03_COAL = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation STRUCTURE_SKULL_04_COAL = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] FOSSILS;
   private static final ResourceLocation[] FOSSILS_COAL;

   public FossilsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49873_1_) {
      super(p_i49873_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      Random lvt_6_1_ = p_212245_1_.getRandom();
      Rotation[] lvt_7_1_ = Rotation.values();
      Rotation lvt_8_1_ = lvt_7_1_[lvt_6_1_.nextInt(lvt_7_1_.length)];
      int lvt_9_1_ = lvt_6_1_.nextInt(FOSSILS.length);
      TemplateManager lvt_10_1_ = ((ServerWorld)p_212245_1_.getWorld()).getSaveHandler().getStructureTemplateManager();
      Template lvt_11_1_ = lvt_10_1_.getTemplateDefaulted(FOSSILS[lvt_9_1_]);
      Template lvt_12_1_ = lvt_10_1_.getTemplateDefaulted(FOSSILS_COAL[lvt_9_1_]);
      ChunkPos lvt_13_1_ = new ChunkPos(p_212245_4_);
      MutableBoundingBox lvt_14_1_ = new MutableBoundingBox(lvt_13_1_.getXStart(), 0, lvt_13_1_.getZStart(), lvt_13_1_.getXEnd(), 256, lvt_13_1_.getZEnd());
      PlacementSettings lvt_15_1_ = (new PlacementSettings()).setRotation(lvt_8_1_).setBoundingBox(lvt_14_1_).setRandom(lvt_6_1_).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
      BlockPos lvt_16_1_ = lvt_11_1_.transformedSize(lvt_8_1_);
      int lvt_17_1_ = lvt_6_1_.nextInt(16 - lvt_16_1_.getX());
      int lvt_18_1_ = lvt_6_1_.nextInt(16 - lvt_16_1_.getZ());
      int lvt_19_1_ = 256;

      int lvt_20_1_;
      for(lvt_20_1_ = 0; lvt_20_1_ < lvt_16_1_.getX(); ++lvt_20_1_) {
         for(int lvt_21_1_ = 0; lvt_21_1_ < lvt_16_1_.getZ(); ++lvt_21_1_) {
            lvt_19_1_ = Math.min(lvt_19_1_, p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, p_212245_4_.getX() + lvt_20_1_ + lvt_17_1_, p_212245_4_.getZ() + lvt_21_1_ + lvt_18_1_));
         }
      }

      lvt_20_1_ = Math.max(lvt_19_1_ - 15 - lvt_6_1_.nextInt(10), 10);
      BlockPos lvt_21_2_ = lvt_11_1_.getZeroPositionWithTransform(p_212245_4_.add(lvt_17_1_, lvt_20_1_, lvt_18_1_), Mirror.NONE, lvt_8_1_);
      IntegrityProcessor lvt_22_1_ = new IntegrityProcessor(0.9F);
      lvt_15_1_.func_215219_b().addProcessor(lvt_22_1_);
      lvt_11_1_.addBlocksToWorld(p_212245_1_, lvt_21_2_, lvt_15_1_, 4);
      lvt_15_1_.func_215220_b(lvt_22_1_);
      IntegrityProcessor lvt_23_1_ = new IntegrityProcessor(0.1F);
      lvt_15_1_.func_215219_b().addProcessor(lvt_23_1_);
      lvt_12_1_.addBlocksToWorld(p_212245_1_, lvt_21_2_, lvt_15_1_, 4);
      return true;
   }

   static {
      FOSSILS = new ResourceLocation[]{STRUCTURE_SPINE_01, STRUCTURE_SPINE_02, STRUCTURE_SPINE_03, STRUCTURE_SPINE_04, STRUCTURE_SKULL_01, STRUCTURE_SKULL_02, STRUCTURE_SKULL_03, STRUCTURE_SKULL_04};
      FOSSILS_COAL = new ResourceLocation[]{STRUCTURE_SPINE_01_COAL, STRUCTURE_SPINE_02_COAL, STRUCTURE_SPINE_03_COAL, STRUCTURE_SPINE_04_COAL, STRUCTURE_SKULL_01_COAL, STRUCTURE_SKULL_02_COAL, STRUCTURE_SKULL_03_COAL, STRUCTURE_SKULL_04_COAL};
   }
}
