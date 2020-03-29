package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class WalkNodeProcessor extends NodeProcessor {
   protected float avoidsWater;

   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      super.func_225578_a_(p_225578_1_, p_225578_2_);
      this.avoidsWater = p_225578_2_.getPathPriority(PathNodeType.WATER);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int i;
      BlockPos blockpos2;
      if (this.getCanSwim() && this.entity.isInWater()) {
         i = MathHelper.floor(this.entity.func_226278_cu_());
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.entity.func_226277_ct_(), (double)i, this.entity.func_226281_cx_());

         for(BlockState blockstate = this.blockaccess.getBlockState(blockpos$mutable); blockstate.getBlock() == Blocks.WATER || blockstate.getFluidState() == Fluids.WATER.getStillFluidState(false); blockstate = this.blockaccess.getBlockState(blockpos$mutable)) {
            ++i;
            blockpos$mutable.setPos(this.entity.func_226277_ct_(), (double)i, this.entity.func_226281_cx_());
         }

         --i;
      } else if (this.entity.onGround) {
         i = MathHelper.floor(this.entity.func_226278_cu_() + 0.5D);
      } else {
         for(blockpos2 = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos2).isAir() || this.blockaccess.getBlockState(blockpos2).allowsMovement(this.blockaccess, blockpos2, PathType.LAND)) && blockpos2.getY() > 0; blockpos2 = blockpos2.down()) {
         }

         i = blockpos2.up().getY();
      }

      blockpos2 = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos2.getX(), i, blockpos2.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));
         Iterator var5 = set.iterator();

         while(var5.hasNext()) {
            BlockPos blockpos1 = (BlockPos)var5.next();
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos1);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return this.openPoint(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
         }
      }

      return this.openPoint(blockpos2.getX(), i, blockpos2.getZ());
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(this.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_), MathHelper.floor(p_224768_5_)));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      int j = 0;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
         PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, p_222859_2_.x, p_222859_2_.y, p_222859_2_.z);
         if (pathnodetype1 == PathNodeType.STICKY_HONEY) {
            j = 0;
         } else {
            j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
         }
      }

      double d0 = getGroundY(this.blockaccess, new BlockPos(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z));
      PathPoint pathpoint = this.getSafePoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH);
      if (pathpoint != null && !pathpoint.visited && pathpoint.costMalus >= 0.0F) {
         p_222859_1_[i++] = pathpoint;
      }

      PathPoint pathpoint1 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z, j, d0, Direction.WEST);
      if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.costMalus >= 0.0F) {
         p_222859_1_[i++] = pathpoint1;
      }

      PathPoint pathpoint2 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z, j, d0, Direction.EAST);
      if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.costMalus >= 0.0F) {
         p_222859_1_[i++] = pathpoint2;
      }

      PathPoint pathpoint3 = this.getSafePoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH);
      if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.costMalus >= 0.0F) {
         p_222859_1_[i++] = pathpoint3;
      }

      PathPoint pathpoint4 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH);
      if (this.func_222860_a(p_222859_2_, pathpoint1, pathpoint3, pathpoint4)) {
         p_222859_1_[i++] = pathpoint4;
      }

      PathPoint pathpoint5 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH);
      if (this.func_222860_a(p_222859_2_, pathpoint2, pathpoint3, pathpoint5)) {
         p_222859_1_[i++] = pathpoint5;
      }

      PathPoint pathpoint6 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH);
      if (this.func_222860_a(p_222859_2_, pathpoint1, pathpoint, pathpoint6)) {
         p_222859_1_[i++] = pathpoint6;
      }

      PathPoint pathpoint7 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH);
      if (this.func_222860_a(p_222859_2_, pathpoint2, pathpoint, pathpoint7)) {
         p_222859_1_[i++] = pathpoint7;
      }

      return i;
   }

   private boolean func_222860_a(PathPoint p_222860_1_, @Nullable PathPoint p_222860_2_, @Nullable PathPoint p_222860_3_, @Nullable PathPoint p_222860_4_) {
      if (p_222860_4_ != null && p_222860_3_ != null && p_222860_2_ != null) {
         if (p_222860_4_.visited) {
            return false;
         } else if (p_222860_3_.y <= p_222860_1_.y && p_222860_2_.y <= p_222860_1_.y) {
            return p_222860_4_.costMalus >= 0.0F && (p_222860_3_.y < p_222860_1_.y || p_222860_3_.costMalus >= 0.0F) && (p_222860_2_.y < p_222860_1_.y || p_222860_2_.costMalus >= 0.0F);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static double getGroundY(IBlockReader p_197682_0_, BlockPos p_197682_1_) {
      BlockPos blockpos = p_197682_1_.down();
      VoxelShape voxelshape = p_197682_0_.getBlockState(blockpos).getCollisionShape(p_197682_0_, blockpos);
      return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(Direction.Axis.Y));
   }

   @Nullable
   private PathPoint getSafePoint(int p_186332_1_, int p_186332_2_, int p_186332_3_, int p_186332_4_, double p_186332_5_, Direction p_186332_7_) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(p_186332_1_, p_186332_2_, p_186332_3_);
      double d0 = getGroundY(this.blockaccess, blockpos);
      if (d0 - p_186332_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
         float f = this.entity.getPathPriority(pathnodetype);
         double d1 = (double)this.entity.getWidth() / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype == PathNodeType.WALKABLE) {
            return pathpoint;
         } else {
            if ((pathpoint == null || pathpoint.costMalus < 0.0F) && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.getSafePoint(p_186332_1_, p_186332_2_ + 1, p_186332_3_, p_186332_4_ - 1, p_186332_5_, p_186332_7_);
               if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F) {
                  double d2 = (double)(p_186332_1_ - p_186332_7_.getXOffset()) + 0.5D;
                  double d3 = (double)(p_186332_3_ - p_186332_7_.getZOffset()) + 0.5D;
                  AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, getGroundY(this.blockaccess, new BlockPos(d2, (double)(p_186332_2_ + 1), d3)) + 0.001D, d3 - d1, d2 + d1, (double)this.entity.getHeight() + getGroundY(this.blockaccess, new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z)) - 0.002D, d3 + d1);
                  if (!this.blockaccess.func_226665_a__(this.entity, axisalignedbb)) {
                     pathpoint = null;
                  }
               }
            }

            if (pathnodetype == PathNodeType.WATER && !this.getCanSwim()) {
               if (this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_ - 1, p_186332_3_) != PathNodeType.WATER) {
                  return pathpoint;
               }

               while(p_186332_2_ > 0) {
                  --p_186332_2_;
                  pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
                  if (pathnodetype != PathNodeType.WATER) {
                     return pathpoint;
                  }

                  pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
                  pathpoint.nodeType = pathnodetype;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, this.entity.getPathPriority(pathnodetype));
               }
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb1 = new AxisAlignedBB((double)p_186332_1_ - d1 + 0.5D, (double)p_186332_2_ + 0.001D, (double)p_186332_3_ - d1 + 0.5D, (double)p_186332_1_ + d1 + 0.5D, (double)((float)p_186332_2_ + this.entity.getHeight()), (double)p_186332_3_ + d1 + 0.5D);
               if (!this.blockaccess.func_226665_a__(this.entity, axisalignedbb1)) {
                  return null;
               }

               if (this.entity.getWidth() >= 1.0F) {
                  PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_ - 1, p_186332_3_);
                  if (pathnodetype1 == PathNodeType.BLOCKED) {
                     pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
                     pathpoint.nodeType = PathNodeType.WALKABLE;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     return pathpoint;
                  }
               }

               int i = 0;
               int j = p_186332_2_;

               while(pathnodetype == PathNodeType.OPEN) {
                  --p_186332_2_;
                  PathPoint pathpoint1;
                  if (p_186332_2_ < 0) {
                     pathpoint1 = this.openPoint(p_186332_1_, j, p_186332_3_);
                     pathpoint1.nodeType = PathNodeType.BLOCKED;
                     pathpoint1.costMalus = -1.0F;
                     return pathpoint1;
                  }

                  pathpoint1 = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
                  if (i++ >= this.entity.getMaxFallHeight()) {
                     pathpoint1.nodeType = PathNodeType.BLOCKED;
                     pathpoint1.costMalus = -1.0F;
                     return pathpoint1;
                  }

                  pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
                  f = this.entity.getPathPriority(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = pathpoint1;
                     pathpoint1.nodeType = pathnodetype;
                     pathpoint1.costMalus = Math.max(pathpoint1.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     pathpoint1.nodeType = PathNodeType.BLOCKED;
                     pathpoint1.costMalus = -1.0F;
                     return pathpoint1;
                  }
               }
            }

            return pathpoint;
         }
      }
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, MobEntity p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      double d0 = (double)p_186319_5_.getWidth() / 2.0D;
      BlockPos blockpos = new BlockPos(p_186319_5_);
      pathnodetype = this.getPathNodeType(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_, p_186319_6_, p_186319_7_, p_186319_8_, p_186319_9_, p_186319_10_, enumset, pathnodetype, blockpos);
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;
         Iterator var17 = enumset.iterator();

         while(var17.hasNext()) {
            PathNodeType pathnodetype2 = (PathNodeType)var17.next();
            if (p_186319_5_.getPathPriority(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (p_186319_5_.getPathPriority(pathnodetype2) >= p_186319_5_.getPathPriority(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         return pathnodetype == PathNodeType.OPEN && p_186319_5_.getPathPriority(pathnodetype1) == 0.0F ? PathNodeType.OPEN : pathnodetype1;
      }
   }

   public PathNodeType getPathNodeType(IBlockReader p_193577_1_, int p_193577_2_, int p_193577_3_, int p_193577_4_, int p_193577_5_, int p_193577_6_, int p_193577_7_, boolean p_193577_8_, boolean p_193577_9_, EnumSet<PathNodeType> p_193577_10_, PathNodeType p_193577_11_, BlockPos p_193577_12_) {
      for(int i = 0; i < p_193577_5_; ++i) {
         for(int j = 0; j < p_193577_6_; ++j) {
            for(int k = 0; k < p_193577_7_; ++k) {
               int l = i + p_193577_2_;
               int i1 = j + p_193577_3_;
               int j1 = k + p_193577_4_;
               PathNodeType pathnodetype = this.getPathNodeType(p_193577_1_, l, i1, j1);
               pathnodetype = this.func_215744_a(p_193577_1_, p_193577_8_, p_193577_9_, p_193577_12_, pathnodetype);
               if (i == 0 && j == 0 && k == 0) {
                  p_193577_11_ = pathnodetype;
               }

               p_193577_10_.add(pathnodetype);
            }
         }
      }

      return p_193577_11_;
   }

   protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
      if (p_215744_5_ == PathNodeType.DOOR_WOOD_CLOSED && p_215744_2_ && p_215744_3_) {
         p_215744_5_ = PathNodeType.WALKABLE;
      }

      if (p_215744_5_ == PathNodeType.DOOR_OPEN && !p_215744_3_) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      if (p_215744_5_ == PathNodeType.RAIL && !(p_215744_1_.getBlockState(p_215744_4_).getBlock() instanceof AbstractRailBlock) && !(p_215744_1_.getBlockState(p_215744_4_.down()).getBlock() instanceof AbstractRailBlock)) {
         p_215744_5_ = PathNodeType.FENCE;
      }

      if (p_215744_5_ == PathNodeType.LEAVES) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      return p_215744_5_;
   }

   private PathNodeType getPathNodeType(MobEntity p_186329_1_, BlockPos p_186329_2_) {
      return this.getPathNodeType(p_186329_1_, p_186329_2_.getX(), p_186329_2_.getY(), p_186329_2_.getZ());
   }

   private PathNodeType getPathNodeType(MobEntity p_186331_1_, int p_186331_2_, int p_186331_3_, int p_186331_4_) {
      return this.getPathNodeType(this.blockaccess, p_186331_2_, p_186331_3_, p_186331_4_, p_186331_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      return func_227480_b_(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
   }

   public static PathNodeType func_227480_b_(IBlockReader p_227480_0_, int p_227480_1_, int p_227480_2_, int p_227480_3_) {
      PathNodeType pathnodetype = getPathNodeTypeRaw(p_227480_0_, p_227480_1_, p_227480_2_, p_227480_3_);
      if (pathnodetype == PathNodeType.OPEN && p_227480_2_ >= 1) {
         Block block = p_227480_0_.getBlockState(new BlockPos(p_227480_1_, p_227480_2_ - 1, p_227480_3_)).getBlock();
         PathNodeType pathnodetype1 = getPathNodeTypeRaw(p_227480_0_, p_227480_1_, p_227480_2_ - 1, p_227480_3_);
         pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
         if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }

         if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
            pathnodetype = PathNodeType.DAMAGE_CACTUS;
         }

         if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
            pathnodetype = PathNodeType.DAMAGE_OTHER;
         }

         if (pathnodetype1 == PathNodeType.STICKY_HONEY) {
            pathnodetype = PathNodeType.STICKY_HONEY;
         }
      }

      if (pathnodetype == PathNodeType.WALKABLE) {
         pathnodetype = checkNeighborBlocks(p_227480_0_, p_227480_1_, p_227480_2_, p_227480_3_, pathnodetype);
      }

      return pathnodetype;
   }

   public static PathNodeType checkNeighborBlocks(IBlockReader p_193578_0_, int p_193578_1_, int p_193578_2_, int p_193578_3_, PathNodeType p_193578_4_) {
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var6 = null;

      try {
         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               for(int k = -1; k <= 1; ++k) {
                  if (i != 0 || k != 0) {
                     PathNodeType type = getPathNodeTypeRaw(p_193578_0_, p_193578_1_, p_193578_2_, p_193578_3_);
                     if (type == PathNodeType.DANGER_CACTUS || type == PathNodeType.DANGER_FIRE || type == PathNodeType.DANGER_OTHER) {
                        p_193578_4_ = type;
                     }
                  }
               }
            }
         }
      } catch (Throwable var18) {
         var6 = var18;
         throw var18;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var6 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var17) {
                  var6.addSuppressed(var17);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return p_193578_4_;
   }

   protected static PathNodeType getPathNodeTypeRaw(IBlockReader p_189553_0_, int p_189553_1_, int p_189553_2_, int p_189553_3_) {
      BlockPos blockpos = new BlockPos(p_189553_1_, p_189553_2_, p_189553_3_);
      BlockState blockstate = p_189553_0_.getBlockState(blockpos);
      PathNodeType type = blockstate.getAiPathNodeType(p_189553_0_, blockpos);
      if (type != null) {
         return type;
      } else {
         Block block = blockstate.getBlock();
         Material material = blockstate.getMaterial();
         if (blockstate.isAir(p_189553_0_, blockpos)) {
            return PathNodeType.OPEN;
         } else if (!block.isIn(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
               return PathNodeType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
               return PathNodeType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
               return PathNodeType.DAMAGE_OTHER;
            } else if (block == Blocks.field_226907_mc_) {
               return PathNodeType.STICKY_HONEY;
            } else if (block == Blocks.COCOA) {
               return PathNodeType.COCOA;
            } else if (block instanceof DoorBlock && material == Material.WOOD && !(Boolean)blockstate.get(DoorBlock.OPEN)) {
               return PathNodeType.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.IRON && !(Boolean)blockstate.get(DoorBlock.OPEN)) {
               return PathNodeType.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && (Boolean)blockstate.get(DoorBlock.OPEN)) {
               return PathNodeType.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
               return PathNodeType.RAIL;
            } else if (block instanceof LeavesBlock) {
               return PathNodeType.LEAVES;
            } else if (block.isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock && !(Boolean)blockstate.get(FenceGateBlock.OPEN)) {
               return PathNodeType.FENCE;
            } else {
               IFluidState ifluidstate = p_189553_0_.getFluidState(blockpos);
               if (ifluidstate.isTagged(FluidTags.WATER)) {
                  return PathNodeType.WATER;
               } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
                  return PathNodeType.LAVA;
               } else {
                  return blockstate.allowsMovement(p_189553_0_, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
               }
            }
         } else {
            return PathNodeType.TRAPDOOR;
         }
      }
   }
}
