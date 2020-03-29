package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTBlock extends Block {
   public static final BooleanProperty UNSTABLE;

   public TNTBlock(Block.Properties p_i48309_1_) {
      super(p_i48309_1_);
      this.setDefaultState((BlockState)this.getDefaultState().with(UNSTABLE, false));
   }

   public void catchFire(BlockState p_catchFire_1_, World p_catchFire_2_, BlockPos p_catchFire_3_, @Nullable Direction p_catchFire_4_, @Nullable LivingEntity p_catchFire_5_) {
      explode(p_catchFire_2_, p_catchFire_3_, p_catchFire_5_);
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && p_220082_2_.isBlockPowered(p_220082_3_)) {
         this.catchFire(p_220082_1_, p_220082_2_, p_220082_3_, (Direction)null, (LivingEntity)null);
         p_220082_2_.removeBlock(p_220082_3_, false);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_2_.isBlockPowered(p_220069_3_)) {
         this.catchFire(p_220069_1_, p_220069_2_, p_220069_3_, (Direction)null, (LivingEntity)null);
         p_220069_2_.removeBlock(p_220069_3_, false);
      }

   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isRemote() && !p_176208_4_.isCreative() && (Boolean)p_176208_3_.get(UNSTABLE)) {
         this.catchFire(p_176208_3_, p_176208_1_, p_176208_2_, (Direction)null, (LivingEntity)null);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public void onExplosionDestroy(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
      if (!p_180652_1_.isRemote) {
         TNTEntity tntentity = new TNTEntity(p_180652_1_, (double)((float)p_180652_2_.getX() + 0.5F), (double)p_180652_2_.getY(), (double)((float)p_180652_2_.getZ() + 0.5F), p_180652_3_.getExplosivePlacedBy());
         tntentity.setFuse((short)(p_180652_1_.rand.nextInt(tntentity.getFuse() / 4) + tntentity.getFuse() / 8));
         p_180652_1_.addEntity(tntentity);
      }

   }

   /** @deprecated */
   @Deprecated
   public static void explode(World p_196534_0_, BlockPos p_196534_1_) {
      explode(p_196534_0_, p_196534_1_, (LivingEntity)null);
   }

   /** @deprecated */
   @Deprecated
   private static void explode(World p_196535_0_, BlockPos p_196535_1_, @Nullable LivingEntity p_196535_2_) {
      if (!p_196535_0_.isRemote) {
         TNTEntity tntentity = new TNTEntity(p_196535_0_, (double)p_196535_1_.getX() + 0.5D, (double)p_196535_1_.getY(), (double)p_196535_1_.getZ() + 0.5D, p_196535_2_);
         p_196535_0_.addEntity(tntentity);
         p_196535_0_.playSound((PlayerEntity)null, tntentity.func_226277_ct_(), tntentity.func_226278_cu_(), tntentity.func_226281_cx_(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack itemstack = p_225533_4_.getHeldItem(p_225533_5_);
      Item item = itemstack.getItem();
      if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
         return super.func_225533_a_(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      } else {
         this.catchFire(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_6_.getFace(), p_225533_4_);
         p_225533_2_.setBlockState(p_225533_3_, Blocks.AIR.getDefaultState(), 11);
         if (!p_225533_4_.isCreative()) {
            if (item == Items.FLINT_AND_STEEL) {
               itemstack.damageItem(1, p_225533_4_, (p_lambda$func_225533_a_$0_1_) -> {
                  p_lambda$func_225533_a_$0_1_.sendBreakAnimation(p_225533_5_);
               });
            } else {
               itemstack.shrink(1);
            }
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void onProjectileCollision(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
      if (!p_220066_1_.isRemote && p_220066_4_ instanceof AbstractArrowEntity) {
         AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)p_220066_4_;
         Entity entity = abstractarrowentity.getShooter();
         if (abstractarrowentity.isBurning()) {
            BlockPos blockpos = p_220066_3_.getPos();
            this.catchFire(p_220066_2_, p_220066_1_, blockpos, (Direction)null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
            p_220066_1_.removeBlock(blockpos, false);
         }
      }

   }

   public boolean canDropFromExplosion(Explosion p_149659_1_) {
      return false;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UNSTABLE);
   }

   static {
      UNSTABLE = BlockStateProperties.UNSTABLE;
   }
}
