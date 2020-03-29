package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractGlassBlock extends BreakableBlock {
   protected AbstractGlassBlock(Block.Properties p_i49999_1_) {
      super(p_i49999_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_220080_a(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return 1.0F;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return false;
   }

   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return false;
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return false;
   }
}
