package net.minecraft.item;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class BucketItem extends Item {
   private final Fluid containedBlock;
   private final Supplier<? extends Fluid> fluidSupplier;

   /** @deprecated */
   @Deprecated
   public BucketItem(Fluid p_i49025_1_, Item.Properties p_i49025_2_) {
      super(p_i49025_2_);
      this.containedBlock = p_i49025_1_;
      this.fluidSupplier = p_i49025_1_.delegate;
   }

   public BucketItem(Supplier<? extends Fluid> p_i230075_1_, Item.Properties p_i230075_2_) {
      super(p_i230075_2_);
      this.containedBlock = null;
      this.fluidSupplier = p_i230075_1_;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult raytraceresult = rayTrace(p_77659_1_, p_77659_2_, this.containedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
      ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(p_77659_2_, p_77659_1_, itemstack, raytraceresult);
      if (ret != null) {
         return ret;
      } else if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.func_226250_c_(itemstack);
      } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
         return ActionResult.func_226250_c_(itemstack);
      } else {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
         BlockPos blockpos = blockraytraceresult.getPos();
         Direction direction = blockraytraceresult.getFace();
         BlockPos blockpos1 = blockpos.offset(direction);
         if (p_77659_1_.isBlockModifiable(p_77659_2_, blockpos) && p_77659_2_.canPlayerEdit(blockpos1, direction, itemstack)) {
            BlockState blockstate1;
            if (this.containedBlock == Fluids.EMPTY) {
               blockstate1 = p_77659_1_.getBlockState(blockpos);
               if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler)blockstate1.getBlock()).pickupFluid(p_77659_1_, blockpos, blockstate1);
                  if (fluid != Fluids.EMPTY) {
                     p_77659_2_.addStat(Stats.ITEM_USED.get(this));
                     SoundEvent soundevent = this.containedBlock.getAttributes().getEmptySound();
                     if (soundevent == null) {
                        soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                     }

                     p_77659_2_.playSound(soundevent, 1.0F, 1.0F);
                     ItemStack itemstack1 = this.fillBucket(itemstack, p_77659_2_, fluid.getFilledBucket());
                     if (!p_77659_1_.isRemote) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_77659_2_, new ItemStack(fluid.getFilledBucket()));
                     }

                     return ActionResult.func_226248_a_(itemstack1);
                  }
               }

               return ActionResult.func_226251_d_(itemstack);
            } else {
               blockstate1 = p_77659_1_.getBlockState(blockpos);
               BlockPos blockpos2 = blockstate1.getBlock() instanceof ILiquidContainer && this.containedBlock == Fluids.WATER ? blockpos : blockpos1;
               if (this.tryPlaceContainedLiquid(p_77659_2_, p_77659_1_, blockpos2, blockraytraceresult)) {
                  this.onLiquidPlaced(p_77659_1_, itemstack, blockpos2);
                  if (p_77659_2_ instanceof ServerPlayerEntity) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)p_77659_2_, blockpos2, itemstack);
                  }

                  p_77659_2_.addStat(Stats.ITEM_USED.get(this));
                  return ActionResult.func_226248_a_(this.emptyBucket(itemstack, p_77659_2_));
               } else {
                  return ActionResult.func_226251_d_(itemstack);
               }
            }
         } else {
            return ActionResult.func_226251_d_(itemstack);
         }
      }
   }

   protected ItemStack emptyBucket(ItemStack p_203790_1_, PlayerEntity p_203790_2_) {
      return !p_203790_2_.abilities.isCreativeMode ? new ItemStack(Items.BUCKET) : p_203790_1_;
   }

   public void onLiquidPlaced(World p_203792_1_, ItemStack p_203792_2_, BlockPos p_203792_3_) {
   }

   private ItemStack fillBucket(ItemStack p_150910_1_, PlayerEntity p_150910_2_, Item p_150910_3_) {
      if (p_150910_2_.abilities.isCreativeMode) {
         return p_150910_1_;
      } else {
         p_150910_1_.shrink(1);
         if (p_150910_1_.isEmpty()) {
            return new ItemStack(p_150910_3_);
         } else {
            if (!p_150910_2_.inventory.addItemStackToInventory(new ItemStack(p_150910_3_))) {
               p_150910_2_.dropItem(new ItemStack(p_150910_3_), false);
            }

            return p_150910_1_;
         }
      }
   }

   public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity p_180616_1_, World p_180616_2_, BlockPos p_180616_3_, @Nullable BlockRayTraceResult p_180616_4_) {
      if (!(this.containedBlock instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState blockstate = p_180616_2_.getBlockState(p_180616_3_);
         Material material = blockstate.getMaterial();
         boolean flag = blockstate.func_227032_a_(this.containedBlock);
         if (!blockstate.isAir() && !flag && (!(blockstate.getBlock() instanceof ILiquidContainer) || !((ILiquidContainer)blockstate.getBlock()).canContainFluid(p_180616_2_, p_180616_3_, blockstate, this.containedBlock))) {
            return p_180616_4_ == null ? false : this.tryPlaceContainedLiquid(p_180616_1_, p_180616_2_, p_180616_4_.getPos().offset(p_180616_4_.getFace()), (BlockRayTraceResult)null);
         } else {
            if (p_180616_2_.dimension.doesWaterVaporize() && this.containedBlock.isIn(FluidTags.WATER)) {
               int i = p_180616_3_.getX();
               int j = p_180616_3_.getY();
               int k = p_180616_3_.getZ();
               p_180616_2_.playSound(p_180616_1_, p_180616_3_, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (p_180616_2_.rand.nextFloat() - p_180616_2_.rand.nextFloat()) * 0.8F);

               for(int l = 0; l < 8; ++l) {
                  p_180616_2_.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if (blockstate.getBlock() instanceof ILiquidContainer && this.containedBlock == Fluids.WATER) {
               if (((ILiquidContainer)blockstate.getBlock()).receiveFluid(p_180616_2_, p_180616_3_, blockstate, ((FlowingFluid)this.containedBlock).getStillFluidState(false))) {
                  this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
               }
            } else {
               if (!p_180616_2_.isRemote && flag && !material.isLiquid()) {
                  p_180616_2_.destroyBlock(p_180616_3_, true);
               }

               this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
               p_180616_2_.setBlockState(p_180616_3_, this.containedBlock.getDefaultState().getBlockState(), 11);
            }

            return true;
         }
      }
   }

   protected void playEmptySound(@Nullable PlayerEntity p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
      SoundEvent soundevent = this.containedBlock.getAttributes().getEmptySound();
      if (soundevent == null) {
         soundevent = this.containedBlock.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
      }

      p_203791_2_.playSound(p_203791_1_, p_203791_3_, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public ICapabilityProvider initCapabilities(ItemStack p_initCapabilities_1_, @Nullable CompoundNBT p_initCapabilities_2_) {
      return (ICapabilityProvider)(this.getClass() == BucketItem.class ? new FluidBucketWrapper(p_initCapabilities_1_) : super.initCapabilities(p_initCapabilities_1_, p_initCapabilities_2_));
   }

   public Fluid getFluid() {
      return (Fluid)this.fluidSupplier.get();
   }
}
