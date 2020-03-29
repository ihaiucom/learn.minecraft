package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulSandBlock extends Block {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public SoulSandBlock(Block.Properties p_i48326_1_) {
      super(p_i48326_1_);
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return SHAPE;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      BubbleColumnBlock.placeBubbleColumn(p_225534_2_, p_225534_3_.up(), false);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      p_220069_2_.getPendingBlockTicks().scheduleTick(p_220069_3_, this, this.tickRate(p_220069_2_));
   }

   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return true;
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 20;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      p_220082_2_.getPendingBlockTicks().scheduleTick(p_220082_3_, this, this.tickRate(p_220082_2_));
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_229870_f_(BlockState p_229870_1_, IBlockReader p_229870_2_, BlockPos p_229870_3_) {
      return true;
   }
}
