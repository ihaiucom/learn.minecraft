package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ContainerBlock extends Block implements ITileEntityProvider {
   protected ContainerBlock(Block.Properties p_i48446_1_) {
      super(p_i48446_1_);
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public boolean eventReceived(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.eventReceived(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity lvt_6_1_ = p_189539_2_.getTileEntity(p_189539_3_);
      return lvt_6_1_ == null ? false : lvt_6_1_.receiveClientEvent(p_189539_4_, p_189539_5_);
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      TileEntity lvt_4_1_ = p_220052_2_.getTileEntity(p_220052_3_);
      return lvt_4_1_ instanceof INamedContainerProvider ? (INamedContainerProvider)lvt_4_1_ : null;
   }
}
