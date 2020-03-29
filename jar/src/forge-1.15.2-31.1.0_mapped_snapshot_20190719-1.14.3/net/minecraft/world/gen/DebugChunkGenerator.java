package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;

public class DebugChunkGenerator extends ChunkGenerator<DebugGenerationSettings> {
   private static final List<BlockState> ALL_VALID_STATES;
   private static final int GRID_WIDTH;
   private static final int GRID_HEIGHT;
   protected static final BlockState AIR;
   protected static final BlockState BARRIER;

   public DebugChunkGenerator(IWorld p_i48959_1_, BiomeProvider p_i48959_2_, DebugGenerationSettings p_i48959_3_) {
      super(p_i48959_1_, p_i48959_2_, p_i48959_3_);
   }

   public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
   }

   public void func_225550_a_(BiomeManager p_225550_1_, IChunk p_225550_2_, GenerationStage.Carving p_225550_3_) {
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public void decorate(WorldGenRegion p_202092_1_) {
      BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable();
      int lvt_3_1_ = p_202092_1_.getMainChunkX();
      int lvt_4_1_ = p_202092_1_.getMainChunkZ();

      for(int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_) {
         for(int lvt_6_1_ = 0; lvt_6_1_ < 16; ++lvt_6_1_) {
            int lvt_7_1_ = (lvt_3_1_ << 4) + lvt_5_1_;
            int lvt_8_1_ = (lvt_4_1_ << 4) + lvt_6_1_;
            p_202092_1_.setBlockState(lvt_2_1_.setPos(lvt_7_1_, 60, lvt_8_1_), BARRIER, 2);
            BlockState lvt_9_1_ = getBlockStateFor(lvt_7_1_, lvt_8_1_);
            if (lvt_9_1_ != null) {
               p_202092_1_.setBlockState(lvt_2_1_.setPos(lvt_7_1_, 70, lvt_8_1_), lvt_9_1_, 2);
            }
         }
      }

   }

   public void makeBase(IWorld p_222537_1_, IChunk p_222537_2_) {
   }

   public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      return 0;
   }

   public static BlockState getBlockStateFor(int p_177461_0_, int p_177461_1_) {
      BlockState lvt_2_1_ = AIR;
      if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0) {
         p_177461_0_ /= 2;
         p_177461_1_ /= 2;
         if (p_177461_0_ <= GRID_WIDTH && p_177461_1_ <= GRID_HEIGHT) {
            int lvt_3_1_ = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_);
            if (lvt_3_1_ < ALL_VALID_STATES.size()) {
               lvt_2_1_ = (BlockState)ALL_VALID_STATES.get(lvt_3_1_);
            }
         }
      }

      return lvt_2_1_;
   }

   static {
      ALL_VALID_STATES = (List)StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_199812_0_) -> {
         return p_199812_0_.getStateContainer().getValidStates().stream();
      }).collect(Collectors.toList());
      GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt((float)ALL_VALID_STATES.size()));
      GRID_HEIGHT = MathHelper.ceil((float)ALL_VALID_STATES.size() / (float)GRID_WIDTH);
      AIR = Blocks.AIR.getDefaultState();
      BARRIER = Blocks.BARRIER.getDefaultState();
   }
}
