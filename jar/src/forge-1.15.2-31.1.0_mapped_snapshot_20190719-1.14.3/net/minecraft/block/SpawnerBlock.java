package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SpawnerBlock extends ContainerBlock {
   protected SpawnerBlock(Block.Properties p_i48364_1_) {
      super(p_i48364_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new MobSpawnerTileEntity();
   }

   public void spawnAdditionalDrops(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAdditionalDrops(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
   }

   public int getExpDrop(BlockState p_getExpDrop_1_, IWorldReader p_getExpDrop_2_, BlockPos p_getExpDrop_3_, int p_getExpDrop_4_, int p_getExpDrop_5_) {
      return 15 + this.RANDOM.nextInt(15) + this.RANDOM.nextInt(15);
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }
}
