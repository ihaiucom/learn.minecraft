package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH;
   public static final EnumProperty<RedstoneSide> EAST;
   public static final EnumProperty<RedstoneSide> SOUTH;
   public static final EnumProperty<RedstoneSide> WEST;
   public static final IntegerProperty POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP;
   protected static final VoxelShape[] SHAPES;
   private boolean canProvidePower = true;
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public RedstoneWireBlock(Block.Properties p_i48344_1_) {
      super(p_i48344_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, RedstoneSide.NONE)).with(EAST, RedstoneSide.NONE)).with(SOUTH, RedstoneSide.NONE)).with(WEST, RedstoneSide.NONE)).with(POWER, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[getAABBIndex(p_220053_1_)];
   }

   private static int getAABBIndex(BlockState p_185699_0_) {
      int i = 0;
      boolean flag = p_185699_0_.get(NORTH) != RedstoneSide.NONE;
      boolean flag1 = p_185699_0_.get(EAST) != RedstoneSide.NONE;
      boolean flag2 = p_185699_0_.get(SOUTH) != RedstoneSide.NONE;
      boolean flag3 = p_185699_0_.get(WEST) != RedstoneSide.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << Direction.NORTH.getHorizontalIndex();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << Direction.EAST.getHorizontalIndex();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << Direction.SOUTH.getHorizontalIndex();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << Direction.WEST.getHorizontalIndex();
      }

      return i;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST))).with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST))).with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH))).with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN) {
         return p_196271_1_;
      } else {
         return p_196271_2_ == Direction.UP ? (BlockState)((BlockState)((BlockState)((BlockState)p_196271_1_.with(WEST, this.getSide(p_196271_4_, p_196271_5_, Direction.WEST))).with(EAST, this.getSide(p_196271_4_, p_196271_5_, Direction.EAST))).with(NORTH, this.getSide(p_196271_4_, p_196271_5_, Direction.NORTH))).with(SOUTH, this.getSide(p_196271_4_, p_196271_5_, Direction.SOUTH)) : (BlockState)p_196271_1_.with((IProperty)FACING_PROPERTY_MAP.get(p_196271_2_), this.getSide(p_196271_4_, p_196271_5_, p_196271_2_));
      }
   }

   public void updateDiagonalNeighbors(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var6 = null;

      try {
         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction direction = (Direction)var7.next();
            RedstoneSide redstoneside = (RedstoneSide)p_196248_1_.get((IProperty)FACING_PROPERTY_MAP.get(direction));
            if (redstoneside != RedstoneSide.NONE && p_196248_2_.getBlockState(blockpos$pooledmutable.setPos((Vec3i)p_196248_3_).move(direction)).getBlock() != this) {
               blockpos$pooledmutable.move(Direction.DOWN);
               BlockState blockstate = p_196248_2_.getBlockState(blockpos$pooledmutable);
               if (blockstate.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos = blockpos$pooledmutable.offset(direction.getOpposite());
                  BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), p_196248_2_.getBlockState(blockpos), p_196248_2_, blockpos$pooledmutable, blockpos);
                  replaceBlock(blockstate, blockstate1, p_196248_2_, blockpos$pooledmutable, p_196248_4_);
               }

               blockpos$pooledmutable.setPos((Vec3i)p_196248_3_).move(direction).move(Direction.UP);
               BlockState blockstate3 = p_196248_2_.getBlockState(blockpos$pooledmutable);
               if (blockstate3.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos1 = blockpos$pooledmutable.offset(direction.getOpposite());
                  BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), p_196248_2_.getBlockState(blockpos1), p_196248_2_, blockpos$pooledmutable, blockpos1);
                  replaceBlock(blockstate3, blockstate2, p_196248_2_, blockpos$pooledmutable, p_196248_4_);
               }
            }
         }
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var6 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

   }

   private RedstoneSide getSide(IBlockReader p_208074_1_, BlockPos p_208074_2_, Direction p_208074_3_) {
      BlockPos blockpos = p_208074_2_.offset(p_208074_3_);
      BlockState blockstate = p_208074_1_.getBlockState(blockpos);
      BlockPos blockpos1 = p_208074_2_.up();
      BlockState blockstate1 = p_208074_1_.getBlockState(blockpos1);
      if (!blockstate1.isNormalCube(p_208074_1_, blockpos1)) {
         boolean flag = blockstate.func_224755_d(p_208074_1_, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
         if (flag && canConnectTo(p_208074_1_.getBlockState(blockpos.up()), p_208074_1_, blockpos.up(), (Direction)null)) {
            if (blockstate.func_224756_o(p_208074_1_, blockpos)) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return canConnectTo(blockstate, p_208074_1_, blockpos, p_208074_3_) || !blockstate.isNormalCube(p_208074_1_, blockpos) && canConnectTo(p_208074_1_.getBlockState(blockpos.down()), p_208074_1_, blockpos.down(), (Direction)null) ? RedstoneSide.SIDE : RedstoneSide.NONE;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      return blockstate.func_224755_d(p_196260_2_, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
   }

   private BlockState updateSurroundingRedstone(World p_176338_1_, BlockPos p_176338_2_, BlockState p_176338_3_) {
      p_176338_3_ = this.func_212568_b(p_176338_1_, p_176338_2_, p_176338_3_);
      List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         BlockPos blockpos = (BlockPos)var5.next();
         p_176338_1_.notifyNeighborsOfStateChange(blockpos, this);
      }

      return p_176338_3_;
   }

   private BlockState func_212568_b(World p_212568_1_, BlockPos p_212568_2_, BlockState p_212568_3_) {
      BlockState blockstate = p_212568_3_;
      int i = (Integer)p_212568_3_.get(POWER);
      this.canProvidePower = false;
      int j = p_212568_1_.getRedstonePowerFromNeighbors(p_212568_2_);
      this.canProvidePower = true;
      int k = 0;
      if (j < 15) {
         Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

         label43:
         while(true) {
            while(true) {
               if (!var8.hasNext()) {
                  break label43;
               }

               Direction direction = (Direction)var8.next();
               BlockPos blockpos = p_212568_2_.offset(direction);
               BlockState blockstate1 = p_212568_1_.getBlockState(blockpos);
               k = this.maxSignal(k, blockstate1);
               BlockPos blockpos1 = p_212568_2_.up();
               if (blockstate1.isNormalCube(p_212568_1_, blockpos) && !p_212568_1_.getBlockState(blockpos1).isNormalCube(p_212568_1_, blockpos1)) {
                  k = this.maxSignal(k, p_212568_1_.getBlockState(blockpos.up()));
               } else if (!blockstate1.isNormalCube(p_212568_1_, blockpos)) {
                  k = this.maxSignal(k, p_212568_1_.getBlockState(blockpos.down()));
               }
            }
         }
      }

      int l = k - 1;
      if (j > l) {
         l = j;
      }

      if (i != l) {
         p_212568_3_ = (BlockState)p_212568_3_.with(POWER, l);
         if (p_212568_1_.getBlockState(p_212568_2_) == blockstate) {
            p_212568_1_.setBlockState(p_212568_2_, p_212568_3_, 2);
         }

         this.blocksNeedingUpdate.add(p_212568_2_);
         Direction[] var14 = Direction.values();
         int var15 = var14.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            Direction direction1 = var14[var16];
            this.blocksNeedingUpdate.add(p_212568_2_.offset(direction1));
         }
      }

      return p_212568_3_;
   }

   private void notifyWireNeighborsOfStateChange(World p_176344_1_, BlockPos p_176344_2_) {
      if (p_176344_1_.getBlockState(p_176344_2_).getBlock() == this) {
         p_176344_1_.notifyNeighborsOfStateChange(p_176344_2_, this);
         Direction[] var3 = Direction.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
            p_176344_1_.notifyNeighborsOfStateChange(p_176344_2_.offset(direction), this);
         }
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && !p_220082_2_.isRemote) {
         this.updateSurroundingRedstone(p_220082_2_, p_220082_3_, p_220082_1_);
         Iterator var6 = Direction.Plane.VERTICAL.iterator();

         Direction direction2;
         while(var6.hasNext()) {
            direction2 = (Direction)var6.next();
            p_220082_2_.notifyNeighborsOfStateChange(p_220082_3_.offset(direction2), this);
         }

         var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            direction2 = (Direction)var6.next();
            this.notifyWireNeighborsOfStateChange(p_220082_2_, p_220082_3_.offset(direction2));
         }

         var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            direction2 = (Direction)var6.next();
            BlockPos blockpos = p_220082_3_.offset(direction2);
            if (p_220082_2_.getBlockState(blockpos).isNormalCube(p_220082_2_, blockpos)) {
               this.notifyWireNeighborsOfStateChange(p_220082_2_, blockpos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(p_220082_2_, blockpos.down());
            }
         }
      }

   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (!p_196243_2_.isRemote) {
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Direction direction = var6[var8];
               p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(direction), this);
            }

            this.updateSurroundingRedstone(p_196243_2_, p_196243_3_, p_196243_1_);
            Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

            Direction direction2;
            while(var10.hasNext()) {
               direction2 = (Direction)var10.next();
               this.notifyWireNeighborsOfStateChange(p_196243_2_, p_196243_3_.offset(direction2));
            }

            var10 = Direction.Plane.HORIZONTAL.iterator();

            while(var10.hasNext()) {
               direction2 = (Direction)var10.next();
               BlockPos blockpos = p_196243_3_.offset(direction2);
               if (p_196243_2_.getBlockState(blockpos).isNormalCube(p_196243_2_, blockpos)) {
                  this.notifyWireNeighborsOfStateChange(p_196243_2_, blockpos.up());
               } else {
                  this.notifyWireNeighborsOfStateChange(p_196243_2_, blockpos.down());
               }
            }
         }
      }

   }

   private int maxSignal(int p_212567_1_, BlockState p_212567_2_) {
      if (p_212567_2_.getBlock() != this) {
         return p_212567_1_;
      } else {
         int i = (Integer)p_212567_2_.get(POWER);
         return i > p_212567_1_ ? i : p_212567_1_;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         if (p_220069_1_.isValidPosition(p_220069_2_, p_220069_3_)) {
            this.updateSurroundingRedstone(p_220069_2_, p_220069_3_, p_220069_1_);
         } else {
            spawnDrops(p_220069_1_, p_220069_2_, p_220069_3_);
            p_220069_2_.removeBlock(p_220069_3_, false);
         }
      }

   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return !this.canProvidePower ? 0 : p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      if (!this.canProvidePower) {
         return 0;
      } else {
         int i = (Integer)p_180656_1_.get(POWER);
         if (i == 0) {
            return 0;
         } else if (p_180656_4_ == Direction.UP) {
            return i;
         } else {
            EnumSet<Direction> enumset = EnumSet.noneOf(Direction.class);
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction direction = (Direction)var7.next();
               if (this.isPowerSourceAt(p_180656_2_, p_180656_3_, direction)) {
                  enumset.add(direction);
               }
            }

            if (p_180656_4_.getAxis().isHorizontal() && enumset.isEmpty()) {
               return i;
            } else {
               return enumset.contains(p_180656_4_) && !enumset.contains(p_180656_4_.rotateYCCW()) && !enumset.contains(p_180656_4_.rotateY()) ? i : 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(IBlockReader p_176339_1_, BlockPos p_176339_2_, Direction p_176339_3_) {
      BlockPos blockpos = p_176339_2_.offset(p_176339_3_);
      BlockState blockstate = p_176339_1_.getBlockState(blockpos);
      boolean flag = blockstate.isNormalCube(p_176339_1_, blockpos);
      BlockPos blockpos1 = p_176339_2_.up();
      boolean flag1 = p_176339_1_.getBlockState(blockpos1).isNormalCube(p_176339_1_, blockpos1);
      if (!flag1 && flag && canConnectTo(p_176339_1_.getBlockState(blockpos.up()), p_176339_1_, blockpos.up(), (Direction)null)) {
         return true;
      } else if (canConnectTo(blockstate, p_176339_1_, blockpos, p_176339_3_)) {
         return true;
      } else if (blockstate.getBlock() == Blocks.REPEATER && (Boolean)blockstate.get(RedstoneDiodeBlock.POWERED) && blockstate.get(RedstoneDiodeBlock.HORIZONTAL_FACING) == p_176339_3_) {
         return true;
      } else {
         return !flag && canConnectTo(p_176339_1_.getBlockState(blockpos.down()), p_176339_1_, blockpos.down(), (Direction)null);
      }
   }

   protected static boolean canConnectTo(BlockState p_canConnectTo_0_, IBlockReader p_canConnectTo_1_, BlockPos p_canConnectTo_2_, @Nullable Direction p_canConnectTo_3_) {
      Block block = p_canConnectTo_0_.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (p_canConnectTo_0_.getBlock() == Blocks.REPEATER) {
         Direction direction = (Direction)p_canConnectTo_0_.get(RepeaterBlock.HORIZONTAL_FACING);
         return direction == p_canConnectTo_3_ || direction.getOpposite() == p_canConnectTo_3_;
      } else if (Blocks.OBSERVER == p_canConnectTo_0_.getBlock()) {
         return p_canConnectTo_3_ == p_canConnectTo_0_.get(ObserverBlock.FACING);
      } else {
         return p_canConnectTo_0_.canConnectRedstone(p_canConnectTo_1_, p_canConnectTo_2_, p_canConnectTo_3_) && p_canConnectTo_3_ != null;
      }
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return this.canProvidePower;
   }

   @OnlyIn(Dist.CLIENT)
   public static int colorMultiplier(int p_176337_0_) {
      float f = (float)p_176337_0_ / 15.0F;
      float f1 = f * 0.6F + 0.4F;
      if (p_176337_0_ == 0) {
         f1 = 0.3F;
      }

      float f2 = f * f * 0.7F - 0.5F;
      float f3 = f * f * 0.6F - 0.7F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
      return -16777216 | i << 16 | j << 8 | k;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      int i = (Integer)p_180655_1_.get(POWER);
      if (i != 0) {
         double d0 = (double)p_180655_3_.getX() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         double d1 = (double)((float)p_180655_3_.getY() + 0.0625F);
         double d2 = (double)p_180655_3_.getZ() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + 0.4F;
         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         p_180655_2_.addParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }

   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(SOUTH))).with(EAST, p_185499_1_.get(WEST))).with(SOUTH, p_185499_1_.get(NORTH))).with(WEST, p_185499_1_.get(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(EAST))).with(EAST, p_185499_1_.get(SOUTH))).with(SOUTH, p_185499_1_.get(WEST))).with(WEST, p_185499_1_.get(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(WEST))).with(EAST, p_185499_1_.get(NORTH))).with(SOUTH, p_185499_1_.get(EAST))).with(WEST, p_185499_1_.get(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)p_185471_1_.with(NORTH, p_185471_1_.get(SOUTH))).with(SOUTH, p_185471_1_.get(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)p_185471_1_.with(EAST, p_185471_1_.get(WEST))).with(WEST, p_185471_1_.get(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   static {
      NORTH = BlockStateProperties.REDSTONE_NORTH;
      EAST = BlockStateProperties.REDSTONE_EAST;
      SOUTH = BlockStateProperties.REDSTONE_SOUTH;
      WEST = BlockStateProperties.REDSTONE_WEST;
      POWER = BlockStateProperties.POWER_0_15;
      FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
      SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   }
}
