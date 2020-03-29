package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastFurnaceBlock extends AbstractFurnaceBlock {
   protected BlastFurnaceBlock(Block.Properties p_i49992_1_) {
      super(p_i49992_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BlastFurnaceTileEntity();
   }

   protected void interactWith(World p_220089_1_, BlockPos p_220089_2_, PlayerEntity p_220089_3_) {
      TileEntity lvt_4_1_ = p_220089_1_.getTileEntity(p_220089_2_);
      if (lvt_4_1_ instanceof BlastFurnaceTileEntity) {
         p_220089_3_.openContainer((INamedContainerProvider)lvt_4_1_);
         p_220089_3_.addStat(Stats.INTERACT_WITH_BLAST_FURNACE);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(LIT)) {
         double lvt_5_1_ = (double)p_180655_3_.getX() + 0.5D;
         double lvt_7_1_ = (double)p_180655_3_.getY();
         double lvt_9_1_ = (double)p_180655_3_.getZ() + 0.5D;
         if (p_180655_4_.nextDouble() < 0.1D) {
            p_180655_2_.playSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction lvt_11_1_ = (Direction)p_180655_1_.get(FACING);
         Direction.Axis lvt_12_1_ = lvt_11_1_.getAxis();
         double lvt_13_1_ = 0.52D;
         double lvt_15_1_ = p_180655_4_.nextDouble() * 0.6D - 0.3D;
         double lvt_17_1_ = lvt_12_1_ == Direction.Axis.X ? (double)lvt_11_1_.getXOffset() * 0.52D : lvt_15_1_;
         double lvt_19_1_ = p_180655_4_.nextDouble() * 9.0D / 16.0D;
         double lvt_21_1_ = lvt_12_1_ == Direction.Axis.Z ? (double)lvt_11_1_.getZOffset() * 0.52D : lvt_15_1_;
         p_180655_2_.addParticle(ParticleTypes.SMOKE, lvt_5_1_ + lvt_17_1_, lvt_7_1_ + lvt_19_1_, lvt_9_1_ + lvt_21_1_, 0.0D, 0.0D, 0.0D);
      }
   }
}
