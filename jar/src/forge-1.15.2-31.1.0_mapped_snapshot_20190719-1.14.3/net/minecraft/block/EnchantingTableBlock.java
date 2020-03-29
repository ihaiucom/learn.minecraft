package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantingTableBlock extends ContainerBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected EnchantingTableBlock(Block.Properties p_i48408_1_) {
      super(p_i48408_1_);
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      super.animateTick(p_180655_1_, p_180655_2_, p_180655_3_, p_180655_4_);

      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (i > -2 && i < 2 && j == -1) {
               j = 2;
            }

            if (p_180655_4_.nextInt(16) == 0) {
               for(int k = 0; k <= 1; ++k) {
                  BlockPos blockpos = p_180655_3_.add(i, k, j);
                  if (p_180655_2_.getBlockState(blockpos).getEnchantPowerBonus(p_180655_2_, p_180655_3_) > 0.0F) {
                     if (!p_180655_2_.isAirBlock(p_180655_3_.add(i / 2, 0, j / 2))) {
                        break;
                     }

                     p_180655_2_.addParticle(ParticleTypes.ENCHANT, (double)p_180655_3_.getX() + 0.5D, (double)p_180655_3_.getY() + 2.0D, (double)p_180655_3_.getZ() + 0.5D, (double)((float)i + p_180655_4_.nextFloat()) - 0.5D, (double)((float)k - p_180655_4_.nextFloat() - 1.0F), (double)((float)j + p_180655_4_.nextFloat()) - 0.5D);
                  }
               }
            }
         }
      }

   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new EnchantingTableTileEntity();
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openContainer(p_225533_1_.getContainer(p_225533_2_, p_225533_3_));
         return ActionResultType.SUCCESS;
      }
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      TileEntity tileentity = p_220052_2_.getTileEntity(p_220052_3_);
      if (tileentity instanceof EnchantingTableTileEntity) {
         ITextComponent itextcomponent = ((INameable)tileentity).getDisplayName();
         return new SimpleNamedContainerProvider((p_lambda$getContainer$0_2_, p_lambda$getContainer$0_3_, p_lambda$getContainer$0_4_) -> {
            return new EnchantmentContainer(p_lambda$getContainer$0_2_, p_lambda$getContainer$0_3_, IWorldPosCallable.of(p_220052_2_, p_220052_3_));
         }, itextcomponent);
      } else {
         return null;
      }
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof EnchantingTableTileEntity) {
            ((EnchantingTableTileEntity)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
