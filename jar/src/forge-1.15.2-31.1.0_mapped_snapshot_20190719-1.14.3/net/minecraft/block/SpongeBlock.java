package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
   protected SpongeBlock(Block.Properties p_i48325_1_) {
      super(p_i48325_1_);
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         this.tryAbsorb(p_220082_2_, p_220082_3_);
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      this.tryAbsorb(p_220069_2_, p_220069_3_);
      super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
   }

   protected void tryAbsorb(World p_196510_1_, BlockPos p_196510_2_) {
      if (this.absorb(p_196510_1_, p_196510_2_)) {
         p_196510_1_.setBlockState(p_196510_2_, Blocks.WET_SPONGE.getDefaultState(), 2);
         p_196510_1_.playEvent(2001, p_196510_2_, Block.getStateId(Blocks.WATER.getDefaultState()));
      }

   }

   private boolean absorb(World p_176312_1_, BlockPos p_176312_2_) {
      Queue<Tuple<BlockPos, Integer>> lvt_3_1_ = Lists.newLinkedList();
      lvt_3_1_.add(new Tuple(p_176312_2_, 0));
      int lvt_4_1_ = 0;

      while(!lvt_3_1_.isEmpty()) {
         Tuple<BlockPos, Integer> lvt_5_1_ = (Tuple)lvt_3_1_.poll();
         BlockPos lvt_6_1_ = (BlockPos)lvt_5_1_.getA();
         int lvt_7_1_ = (Integer)lvt_5_1_.getB();
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction lvt_11_1_ = var8[var10];
            BlockPos lvt_12_1_ = lvt_6_1_.offset(lvt_11_1_);
            BlockState lvt_13_1_ = p_176312_1_.getBlockState(lvt_12_1_);
            IFluidState lvt_14_1_ = p_176312_1_.getFluidState(lvt_12_1_);
            Material lvt_15_1_ = lvt_13_1_.getMaterial();
            if (lvt_14_1_.isTagged(FluidTags.WATER)) {
               if (lvt_13_1_.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)lvt_13_1_.getBlock()).pickupFluid(p_176312_1_, lvt_12_1_, lvt_13_1_) != Fluids.EMPTY) {
                  ++lvt_4_1_;
                  if (lvt_7_1_ < 6) {
                     lvt_3_1_.add(new Tuple(lvt_12_1_, lvt_7_1_ + 1));
                  }
               } else if (lvt_13_1_.getBlock() instanceof FlowingFluidBlock) {
                  p_176312_1_.setBlockState(lvt_12_1_, Blocks.AIR.getDefaultState(), 3);
                  ++lvt_4_1_;
                  if (lvt_7_1_ < 6) {
                     lvt_3_1_.add(new Tuple(lvt_12_1_, lvt_7_1_ + 1));
                  }
               } else if (lvt_15_1_ == Material.OCEAN_PLANT || lvt_15_1_ == Material.SEA_GRASS) {
                  TileEntity lvt_16_1_ = lvt_13_1_.getBlock().hasTileEntity() ? p_176312_1_.getTileEntity(lvt_12_1_) : null;
                  spawnDrops(lvt_13_1_, p_176312_1_, lvt_12_1_, lvt_16_1_);
                  p_176312_1_.setBlockState(lvt_12_1_, Blocks.AIR.getDefaultState(), 3);
                  ++lvt_4_1_;
                  if (lvt_7_1_ < 6) {
                     lvt_3_1_.add(new Tuple(lvt_12_1_, lvt_7_1_ + 1));
                  }
               }
            }
         }

         if (lvt_4_1_ > 64) {
            break;
         }
      }

      return lvt_4_1_ > 0;
   }
}
