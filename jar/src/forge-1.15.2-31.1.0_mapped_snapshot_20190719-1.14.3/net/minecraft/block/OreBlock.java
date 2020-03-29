package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class OreBlock extends Block {
   public OreBlock(Block.Properties p_i48357_1_) {
      super(p_i48357_1_);
   }

   protected int func_220281_a(Random p_220281_1_) {
      if (this == Blocks.COAL_ORE) {
         return MathHelper.nextInt(p_220281_1_, 0, 2);
      } else if (this == Blocks.DIAMOND_ORE) {
         return MathHelper.nextInt(p_220281_1_, 3, 7);
      } else if (this == Blocks.EMERALD_ORE) {
         return MathHelper.nextInt(p_220281_1_, 3, 7);
      } else if (this == Blocks.LAPIS_ORE) {
         return MathHelper.nextInt(p_220281_1_, 2, 5);
      } else {
         return this == Blocks.NETHER_QUARTZ_ORE ? MathHelper.nextInt(p_220281_1_, 2, 5) : 0;
      }
   }

   public void spawnAdditionalDrops(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAdditionalDrops(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
   }

   public int getExpDrop(BlockState p_getExpDrop_1_, IWorldReader p_getExpDrop_2_, BlockPos p_getExpDrop_3_, int p_getExpDrop_4_, int p_getExpDrop_5_) {
      return p_getExpDrop_5_ == 0 ? this.func_220281_a(this.RANDOM) : 0;
   }
}
