package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class CampfireBlock extends ContainerBlock implements IWaterLoggable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
   public static final BooleanProperty LIT;
   public static final BooleanProperty SIGNAL_FIRE;
   public static final BooleanProperty WATERLOGGED;
   public static final DirectionProperty FACING;
   private static final VoxelShape field_226912_f_;

   public CampfireBlock(Block.Properties p_i49989_1_) {
      super(p_i49989_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LIT, true)).with(SIGNAL_FIRE, false)).with(WATERLOGGED, false)).with(FACING, Direction.NORTH));
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if ((Boolean)p_225533_1_.get(LIT)) {
         TileEntity tileentity = p_225533_2_.getTileEntity(p_225533_3_);
         if (tileentity instanceof CampfireTileEntity) {
            CampfireTileEntity campfiretileentity = (CampfireTileEntity)tileentity;
            ItemStack itemstack = p_225533_4_.getHeldItem(p_225533_5_);
            Optional<CampfireCookingRecipe> optional = campfiretileentity.findMatchingRecipe(itemstack);
            if (optional.isPresent()) {
               if (!p_225533_2_.isRemote && campfiretileentity.addItem(p_225533_4_.abilities.isCreativeMode ? itemstack.copy() : itemstack, ((CampfireCookingRecipe)optional.get()).getCookTime())) {
                  p_225533_4_.addStat(Stats.INTERACT_WITH_CAMPFIRE);
                  return ActionResultType.SUCCESS;
               }

               return ActionResultType.CONSUME;
            }
         }
      }

      return ActionResultType.PASS;
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.isImmuneToFire() && (Boolean)p_196262_1_.get(LIT) && p_196262_4_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_196262_4_)) {
         p_196262_4_.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
      }

      super.onEntityCollision(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof CampfireTileEntity) {
            InventoryHelper.dropItems(p_196243_2_, p_196243_3_, ((CampfireTileEntity)tileentity).getInventory());
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }

   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IWorld iworld = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      boolean flag = iworld.getFluidState(blockpos).getFluid() == Fluids.WATER;
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, flag)).with(SIGNAL_FIRE, this.isHayBlock(iworld.getBlockState(blockpos.down())))).with(LIT, !flag)).with(FACING, p_196258_1_.getPlacementHorizontalFacing());
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_ == Direction.DOWN ? (BlockState)p_196271_1_.with(SIGNAL_FIRE, this.isHayBlock(p_196271_3_)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   private boolean isHayBlock(BlockState p_220099_1_) {
      return p_220099_1_.getBlock() == Blocks.HAY_BLOCK;
   }

   public int getLightValue(BlockState p_149750_1_) {
      return (Boolean)p_149750_1_.get(LIT) ? super.getLightValue(p_149750_1_) : 0;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(LIT)) {
         if (p_180655_4_.nextInt(10) == 0) {
            p_180655_2_.playSound((double)((float)p_180655_3_.getX() + 0.5F), (double)((float)p_180655_3_.getY() + 0.5F), (double)((float)p_180655_3_.getZ() + 0.5F), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + p_180655_4_.nextFloat(), p_180655_4_.nextFloat() * 0.7F + 0.6F, false);
         }

         if (p_180655_4_.nextInt(5) == 0) {
            for(int i = 0; i < p_180655_4_.nextInt(1) + 1; ++i) {
               p_180655_2_.addParticle(ParticleTypes.LAVA, (double)((float)p_180655_3_.getX() + 0.5F), (double)((float)p_180655_3_.getY() + 0.5F), (double)((float)p_180655_3_.getZ() + 0.5F), (double)(p_180655_4_.nextFloat() / 2.0F), 5.0E-5D, (double)(p_180655_4_.nextFloat() / 2.0F));
            }
         }
      }

   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!(Boolean)p_204509_3_.get(BlockStateProperties.WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         boolean flag = (Boolean)p_204509_3_.get(LIT);
         if (flag) {
            if (p_204509_1_.isRemote()) {
               for(int i = 0; i < 20; ++i) {
                  func_220098_a(p_204509_1_.getWorld(), p_204509_2_, (Boolean)p_204509_3_.get(SIGNAL_FIRE), true);
               }
            } else {
               p_204509_1_.playSound((PlayerEntity)null, p_204509_2_, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            TileEntity tileentity = p_204509_1_.getTileEntity(p_204509_2_);
            if (tileentity instanceof CampfireTileEntity) {
               ((CampfireTileEntity)tileentity).func_213986_d();
            }
         }

         p_204509_1_.setBlockState(p_204509_2_, (BlockState)((BlockState)p_204509_3_.with(WATERLOGGED, true)).with(LIT, false), 3);
         p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, p_204509_4_.getFluid(), p_204509_4_.getFluid().getTickRate(p_204509_1_));
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private Entity func_226913_a_(Entity p_226913_1_) {
      if (p_226913_1_ instanceof AbstractFireballEntity) {
         return ((AbstractFireballEntity)p_226913_1_).shootingEntity;
      } else {
         return p_226913_1_ instanceof AbstractArrowEntity ? ((AbstractArrowEntity)p_226913_1_).getShooter() : null;
      }
   }

   public void onProjectileCollision(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
      if (!p_220066_1_.isRemote) {
         boolean flag = p_220066_4_ instanceof AbstractFireballEntity || p_220066_4_ instanceof AbstractArrowEntity && p_220066_4_.isBurning();
         if (flag) {
            Entity entity = this.func_226913_a_(p_220066_4_);
            boolean flag1 = entity == null || entity instanceof PlayerEntity || ForgeEventFactory.getMobGriefingEvent(p_220066_1_, entity);
            if (flag1 && !(Boolean)p_220066_2_.get(LIT) && !(Boolean)p_220066_2_.get(WATERLOGGED)) {
               BlockPos blockpos = p_220066_3_.getPos();
               p_220066_1_.setBlockState(blockpos, (BlockState)p_220066_2_.with(BlockStateProperties.LIT, true), 11);
            }
         }
      }

   }

   public static void func_220098_a(World p_220098_0_, BlockPos p_220098_1_, boolean p_220098_2_, boolean p_220098_3_) {
      Random random = p_220098_0_.getRandom();
      BasicParticleType basicparticletype = p_220098_2_ ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
      p_220098_0_.func_217404_b(basicparticletype, true, (double)p_220098_1_.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)p_220098_1_.getY() + random.nextDouble() + random.nextDouble(), (double)p_220098_1_.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
      if (p_220098_3_) {
         p_220098_0_.addParticle(ParticleTypes.SMOKE, (double)p_220098_1_.getX() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), (double)p_220098_1_.getY() + 0.4D, (double)p_220098_1_.getZ() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
      }

   }

   public static boolean func_226914_b_(World p_226914_0_, BlockPos p_226914_1_, int p_226914_2_) {
      for(int i = 1; i <= p_226914_2_; ++i) {
         BlockPos blockpos = p_226914_1_.down(i);
         BlockState blockstate = p_226914_0_.getBlockState(blockpos);
         if (func_226915_i_(blockstate)) {
            return true;
         }

         boolean flag = VoxelShapes.compare(field_226912_f_, blockstate.getCollisionShape(p_226914_0_, p_226914_1_, ISelectionContext.dummy()), IBooleanFunction.AND);
         if (flag) {
            BlockState blockstate1 = p_226914_0_.getBlockState(blockpos.down());
            return func_226915_i_(blockstate1);
         }
      }

      return false;
   }

   private static boolean func_226915_i_(BlockState p_226915_0_) {
      return p_226915_0_.getBlock() == Blocks.CAMPFIRE && (Boolean)p_226915_0_.get(LIT);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new CampfireTileEntity();
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      LIT = BlockStateProperties.LIT;
      SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      field_226912_f_ = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   }
}
