package net.minecraft.block;

import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSkullBlock extends ContainerBlock {
   private final SkullBlock.ISkullType skullType;

   public AbstractSkullBlock(SkullBlock.ISkullType p_i48452_1_, Block.Properties p_i48452_2_) {
      super(p_i48452_2_);
      this.skullType = p_i48452_1_;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new SkullTileEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public SkullBlock.ISkullType getSkullType() {
      return this.skullType;
   }
}
