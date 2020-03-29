package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CauldronBlock extends Block {
   public static final IntegerProperty LEVEL;
   private static final VoxelShape INSIDE;
   protected static final VoxelShape SHAPE;

   public CauldronBlock(Block.Properties p_i48431_1_) {
      super(p_i48431_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LEVEL, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public VoxelShape getRaytraceShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return INSIDE;
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      int lvt_5_1_ = (Integer)p_196262_1_.get(LEVEL);
      float lvt_6_1_ = (float)p_196262_3_.getY() + (6.0F + (float)(3 * lvt_5_1_)) / 16.0F;
      if (!p_196262_2_.isRemote && p_196262_4_.isBurning() && lvt_5_1_ > 0 && p_196262_4_.func_226278_cu_() <= (double)lvt_6_1_) {
         p_196262_4_.extinguish();
         this.setWaterLevel(p_196262_2_, p_196262_3_, p_196262_1_, lvt_5_1_ - 1);
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
      if (lvt_7_1_.isEmpty()) {
         return ActionResultType.PASS;
      } else {
         int lvt_8_1_ = (Integer)p_225533_1_.get(LEVEL);
         Item lvt_9_1_ = lvt_7_1_.getItem();
         if (lvt_9_1_ == Items.WATER_BUCKET) {
            if (lvt_8_1_ < 3 && !p_225533_2_.isRemote) {
               if (!p_225533_4_.abilities.isCreativeMode) {
                  p_225533_4_.setHeldItem(p_225533_5_, new ItemStack(Items.BUCKET));
               }

               p_225533_4_.addStat(Stats.FILL_CAULDRON);
               this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, 3);
               p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return ActionResultType.SUCCESS;
         } else if (lvt_9_1_ == Items.BUCKET) {
            if (lvt_8_1_ == 3 && !p_225533_2_.isRemote) {
               if (!p_225533_4_.abilities.isCreativeMode) {
                  lvt_7_1_.shrink(1);
                  if (lvt_7_1_.isEmpty()) {
                     p_225533_4_.setHeldItem(p_225533_5_, new ItemStack(Items.WATER_BUCKET));
                  } else if (!p_225533_4_.inventory.addItemStackToInventory(new ItemStack(Items.WATER_BUCKET))) {
                     p_225533_4_.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                  }
               }

               p_225533_4_.addStat(Stats.USE_CAULDRON);
               this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, 0);
               p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return ActionResultType.SUCCESS;
         } else {
            ItemStack lvt_10_4_;
            if (lvt_9_1_ == Items.GLASS_BOTTLE) {
               if (lvt_8_1_ > 0 && !p_225533_2_.isRemote) {
                  if (!p_225533_4_.abilities.isCreativeMode) {
                     lvt_10_4_ = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
                     p_225533_4_.addStat(Stats.USE_CAULDRON);
                     lvt_7_1_.shrink(1);
                     if (lvt_7_1_.isEmpty()) {
                        p_225533_4_.setHeldItem(p_225533_5_, lvt_10_4_);
                     } else if (!p_225533_4_.inventory.addItemStackToInventory(lvt_10_4_)) {
                        p_225533_4_.dropItem(lvt_10_4_, false);
                     } else if (p_225533_4_ instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)p_225533_4_).sendContainerToPlayer(p_225533_4_.container);
                     }
                  }

                  p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, lvt_8_1_ - 1);
               }

               return ActionResultType.SUCCESS;
            } else if (lvt_9_1_ == Items.POTION && PotionUtils.getPotionFromItem(lvt_7_1_) == Potions.WATER) {
               if (lvt_8_1_ < 3 && !p_225533_2_.isRemote) {
                  if (!p_225533_4_.abilities.isCreativeMode) {
                     lvt_10_4_ = new ItemStack(Items.GLASS_BOTTLE);
                     p_225533_4_.addStat(Stats.USE_CAULDRON);
                     p_225533_4_.setHeldItem(p_225533_5_, lvt_10_4_);
                     if (p_225533_4_ instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)p_225533_4_).sendContainerToPlayer(p_225533_4_.container);
                     }
                  }

                  p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, lvt_8_1_ + 1);
               }

               return ActionResultType.SUCCESS;
            } else {
               if (lvt_8_1_ > 0 && lvt_9_1_ instanceof IDyeableArmorItem) {
                  IDyeableArmorItem lvt_10_3_ = (IDyeableArmorItem)lvt_9_1_;
                  if (lvt_10_3_.hasColor(lvt_7_1_) && !p_225533_2_.isRemote) {
                     lvt_10_3_.removeColor(lvt_7_1_);
                     this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, lvt_8_1_ - 1);
                     p_225533_4_.addStat(Stats.CLEAN_ARMOR);
                     return ActionResultType.SUCCESS;
                  }
               }

               if (lvt_8_1_ > 0 && lvt_9_1_ instanceof BannerItem) {
                  if (BannerTileEntity.getPatterns(lvt_7_1_) > 0 && !p_225533_2_.isRemote) {
                     lvt_10_4_ = lvt_7_1_.copy();
                     lvt_10_4_.setCount(1);
                     BannerTileEntity.removeBannerData(lvt_10_4_);
                     p_225533_4_.addStat(Stats.CLEAN_BANNER);
                     if (!p_225533_4_.abilities.isCreativeMode) {
                        lvt_7_1_.shrink(1);
                        this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, lvt_8_1_ - 1);
                     }

                     if (lvt_7_1_.isEmpty()) {
                        p_225533_4_.setHeldItem(p_225533_5_, lvt_10_4_);
                     } else if (!p_225533_4_.inventory.addItemStackToInventory(lvt_10_4_)) {
                        p_225533_4_.dropItem(lvt_10_4_, false);
                     } else if (p_225533_4_ instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)p_225533_4_).sendContainerToPlayer(p_225533_4_.container);
                     }
                  }

                  return ActionResultType.SUCCESS;
               } else if (lvt_8_1_ > 0 && lvt_9_1_ instanceof BlockItem) {
                  Block lvt_10_5_ = ((BlockItem)lvt_9_1_).getBlock();
                  if (lvt_10_5_ instanceof ShulkerBoxBlock && !p_225533_2_.isRemote()) {
                     ItemStack lvt_11_1_ = new ItemStack(Blocks.SHULKER_BOX, 1);
                     if (lvt_7_1_.hasTag()) {
                        lvt_11_1_.setTag(lvt_7_1_.getTag().copy());
                     }

                     p_225533_4_.setHeldItem(p_225533_5_, lvt_11_1_);
                     this.setWaterLevel(p_225533_2_, p_225533_3_, p_225533_1_, lvt_8_1_ - 1);
                     p_225533_4_.addStat(Stats.CLEAN_SHULKER_BOX);
                     return ActionResultType.SUCCESS;
                  } else {
                     return ActionResultType.CONSUME;
                  }
               } else {
                  return ActionResultType.PASS;
               }
            }
         }
      }
   }

   public void setWaterLevel(World p_176590_1_, BlockPos p_176590_2_, BlockState p_176590_3_, int p_176590_4_) {
      p_176590_1_.setBlockState(p_176590_2_, (BlockState)p_176590_3_.with(LEVEL, MathHelper.clamp(p_176590_4_, 0, 3)), 2);
      p_176590_1_.updateComparatorOutputLevel(p_176590_2_, this);
   }

   public void fillWithRain(World p_176224_1_, BlockPos p_176224_2_) {
      if (p_176224_1_.rand.nextInt(20) == 1) {
         float lvt_3_1_ = p_176224_1_.func_226691_t_(p_176224_2_).func_225486_c(p_176224_2_);
         if (lvt_3_1_ >= 0.15F) {
            BlockState lvt_4_1_ = p_176224_1_.getBlockState(p_176224_2_);
            if ((Integer)lvt_4_1_.get(LEVEL) < 3) {
               p_176224_1_.setBlockState(p_176224_2_, (BlockState)lvt_4_1_.cycle(LEVEL), 2);
            }

         }
      }
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return (Integer)p_180641_1_.get(LEVEL);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_0_3;
      INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
      SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), IBooleanFunction.ONLY_FIRST);
   }
}
