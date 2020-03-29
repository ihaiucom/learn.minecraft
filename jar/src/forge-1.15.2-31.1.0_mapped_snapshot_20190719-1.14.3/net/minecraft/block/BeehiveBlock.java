package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeehiveBlock extends ContainerBlock {
   public static final Direction[] field_226871_a_;
   public static final DirectionProperty field_226872_b_;
   public static final IntegerProperty field_226873_c_;

   public BeehiveBlock(Block.Properties p_i225756_1_) {
      super(p_i225756_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(field_226873_c_, 0)).with(field_226872_b_, Direction.NORTH));
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return (Integer)p_180641_1_.get(field_226873_c_);
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      if (!p_180657_1_.isRemote && p_180657_5_ instanceof BeehiveTileEntity) {
         BeehiveTileEntity lvt_7_1_ = (BeehiveTileEntity)p_180657_5_;
         if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_180657_6_) == 0) {
            lvt_7_1_.func_226963_a_(p_180657_2_, p_180657_4_, BeehiveTileEntity.State.EMERGENCY);
            p_180657_1_.updateComparatorOutputLevel(p_180657_3_, this);
            this.func_226881_b_(p_180657_1_, p_180657_3_);
         }

         CriteriaTriggers.field_229865_L_.func_226223_a_((ServerPlayerEntity)p_180657_2_, p_180657_4_.getBlock(), p_180657_6_, lvt_7_1_.func_226971_j_());
      }

   }

   private void func_226881_b_(World p_226881_1_, BlockPos p_226881_2_) {
      List<BeeEntity> lvt_3_1_ = p_226881_1_.getEntitiesWithinAABB(BeeEntity.class, (new AxisAlignedBB(p_226881_2_)).grow(8.0D, 6.0D, 8.0D));
      if (!lvt_3_1_.isEmpty()) {
         List<PlayerEntity> lvt_4_1_ = p_226881_1_.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(p_226881_2_)).grow(8.0D, 6.0D, 8.0D));
         int lvt_5_1_ = lvt_4_1_.size();
         Iterator var6 = lvt_3_1_.iterator();

         while(var6.hasNext()) {
            BeeEntity lvt_7_1_ = (BeeEntity)var6.next();
            if (lvt_7_1_.getAttackTarget() == null) {
               lvt_7_1_.func_226391_a_((Entity)lvt_4_1_.get(p_226881_1_.rand.nextInt(lvt_5_1_)));
            }
         }
      }

   }

   public static void func_226878_a_(World p_226878_0_, BlockPos p_226878_1_) {
      spawnAsEntity(p_226878_0_, p_226878_1_, new ItemStack(Items.field_226635_pU_, 3));
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
      ItemStack lvt_8_1_ = lvt_7_1_.copy();
      int lvt_9_1_ = (Integer)p_225533_1_.get(field_226873_c_);
      boolean lvt_10_1_ = false;
      if (lvt_9_1_ >= 5) {
         if (lvt_7_1_.getItem() == Items.SHEARS) {
            p_225533_2_.playSound(p_225533_4_, p_225533_4_.func_226277_ct_(), p_225533_4_.func_226278_cu_(), p_225533_4_.func_226281_cx_(), SoundEvents.field_226133_ah_, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            func_226878_a_(p_225533_2_, p_225533_3_);
            lvt_7_1_.damageItem(1, p_225533_4_, (p_226874_1_) -> {
               p_226874_1_.sendBreakAnimation(p_225533_5_);
            });
            lvt_10_1_ = true;
         } else if (lvt_7_1_.getItem() == Items.GLASS_BOTTLE) {
            lvt_7_1_.shrink(1);
            p_225533_2_.playSound(p_225533_4_, p_225533_4_.func_226277_ct_(), p_225533_4_.func_226278_cu_(), p_225533_4_.func_226281_cx_(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            if (lvt_7_1_.isEmpty()) {
               p_225533_4_.setHeldItem(p_225533_5_, new ItemStack(Items.field_226638_pX_));
            } else if (!p_225533_4_.inventory.addItemStackToInventory(new ItemStack(Items.field_226638_pX_))) {
               p_225533_4_.dropItem(new ItemStack(Items.field_226638_pX_), false);
            }

            lvt_10_1_ = true;
         }
      }

      if (lvt_10_1_) {
         if (!CampfireBlock.func_226914_b_(p_225533_2_, p_225533_3_, 5)) {
            if (this.func_226882_d_(p_225533_2_, p_225533_3_)) {
               this.func_226881_b_(p_225533_2_, p_225533_3_);
            }

            this.func_226877_a_(p_225533_2_, p_225533_1_, p_225533_3_, p_225533_4_, BeehiveTileEntity.State.EMERGENCY);
         } else {
            this.func_226876_a_(p_225533_2_, p_225533_1_, p_225533_3_);
            if (p_225533_4_ instanceof ServerPlayerEntity) {
               CriteriaTriggers.field_229863_J_.func_226695_a_((ServerPlayerEntity)p_225533_4_, p_225533_3_, lvt_8_1_);
            }
         }

         return ActionResultType.SUCCESS;
      } else {
         return super.func_225533_a_(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      }
   }

   private boolean func_226882_d_(World p_226882_1_, BlockPos p_226882_2_) {
      TileEntity lvt_3_1_ = p_226882_1_.getTileEntity(p_226882_2_);
      if (lvt_3_1_ instanceof BeehiveTileEntity) {
         BeehiveTileEntity lvt_4_1_ = (BeehiveTileEntity)lvt_3_1_;
         return !lvt_4_1_.func_226969_f_();
      } else {
         return false;
      }
   }

   public void func_226877_a_(World p_226877_1_, BlockState p_226877_2_, BlockPos p_226877_3_, @Nullable PlayerEntity p_226877_4_, BeehiveTileEntity.State p_226877_5_) {
      this.func_226876_a_(p_226877_1_, p_226877_2_, p_226877_3_);
      TileEntity lvt_6_1_ = p_226877_1_.getTileEntity(p_226877_3_);
      if (lvt_6_1_ instanceof BeehiveTileEntity) {
         BeehiveTileEntity lvt_7_1_ = (BeehiveTileEntity)lvt_6_1_;
         lvt_7_1_.func_226963_a_(p_226877_4_, p_226877_2_, p_226877_5_);
      }

   }

   public void func_226876_a_(World p_226876_1_, BlockState p_226876_2_, BlockPos p_226876_3_) {
      p_226876_1_.setBlockState(p_226876_3_, (BlockState)p_226876_2_.with(field_226873_c_, 0), 3);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Integer)p_180655_1_.get(field_226873_c_) >= 5) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < p_180655_4_.nextInt(1) + 1; ++lvt_5_1_) {
            this.func_226879_a_(p_180655_2_, p_180655_3_, p_180655_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void func_226879_a_(World p_226879_1_, BlockPos p_226879_2_, BlockState p_226879_3_) {
      if (p_226879_3_.getFluidState().isEmpty() && p_226879_1_.rand.nextFloat() >= 0.3F) {
         VoxelShape lvt_4_1_ = p_226879_3_.getCollisionShape(p_226879_1_, p_226879_2_);
         double lvt_5_1_ = lvt_4_1_.getEnd(Direction.Axis.Y);
         if (lvt_5_1_ >= 1.0D && !p_226879_3_.isIn(BlockTags.IMPERMEABLE)) {
            double lvt_7_1_ = lvt_4_1_.getStart(Direction.Axis.Y);
            if (lvt_7_1_ > 0.0D) {
               this.func_226880_a_(p_226879_1_, p_226879_2_, lvt_4_1_, (double)p_226879_2_.getY() + lvt_7_1_ - 0.05D);
            } else {
               BlockPos lvt_9_1_ = p_226879_2_.down();
               BlockState lvt_10_1_ = p_226879_1_.getBlockState(lvt_9_1_);
               VoxelShape lvt_11_1_ = lvt_10_1_.getCollisionShape(p_226879_1_, lvt_9_1_);
               double lvt_12_1_ = lvt_11_1_.getEnd(Direction.Axis.Y);
               if ((lvt_12_1_ < 1.0D || !lvt_10_1_.func_224756_o(p_226879_1_, lvt_9_1_)) && lvt_10_1_.getFluidState().isEmpty()) {
                  this.func_226880_a_(p_226879_1_, p_226879_2_, lvt_4_1_, (double)p_226879_2_.getY() - 0.05D);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   private void func_226880_a_(World p_226880_1_, BlockPos p_226880_2_, VoxelShape p_226880_3_, double p_226880_4_) {
      this.func_226875_a_(p_226880_1_, (double)p_226880_2_.getX() + p_226880_3_.getStart(Direction.Axis.X), (double)p_226880_2_.getX() + p_226880_3_.getEnd(Direction.Axis.X), (double)p_226880_2_.getZ() + p_226880_3_.getStart(Direction.Axis.Z), (double)p_226880_2_.getZ() + p_226880_3_.getEnd(Direction.Axis.Z), p_226880_4_);
   }

   @OnlyIn(Dist.CLIENT)
   private void func_226875_a_(World p_226875_1_, double p_226875_2_, double p_226875_4_, double p_226875_6_, double p_226875_8_, double p_226875_10_) {
      p_226875_1_.addParticle(ParticleTypes.field_229427_ag_, MathHelper.lerp(p_226875_1_.rand.nextDouble(), p_226875_2_, p_226875_4_), p_226875_10_, MathHelper.lerp(p_226875_1_.rand.nextDouble(), p_226875_6_, p_226875_8_), 0.0D, 0.0D, 0.0D);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(field_226872_b_, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_226873_c_, field_226872_b_);
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BeehiveTileEntity();
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isRemote && p_176208_4_.isCreative() && p_176208_1_.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
         TileEntity lvt_5_1_ = p_176208_1_.getTileEntity(p_176208_2_);
         if (lvt_5_1_ instanceof BeehiveTileEntity) {
            BeehiveTileEntity lvt_6_1_ = (BeehiveTileEntity)lvt_5_1_;
            ItemStack lvt_7_1_ = new ItemStack(this);
            int lvt_8_1_ = (Integer)p_176208_3_.get(field_226873_c_);
            boolean lvt_9_1_ = !lvt_6_1_.func_226969_f_();
            if (!lvt_9_1_ && lvt_8_1_ == 0) {
               return;
            }

            CompoundNBT lvt_10_2_;
            if (lvt_9_1_) {
               lvt_10_2_ = new CompoundNBT();
               lvt_10_2_.put("Bees", lvt_6_1_.func_226974_m_());
               lvt_7_1_.setTagInfo("BlockEntityTag", lvt_10_2_);
            }

            lvt_10_2_ = new CompoundNBT();
            lvt_10_2_.putInt("honey_level", lvt_8_1_);
            lvt_7_1_.setTagInfo("BlockStateTag", lvt_10_2_);
            ItemEntity lvt_11_1_ = new ItemEntity(p_176208_1_, (double)p_176208_2_.getX(), (double)p_176208_2_.getY(), (double)p_176208_2_.getZ(), lvt_7_1_);
            lvt_11_1_.setDefaultPickupDelay();
            p_176208_1_.addEntity(lvt_11_1_);
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      Entity lvt_3_1_ = (Entity)p_220076_2_.get(LootParameters.THIS_ENTITY);
      if (lvt_3_1_ instanceof TNTEntity || lvt_3_1_ instanceof CreeperEntity || lvt_3_1_ instanceof WitherSkullEntity || lvt_3_1_ instanceof WitherEntity || lvt_3_1_ instanceof TNTMinecartEntity) {
         TileEntity lvt_4_1_ = (TileEntity)p_220076_2_.get(LootParameters.BLOCK_ENTITY);
         if (lvt_4_1_ instanceof BeehiveTileEntity) {
            BeehiveTileEntity lvt_5_1_ = (BeehiveTileEntity)lvt_4_1_;
            lvt_5_1_.func_226963_a_((PlayerEntity)null, p_220076_1_, BeehiveTileEntity.State.EMERGENCY);
         }
      }

      return super.getDrops(p_220076_1_, p_220076_2_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_4_.getBlockState(p_196271_6_).getBlock() instanceof FireBlock) {
         TileEntity lvt_7_1_ = p_196271_4_.getTileEntity(p_196271_5_);
         if (lvt_7_1_ instanceof BeehiveTileEntity) {
            BeehiveTileEntity lvt_8_1_ = (BeehiveTileEntity)lvt_7_1_;
            lvt_8_1_.func_226963_a_((PlayerEntity)null, p_196271_1_, BeehiveTileEntity.State.EMERGENCY);
         }
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   static {
      field_226871_a_ = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
      field_226872_b_ = HorizontalBlock.HORIZONTAL_FACING;
      field_226873_c_ = BlockStateProperties.field_227036_ao_;
   }
}
