package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class VoidStartPlatformFeature extends Feature<NoFeatureConfig> {
   private static final BlockPos field_214564_a = new BlockPos(8, 3, 8);
   private static final ChunkPos field_214565_aS;

   public VoidStartPlatformFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51417_1_) {
      super(p_i51417_1_);
   }

   private static int func_214563_a(int p_214563_0_, int p_214563_1_, int p_214563_2_, int p_214563_3_) {
      return Math.max(Math.abs(p_214563_0_ - p_214563_2_), Math.abs(p_214563_1_ - p_214563_3_));
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      ChunkPos lvt_6_1_ = new ChunkPos(p_212245_4_);
      if (func_214563_a(lvt_6_1_.x, lvt_6_1_.z, field_214565_aS.x, field_214565_aS.z) > 1) {
         return true;
      } else {
         BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable();

         for(int lvt_8_1_ = lvt_6_1_.getZStart(); lvt_8_1_ <= lvt_6_1_.getZEnd(); ++lvt_8_1_) {
            for(int lvt_9_1_ = lvt_6_1_.getXStart(); lvt_9_1_ <= lvt_6_1_.getXEnd(); ++lvt_9_1_) {
               if (func_214563_a(field_214564_a.getX(), field_214564_a.getZ(), lvt_9_1_, lvt_8_1_) <= 16) {
                  lvt_7_1_.setPos(lvt_9_1_, field_214564_a.getY(), lvt_8_1_);
                  if (lvt_7_1_.equals(field_214564_a)) {
                     p_212245_1_.setBlockState(lvt_7_1_, Blocks.COBBLESTONE.getDefaultState(), 2);
                  } else {
                     p_212245_1_.setBlockState(lvt_7_1_, Blocks.STONE.getDefaultState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   static {
      field_214565_aS = new ChunkPos(field_214564_a);
   }
}
