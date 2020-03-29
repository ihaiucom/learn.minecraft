package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireBlock extends Block {
   public static final IntegerProperty AGE;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP;
   private final Object2IntMap<Block> encouragements = new Object2IntOpenHashMap();
   private final Object2IntMap<Block> flammabilities = new Object2IntOpenHashMap();

   protected FireBlock(Block.Properties p_i48397_1_) {
      super(p_i48397_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return this.isValidPosition(p_196271_1_, p_196271_4_, p_196271_5_) ? (BlockState)this.getStateForPlacement(p_196271_4_, p_196271_5_).with(AGE, p_196271_1_.get(AGE)) : Blocks.AIR.getDefaultState();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getStateForPlacement(p_196258_1_.getWorld(), p_196258_1_.getPos());
   }

   public BlockState getStateForPlacement(IBlockReader p_196448_1_, BlockPos p_196448_2_) {
      BlockPos blockpos = p_196448_2_.down();
      BlockState blockstate = p_196448_1_.getBlockState(blockpos);
      if (!this.canCatchFire(p_196448_1_, p_196448_2_, Direction.UP) && !Block.hasSolidSide(blockstate, p_196448_1_, blockpos, Direction.UP)) {
         BlockState blockstate1 = this.getDefaultState();
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            BooleanProperty booleanproperty = (BooleanProperty)FACING_TO_PROPERTY_MAP.get(direction);
            if (booleanproperty != null) {
               blockstate1 = (BlockState)blockstate1.with(booleanproperty, this.canCatchFire(p_196448_1_, p_196448_2_.offset(direction), direction.getOpposite()));
            }
         }

         return blockstate1;
      } else {
         return this.getDefaultState();
      }
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      return p_196260_2_.getBlockState(blockpos).func_224755_d(p_196260_2_, blockpos, Direction.UP) || this.areNeighborsFlammable(p_196260_2_, p_196260_3_);
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 30;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
         if (!p_225534_2_.isAreaLoaded(p_225534_3_, 2)) {
            return;
         }

         if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
            p_225534_2_.removeBlock(p_225534_3_, false);
         }

         Block block = p_225534_2_.getBlockState(p_225534_3_.down()).getBlock();
         BlockState other = p_225534_2_.getBlockState(p_225534_3_.down());
         boolean flag = other.isFireSource(p_225534_2_, p_225534_3_.down(), Direction.UP);
         int i = (Integer)p_225534_1_.get(AGE);
         if (!flag && p_225534_2_.isRaining() && this.canDie(p_225534_2_, p_225534_3_) && p_225534_4_.nextFloat() < 0.2F + (float)i * 0.03F) {
            p_225534_2_.removeBlock(p_225534_3_, false);
         } else {
            int j = Math.min(15, i + p_225534_4_.nextInt(3) / 2);
            if (i != j) {
               p_225534_1_ = (BlockState)p_225534_1_.with(AGE, j);
               p_225534_2_.setBlockState(p_225534_3_, p_225534_1_, 4);
            }

            if (!flag) {
               p_225534_2_.getPendingBlockTicks().scheduleTick(p_225534_3_, this, this.tickRate(p_225534_2_) + p_225534_4_.nextInt(10));
               if (!this.areNeighborsFlammable(p_225534_2_, p_225534_3_)) {
                  BlockPos blockpos = p_225534_3_.down();
                  if (!p_225534_2_.getBlockState(blockpos).func_224755_d(p_225534_2_, blockpos, Direction.UP) || i > 3) {
                     p_225534_2_.removeBlock(p_225534_3_, false);
                  }

                  return;
               }

               if (i == 15 && p_225534_4_.nextInt(4) == 0 && !this.canCatchFire(p_225534_2_, p_225534_3_.down(), Direction.UP)) {
                  p_225534_2_.removeBlock(p_225534_3_, false);
                  return;
               }
            }

            boolean flag1 = p_225534_2_.isBlockinHighHumidity(p_225534_3_);
            int k = flag1 ? -50 : 0;
            this.tryCatchFire(p_225534_2_, p_225534_3_.east(), 300 + k, p_225534_4_, i, Direction.WEST);
            this.tryCatchFire(p_225534_2_, p_225534_3_.west(), 300 + k, p_225534_4_, i, Direction.EAST);
            this.tryCatchFire(p_225534_2_, p_225534_3_.down(), 250 + k, p_225534_4_, i, Direction.UP);
            this.tryCatchFire(p_225534_2_, p_225534_3_.up(), 250 + k, p_225534_4_, i, Direction.DOWN);
            this.tryCatchFire(p_225534_2_, p_225534_3_.north(), 300 + k, p_225534_4_, i, Direction.SOUTH);
            this.tryCatchFire(p_225534_2_, p_225534_3_.south(), 300 + k, p_225534_4_, i, Direction.NORTH);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutable.setPos((Vec3i)p_225534_3_).move(l, j1, i1);
                        int l1 = this.getNeighborEncouragement(p_225534_2_, blockpos$mutable);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + p_225534_2_.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && p_225534_4_.nextInt(k1) <= i2 && (!p_225534_2_.isRaining() || !this.canDie(p_225534_2_, blockpos$mutable))) {
                              int j2 = Math.min(15, i + p_225534_4_.nextInt(5) / 4);
                              p_225534_2_.setBlockState(blockpos$mutable, (BlockState)this.getStateForPlacement(p_225534_2_, blockpos$mutable).with(AGE, j2), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   protected boolean canDie(World p_176537_1_, BlockPos p_176537_2_) {
      return p_176537_1_.isRainingAt(p_176537_2_) || p_176537_1_.isRainingAt(p_176537_2_.west()) || p_176537_1_.isRainingAt(p_176537_2_.east()) || p_176537_1_.isRainingAt(p_176537_2_.north()) || p_176537_1_.isRainingAt(p_176537_2_.south());
   }

   /** @deprecated */
   @Deprecated
   public int func_220274_q(BlockState p_220274_1_) {
      return p_220274_1_.has(BlockStateProperties.WATERLOGGED) && (Boolean)p_220274_1_.get(BlockStateProperties.WATERLOGGED) ? 0 : this.flammabilities.getInt(p_220274_1_.getBlock());
   }

   /** @deprecated */
   @Deprecated
   public int func_220275_r(BlockState p_220275_1_) {
      return p_220275_1_.has(BlockStateProperties.WATERLOGGED) && (Boolean)p_220275_1_.get(BlockStateProperties.WATERLOGGED) ? 0 : this.encouragements.getInt(p_220275_1_.getBlock());
   }

   private void tryCatchFire(World p_tryCatchFire_1_, BlockPos p_tryCatchFire_2_, int p_tryCatchFire_3_, Random p_tryCatchFire_4_, int p_tryCatchFire_5_, Direction p_tryCatchFire_6_) {
      int i = p_tryCatchFire_1_.getBlockState(p_tryCatchFire_2_).getFlammability(p_tryCatchFire_1_, p_tryCatchFire_2_, p_tryCatchFire_6_);
      if (p_tryCatchFire_4_.nextInt(p_tryCatchFire_3_) < i) {
         BlockState blockstate = p_tryCatchFire_1_.getBlockState(p_tryCatchFire_2_);
         if (p_tryCatchFire_4_.nextInt(p_tryCatchFire_5_ + 10) < 5 && !p_tryCatchFire_1_.isRainingAt(p_tryCatchFire_2_)) {
            int j = Math.min(p_tryCatchFire_5_ + p_tryCatchFire_4_.nextInt(5) / 4, 15);
            p_tryCatchFire_1_.setBlockState(p_tryCatchFire_2_, (BlockState)this.getStateForPlacement(p_tryCatchFire_1_, p_tryCatchFire_2_).with(AGE, j), 3);
         } else {
            p_tryCatchFire_1_.removeBlock(p_tryCatchFire_2_, false);
         }

         blockstate.catchFire(p_tryCatchFire_1_, p_tryCatchFire_2_, p_tryCatchFire_6_, (LivingEntity)null);
      }

   }

   private boolean areNeighborsFlammable(IBlockReader p_196447_1_, BlockPos p_196447_2_) {
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction direction = var3[var5];
         if (this.canCatchFire(p_196447_1_, p_196447_2_.offset(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   private int getNeighborEncouragement(IWorldReader p_176538_1_, BlockPos p_176538_2_) {
      if (!p_176538_1_.isAirBlock(p_176538_2_)) {
         return 0;
      } else {
         int i = 0;
         Direction[] var4 = Direction.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Direction direction = var4[var6];
            BlockState blockstate = p_176538_1_.getBlockState(p_176538_2_.offset(direction));
            i = Math.max(blockstate.getFlammability(p_176538_1_, p_176538_2_.offset(direction), direction.getOpposite()), i);
         }

         return i;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean canBurn(BlockState p_196446_1_) {
      return this.func_220275_r(p_196446_1_) > 0;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && (p_220082_2_.dimension.getType() != DimensionType.OVERWORLD && p_220082_2_.dimension.getType() != DimensionType.THE_NETHER || !((NetherPortalBlock)Blocks.NETHER_PORTAL).trySpawnPortal(p_220082_2_, p_220082_3_))) {
         if (!p_220082_1_.isValidPosition(p_220082_2_, p_220082_3_)) {
            p_220082_2_.removeBlock(p_220082_3_, false);
         } else {
            p_220082_2_.getPendingBlockTicks().scheduleTick(p_220082_3_, this, this.tickRate(p_220082_2_) + p_220082_2_.rand.nextInt(10));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(24) == 0) {
         p_180655_2_.playSound((double)((float)p_180655_3_.getX() + 0.5F), (double)((float)p_180655_3_.getY() + 0.5F), (double)((float)p_180655_3_.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + p_180655_4_.nextFloat(), p_180655_4_.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockpos = p_180655_3_.down();
      BlockState blockstate = p_180655_2_.getBlockState(blockpos);
      int j1;
      double d7;
      double d12;
      double d17;
      if (!this.canCatchFire(p_180655_2_, blockpos, Direction.UP) && !Block.hasSolidSide(blockstate, p_180655_2_, blockpos, Direction.UP)) {
         if (this.canCatchFire(p_180655_2_, blockpos.west(), Direction.EAST)) {
            for(j1 = 0; j1 < 2; ++j1) {
               d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble() * 0.10000000149011612D;
               d12 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(p_180655_2_, p_180655_3_.east(), Direction.WEST)) {
            for(j1 = 0; j1 < 2; ++j1) {
               d7 = (double)(p_180655_3_.getX() + 1) - p_180655_4_.nextDouble() * 0.10000000149011612D;
               d12 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(p_180655_2_, p_180655_3_.north(), Direction.SOUTH)) {
            for(j1 = 0; j1 < 2; ++j1) {
               d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               d12 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble() * 0.10000000149011612D;
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(p_180655_2_, p_180655_3_.south(), Direction.NORTH)) {
            for(j1 = 0; j1 < 2; ++j1) {
               d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               d12 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble();
               d17 = (double)(p_180655_3_.getZ() + 1) - p_180655_4_.nextDouble() * 0.10000000149011612D;
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canCatchFire(p_180655_2_, p_180655_3_.up(), Direction.DOWN)) {
            for(j1 = 0; j1 < 2; ++j1) {
               d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
               d12 = (double)(p_180655_3_.getY() + 1) - p_180655_4_.nextDouble() * 0.10000000149011612D;
               d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
               p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(j1 = 0; j1 < 3; ++j1) {
            d7 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
            d12 = (double)p_180655_3_.getY() + p_180655_4_.nextDouble() * 0.5D + 0.5D;
            d17 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
            p_180655_2_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   public void setFireInfo(Block p_180686_1_, int p_180686_2_, int p_180686_3_) {
      if (p_180686_1_ == Blocks.AIR) {
         throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
      } else {
         this.encouragements.put(p_180686_1_, p_180686_2_);
         this.flammabilities.put(p_180686_1_, p_180686_3_);
      }
   }

   public boolean canCatchFire(IBlockReader p_canCatchFire_1_, BlockPos p_canCatchFire_2_, Direction p_canCatchFire_3_) {
      return p_canCatchFire_1_.getBlockState(p_canCatchFire_2_).isFlammable(p_canCatchFire_1_, p_canCatchFire_2_, p_canCatchFire_3_);
   }

   public static void init() {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      fireblock.setFireInfo(Blocks.OAK_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_PLANKS, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_SLAB, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_FENCE, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.BIRCH_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.ACACIA_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 20);
      fireblock.setFireInfo(Blocks.OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.SPRUCE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.BIRCH_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.JUNGLE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.ACACIA_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.DARK_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.SPRUCE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.BIRCH_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.JUNGLE_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.ACACIA_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.DARK_OAK_WOOD, 5, 5);
      fireblock.setFireInfo(Blocks.OAK_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.SPRUCE_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.BIRCH_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.JUNGLE_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.ACACIA_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.DARK_OAK_LEAVES, 30, 60);
      fireblock.setFireInfo(Blocks.BOOKSHELF, 30, 20);
      fireblock.setFireInfo(Blocks.TNT, 15, 100);
      fireblock.setFireInfo(Blocks.GRASS, 60, 100);
      fireblock.setFireInfo(Blocks.FERN, 60, 100);
      fireblock.setFireInfo(Blocks.DEAD_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.SUNFLOWER, 60, 100);
      fireblock.setFireInfo(Blocks.LILAC, 60, 100);
      fireblock.setFireInfo(Blocks.ROSE_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.PEONY, 60, 100);
      fireblock.setFireInfo(Blocks.TALL_GRASS, 60, 100);
      fireblock.setFireInfo(Blocks.LARGE_FERN, 60, 100);
      fireblock.setFireInfo(Blocks.DANDELION, 60, 100);
      fireblock.setFireInfo(Blocks.POPPY, 60, 100);
      fireblock.setFireInfo(Blocks.BLUE_ORCHID, 60, 100);
      fireblock.setFireInfo(Blocks.ALLIUM, 60, 100);
      fireblock.setFireInfo(Blocks.AZURE_BLUET, 60, 100);
      fireblock.setFireInfo(Blocks.RED_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.ORANGE_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.WHITE_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.PINK_TULIP, 60, 100);
      fireblock.setFireInfo(Blocks.OXEYE_DAISY, 60, 100);
      fireblock.setFireInfo(Blocks.CORNFLOWER, 60, 100);
      fireblock.setFireInfo(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      fireblock.setFireInfo(Blocks.WITHER_ROSE, 60, 100);
      fireblock.setFireInfo(Blocks.WHITE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.ORANGE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.MAGENTA_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.YELLOW_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIME_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.PINK_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.GRAY_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.CYAN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.PURPLE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BLUE_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BROWN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.GREEN_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.RED_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.BLACK_WOOL, 30, 60);
      fireblock.setFireInfo(Blocks.VINE, 15, 100);
      fireblock.setFireInfo(Blocks.COAL_BLOCK, 5, 5);
      fireblock.setFireInfo(Blocks.HAY_BLOCK, 60, 20);
      fireblock.setFireInfo(Blocks.WHITE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.ORANGE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.MAGENTA_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.YELLOW_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIME_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.PINK_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.GRAY_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.CYAN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.PURPLE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BLUE_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BROWN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.GREEN_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.RED_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.BLACK_CARPET, 60, 20);
      fireblock.setFireInfo(Blocks.DRIED_KELP_BLOCK, 30, 60);
      fireblock.setFireInfo(Blocks.BAMBOO, 60, 60);
      fireblock.setFireInfo(Blocks.SCAFFOLDING, 60, 60);
      fireblock.setFireInfo(Blocks.LECTERN, 30, 20);
      fireblock.setFireInfo(Blocks.COMPOSTER, 5, 20);
      fireblock.setFireInfo(Blocks.SWEET_BERRY_BUSH, 60, 100);
      fireblock.setFireInfo(Blocks.field_226906_mb_, 5, 20);
      fireblock.setFireInfo(Blocks.field_226905_ma_, 30, 20);
   }

   static {
      AGE = BlockStateProperties.AGE_0_15;
      NORTH = SixWayBlock.NORTH;
      EAST = SixWayBlock.EAST;
      SOUTH = SixWayBlock.SOUTH;
      WEST = SixWayBlock.WEST;
      UP = SixWayBlock.UP;
      FACING_TO_PROPERTY_MAP = (Map)SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_.getKey() != Direction.DOWN;
      }).collect(Util.toMapCollector());
   }
}
