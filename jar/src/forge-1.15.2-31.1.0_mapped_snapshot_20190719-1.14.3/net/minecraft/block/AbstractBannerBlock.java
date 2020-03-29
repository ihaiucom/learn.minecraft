package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractBannerBlock extends ContainerBlock {
   private final DyeColor color;

   protected AbstractBannerBlock(DyeColor p_i48453_1_, Block.Properties p_i48453_2_) {
      super(p_i48453_2_);
      this.color = p_i48453_1_;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BannerTileEntity(this.color);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof BannerTileEntity) {
            ((BannerTileEntity)tileentity).func_213136_a(p_180633_5_.getDisplayName());
         }
      }

   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      TileEntity tileentity = p_185473_1_.getTileEntity(p_185473_2_);
      return tileentity instanceof BannerTileEntity ? ((BannerTileEntity)tileentity).getItem(p_185473_3_) : super.getItem(p_185473_1_, p_185473_2_, p_185473_3_);
   }

   public DyeColor getColor() {
      return this.color;
   }
}
