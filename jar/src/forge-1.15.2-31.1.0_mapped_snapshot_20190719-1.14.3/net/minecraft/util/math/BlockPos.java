package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Rotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements IDynamicSerializable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS;
   private static final int NUM_Y_BITS;
   private static final long X_MASK;
   private static final long Y_MASK;
   private static final long Z_MASK;
   private static final int field_218292_j;
   private static final int field_218293_k;

   public BlockPos(int p_i46030_1_, int p_i46030_2_, int p_i46030_3_) {
      super(p_i46030_1_, p_i46030_2_, p_i46030_3_);
   }

   public BlockPos(double p_i46031_1_, double p_i46031_3_, double p_i46031_5_) {
      super(p_i46031_1_, p_i46031_3_, p_i46031_5_);
   }

   public BlockPos(Entity p_i46032_1_) {
      this(p_i46032_1_.func_226277_ct_(), p_i46032_1_.func_226278_cu_(), p_i46032_1_.func_226281_cx_());
   }

   public BlockPos(Vec3d p_i47100_1_) {
      this(p_i47100_1_.x, p_i47100_1_.y, p_i47100_1_.z);
   }

   public BlockPos(IPosition p_i50799_1_) {
      this(p_i50799_1_.getX(), p_i50799_1_.getY(), p_i50799_1_.getZ());
   }

   public BlockPos(Vec3i p_i46034_1_) {
      this(p_i46034_1_.getX(), p_i46034_1_.getY(), p_i46034_1_.getZ());
   }

   public static <T> BlockPos deserialize(Dynamic<T> p_218286_0_) {
      OfInt lvt_1_1_ = p_218286_0_.asIntStream().spliterator();
      int[] lvt_2_1_ = new int[3];
      if (lvt_1_1_.tryAdvance((p_218285_1_) -> {
         lvt_2_1_[0] = p_218285_1_;
      }) && lvt_1_1_.tryAdvance((p_218280_1_) -> {
         lvt_2_1_[1] = p_218280_1_;
      })) {
         lvt_1_1_.tryAdvance((p_218284_1_) -> {
            lvt_2_1_[2] = p_218284_1_;
         });
      }

      return new BlockPos(lvt_2_1_[0], lvt_2_1_[1], lvt_2_1_[2]);
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createIntList(IntStream.of(new int[]{this.getX(), this.getY(), this.getZ()}));
   }

   public static long offset(long p_218289_0_, Direction p_218289_2_) {
      return offset(p_218289_0_, p_218289_2_.getXOffset(), p_218289_2_.getYOffset(), p_218289_2_.getZOffset());
   }

   public static long offset(long p_218291_0_, int p_218291_2_, int p_218291_3_, int p_218291_4_) {
      return pack(unpackX(p_218291_0_) + p_218291_2_, unpackY(p_218291_0_) + p_218291_3_, unpackZ(p_218291_0_) + p_218291_4_);
   }

   public static int unpackX(long p_218290_0_) {
      return (int)(p_218290_0_ << 64 - field_218293_k - NUM_X_BITS >> 64 - NUM_X_BITS);
   }

   public static int unpackY(long p_218274_0_) {
      return (int)(p_218274_0_ << 64 - NUM_Y_BITS >> 64 - NUM_Y_BITS);
   }

   public static int unpackZ(long p_218282_0_) {
      return (int)(p_218282_0_ << 64 - field_218292_j - NUM_Z_BITS >> 64 - NUM_Z_BITS);
   }

   public static BlockPos fromLong(long p_218283_0_) {
      return new BlockPos(unpackX(p_218283_0_), unpackY(p_218283_0_), unpackZ(p_218283_0_));
   }

   public static long pack(int p_218276_0_, int p_218276_1_, int p_218276_2_) {
      long lvt_3_1_ = 0L;
      lvt_3_1_ |= ((long)p_218276_0_ & X_MASK) << field_218293_k;
      lvt_3_1_ |= ((long)p_218276_1_ & Y_MASK) << 0;
      lvt_3_1_ |= ((long)p_218276_2_ & Z_MASK) << field_218292_j;
      return lvt_3_1_;
   }

   public static long func_218288_f(long p_218288_0_) {
      return p_218288_0_ & -16L;
   }

   public long toLong() {
      return pack(this.getX(), this.getY(), this.getZ());
   }

   public BlockPos add(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
      return p_177963_1_ == 0.0D && p_177963_3_ == 0.0D && p_177963_5_ == 0.0D ? this : new BlockPos((double)this.getX() + p_177963_1_, (double)this.getY() + p_177963_3_, (double)this.getZ() + p_177963_5_);
   }

   public BlockPos add(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
      return p_177982_1_ == 0 && p_177982_2_ == 0 && p_177982_3_ == 0 ? this : new BlockPos(this.getX() + p_177982_1_, this.getY() + p_177982_2_, this.getZ() + p_177982_3_);
   }

   public BlockPos add(Vec3i p_177971_1_) {
      return this.add(p_177971_1_.getX(), p_177971_1_.getY(), p_177971_1_.getZ());
   }

   public BlockPos subtract(Vec3i p_177973_1_) {
      return this.add(-p_177973_1_.getX(), -p_177973_1_.getY(), -p_177973_1_.getZ());
   }

   public BlockPos up() {
      return this.offset(Direction.UP);
   }

   public BlockPos up(int p_177981_1_) {
      return this.offset(Direction.UP, p_177981_1_);
   }

   public BlockPos down() {
      return this.offset(Direction.DOWN);
   }

   public BlockPos down(int p_177979_1_) {
      return this.offset(Direction.DOWN, p_177979_1_);
   }

   public BlockPos north() {
      return this.offset(Direction.NORTH);
   }

   public BlockPos north(int p_177964_1_) {
      return this.offset(Direction.NORTH, p_177964_1_);
   }

   public BlockPos south() {
      return this.offset(Direction.SOUTH);
   }

   public BlockPos south(int p_177970_1_) {
      return this.offset(Direction.SOUTH, p_177970_1_);
   }

   public BlockPos west() {
      return this.offset(Direction.WEST);
   }

   public BlockPos west(int p_177985_1_) {
      return this.offset(Direction.WEST, p_177985_1_);
   }

   public BlockPos east() {
      return this.offset(Direction.EAST);
   }

   public BlockPos east(int p_177965_1_) {
      return this.offset(Direction.EAST, p_177965_1_);
   }

   public BlockPos offset(Direction p_177972_1_) {
      return new BlockPos(this.getX() + p_177972_1_.getXOffset(), this.getY() + p_177972_1_.getYOffset(), this.getZ() + p_177972_1_.getZOffset());
   }

   public BlockPos offset(Direction p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new BlockPos(this.getX() + p_177967_1_.getXOffset() * p_177967_2_, this.getY() + p_177967_1_.getYOffset() * p_177967_2_, this.getZ() + p_177967_1_.getZOffset() * p_177967_2_);
   }

   public BlockPos rotate(Rotation p_190942_1_) {
      switch(p_190942_1_) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos crossProduct(Vec3i p_177955_1_) {
      return new BlockPos(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public BlockPos toImmutable() {
      return this;
   }

   public static Iterable<BlockPos> getAllInBoxMutable(BlockPos p_218278_0_, BlockPos p_218278_1_) {
      return getAllInBoxMutable(Math.min(p_218278_0_.getX(), p_218278_1_.getX()), Math.min(p_218278_0_.getY(), p_218278_1_.getY()), Math.min(p_218278_0_.getZ(), p_218278_1_.getZ()), Math.max(p_218278_0_.getX(), p_218278_1_.getX()), Math.max(p_218278_0_.getY(), p_218278_1_.getY()), Math.max(p_218278_0_.getZ(), p_218278_1_.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(BlockPos p_218281_0_, BlockPos p_218281_1_) {
      return getAllInBox(Math.min(p_218281_0_.getX(), p_218281_1_.getX()), Math.min(p_218281_0_.getY(), p_218281_1_.getY()), Math.min(p_218281_0_.getZ(), p_218281_1_.getZ()), Math.max(p_218281_0_.getX(), p_218281_1_.getX()), Math.max(p_218281_0_.getY(), p_218281_1_.getY()), Math.max(p_218281_0_.getZ(), p_218281_1_.getZ()));
   }

   public static Stream<BlockPos> func_229383_a_(MutableBoundingBox p_229383_0_) {
      return getAllInBox(Math.min(p_229383_0_.minX, p_229383_0_.maxX), Math.min(p_229383_0_.minY, p_229383_0_.maxY), Math.min(p_229383_0_.minZ, p_229383_0_.maxZ), Math.max(p_229383_0_.minX, p_229383_0_.maxX), Math.max(p_229383_0_.minY, p_229383_0_.maxY), Math.max(p_229383_0_.minZ, p_229383_0_.maxZ));
   }

   public static Stream<BlockPos> getAllInBox(final int p_218287_0_, final int p_218287_1_, final int p_218287_2_, final int p_218287_3_, final int p_218287_4_, final int p_218287_5_) {
      return StreamSupport.stream(new AbstractSpliterator<BlockPos>((long)((p_218287_3_ - p_218287_0_ + 1) * (p_218287_4_ - p_218287_1_ + 1) * (p_218287_5_ - p_218287_2_ + 1)), 64) {
         final CubeCoordinateIterator iter = new CubeCoordinateIterator(p_218287_0_, p_218287_1_, p_218287_2_, p_218287_3_, p_218287_4_, p_218287_5_);
         final BlockPos.Mutable pos = new BlockPos.Mutable();

         public boolean tryAdvance(Consumer<? super BlockPos> p_tryAdvance_1_) {
            if (this.iter.hasNext()) {
               p_tryAdvance_1_.accept(this.pos.setPos(this.iter.getX(), this.iter.getY(), this.iter.getZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   public static Iterable<BlockPos> getAllInBoxMutable(int p_191531_0_, int p_191531_1_, int p_191531_2_, int p_191531_3_, int p_191531_4_, int p_191531_5_) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final CubeCoordinateIterator field_218298_a = new CubeCoordinateIterator(p_218277_0_, p_218277_1_, p_218277_2_, p_218277_3_, p_218277_4_, p_218277_5_);
            final BlockPos.Mutable field_218299_b = new BlockPos.Mutable();

            protected BlockPos computeNext() {
               return (BlockPos)(this.field_218298_a.hasNext() ? this.field_218299_b.setPos(this.field_218298_a.getX(), this.field_218298_a.getY(), this.field_218298_a.getZ()) : (BlockPos)this.endOfData());
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   // $FF: synthetic method
   public Vec3i crossProduct(Vec3i p_177955_1_) {
      return this.crossProduct(p_177955_1_);
   }

   // $FF: synthetic method
   public Vec3i offset(Direction p_177967_1_, int p_177967_2_) {
      return this.offset(p_177967_1_, p_177967_2_);
   }

   // $FF: synthetic method
   public Vec3i down(int p_177979_1_) {
      return this.down(p_177979_1_);
   }

   // $FF: synthetic method
   public Vec3i down() {
      return this.down();
   }

   static {
      NUM_Z_BITS = NUM_X_BITS;
      NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
      X_MASK = (1L << NUM_X_BITS) - 1L;
      Y_MASK = (1L << NUM_Y_BITS) - 1L;
      Z_MASK = (1L << NUM_Z_BITS) - 1L;
      field_218292_j = NUM_Y_BITS;
      field_218293_k = NUM_Y_BITS + NUM_Z_BITS;
   }

   public static final class PooledMutable extends BlockPos.Mutable implements AutoCloseable {
      private boolean released;
      private static final List<BlockPos.PooledMutable> POOL = Lists.newArrayList();

      private PooledMutable(int p_i46586_1_, int p_i46586_2_, int p_i46586_3_) {
         super(p_i46586_1_, p_i46586_2_, p_i46586_3_);
      }

      public static BlockPos.PooledMutable retain() {
         return retain(0, 0, 0);
      }

      public static BlockPos.PooledMutable retain(Entity p_209907_0_) {
         return retain(p_209907_0_.func_226277_ct_(), p_209907_0_.func_226278_cu_(), p_209907_0_.func_226281_cx_());
      }

      public static BlockPos.PooledMutable retain(double p_185345_0_, double p_185345_2_, double p_185345_4_) {
         return retain(MathHelper.floor(p_185345_0_), MathHelper.floor(p_185345_2_), MathHelper.floor(p_185345_4_));
      }

      public static BlockPos.PooledMutable retain(int p_185339_0_, int p_185339_1_, int p_185339_2_) {
         synchronized(POOL) {
            if (!POOL.isEmpty()) {
               BlockPos.PooledMutable lvt_4_1_ = (BlockPos.PooledMutable)POOL.remove(POOL.size() - 1);
               if (lvt_4_1_ != null && lvt_4_1_.released) {
                  lvt_4_1_.released = false;
                  lvt_4_1_.setPos(p_185339_0_, p_185339_1_, p_185339_2_);
                  return lvt_4_1_;
               }
            }
         }

         return new BlockPos.PooledMutable(p_185339_0_, p_185339_1_, p_185339_2_);
      }

      public BlockPos.PooledMutable setPos(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         return (BlockPos.PooledMutable)super.setPos(p_181079_1_, p_181079_2_, p_181079_3_);
      }

      public BlockPos.PooledMutable setPos(Entity p_189535_1_) {
         return (BlockPos.PooledMutable)super.setPos(p_189535_1_);
      }

      public BlockPos.PooledMutable setPos(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return (BlockPos.PooledMutable)super.setPos(p_189532_1_, p_189532_3_, p_189532_5_);
      }

      public BlockPos.PooledMutable setPos(Vec3i p_189533_1_) {
         return (BlockPos.PooledMutable)super.setPos(p_189533_1_);
      }

      public BlockPos.PooledMutable move(Direction p_189536_1_) {
         return (BlockPos.PooledMutable)super.move(p_189536_1_);
      }

      public BlockPos.PooledMutable move(Direction p_189534_1_, int p_189534_2_) {
         return (BlockPos.PooledMutable)super.move(p_189534_1_, p_189534_2_);
      }

      public BlockPos.PooledMutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return (BlockPos.PooledMutable)super.move(p_196234_1_, p_196234_2_, p_196234_3_);
      }

      public void close() {
         synchronized(POOL) {
            if (POOL.size() < 100) {
               POOL.add(this);
            }

            this.released = true;
         }
      }

      // $FF: synthetic method
      public BlockPos.Mutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return this.move(p_196234_1_, p_196234_2_, p_196234_3_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable move(Direction p_189534_1_, int p_189534_2_) {
         return this.move(p_189534_1_, p_189534_2_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable move(Direction p_189536_1_) {
         return this.move(p_189536_1_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable setPos(Vec3i p_189533_1_) {
         return this.setPos(p_189533_1_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable setPos(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return this.setPos(p_189532_1_, p_189532_3_, p_189532_5_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable setPos(Entity p_189535_1_) {
         return this.setPos(p_189535_1_);
      }

      // $FF: synthetic method
      public BlockPos.Mutable setPos(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         return this.setPos(p_181079_1_, p_181079_2_, p_181079_3_);
      }
   }

   public static class Mutable extends BlockPos {
      protected int x;
      protected int y;
      protected int z;

      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(BlockPos p_i46587_1_) {
         this(p_i46587_1_.getX(), p_i46587_1_.getY(), p_i46587_1_.getZ());
      }

      public Mutable(int p_i46024_1_, int p_i46024_2_, int p_i46024_3_) {
         super(0, 0, 0);
         this.x = p_i46024_1_;
         this.y = p_i46024_2_;
         this.z = p_i46024_3_;
      }

      public Mutable(double p_i50824_1_, double p_i50824_3_, double p_i50824_5_) {
         this(MathHelper.floor(p_i50824_1_), MathHelper.floor(p_i50824_3_), MathHelper.floor(p_i50824_5_));
      }

      public Mutable(Entity p_i226062_1_) {
         this(p_i226062_1_.func_226277_ct_(), p_i226062_1_.func_226278_cu_(), p_i226062_1_.func_226281_cx_());
      }

      public BlockPos add(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
         return super.add(p_177963_1_, p_177963_3_, p_177963_5_).toImmutable();
      }

      public BlockPos add(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
         return super.add(p_177982_1_, p_177982_2_, p_177982_3_).toImmutable();
      }

      public BlockPos offset(Direction p_177967_1_, int p_177967_2_) {
         return super.offset(p_177967_1_, p_177967_2_).toImmutable();
      }

      public BlockPos rotate(Rotation p_190942_1_) {
         return super.rotate(p_190942_1_).toImmutable();
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public BlockPos.Mutable setPos(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         this.x = p_181079_1_;
         this.y = p_181079_2_;
         this.z = p_181079_3_;
         return this;
      }

      public BlockPos.Mutable setPos(Entity p_189535_1_) {
         return this.setPos(p_189535_1_.func_226277_ct_(), p_189535_1_.func_226278_cu_(), p_189535_1_.func_226281_cx_());
      }

      public BlockPos.Mutable setPos(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return this.setPos(MathHelper.floor(p_189532_1_), MathHelper.floor(p_189532_3_), MathHelper.floor(p_189532_5_));
      }

      public BlockPos.Mutable setPos(Vec3i p_189533_1_) {
         return this.setPos(p_189533_1_.getX(), p_189533_1_.getY(), p_189533_1_.getZ());
      }

      public BlockPos.Mutable setPos(long p_218294_1_) {
         return this.setPos(unpackX(p_218294_1_), unpackY(p_218294_1_), unpackZ(p_218294_1_));
      }

      public BlockPos.Mutable func_218295_a(AxisRotation p_218295_1_, int p_218295_2_, int p_218295_3_, int p_218295_4_) {
         return this.setPos(p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.X), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Y), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Z));
      }

      public BlockPos.Mutable move(Direction p_189536_1_) {
         return this.move(p_189536_1_, 1);
      }

      public BlockPos.Mutable move(Direction p_189534_1_, int p_189534_2_) {
         return this.setPos(this.x + p_189534_1_.getXOffset() * p_189534_2_, this.y + p_189534_1_.getYOffset() * p_189534_2_, this.z + p_189534_1_.getZOffset() * p_189534_2_);
      }

      public BlockPos.Mutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return this.setPos(this.x + p_196234_1_, this.y + p_196234_2_, this.z + p_196234_3_);
      }

      public void func_223471_o(int p_223471_1_) {
         this.x = p_223471_1_;
      }

      public void setY(int p_185336_1_) {
         this.y = p_185336_1_;
      }

      public void func_223472_q(int p_223472_1_) {
         this.z = p_223472_1_;
      }

      public BlockPos toImmutable() {
         return new BlockPos(this);
      }

      // $FF: synthetic method
      public Vec3i crossProduct(Vec3i p_177955_1_) {
         return super.crossProduct(p_177955_1_);
      }

      // $FF: synthetic method
      public Vec3i offset(Direction p_177967_1_, int p_177967_2_) {
         return this.offset(p_177967_1_, p_177967_2_);
      }

      // $FF: synthetic method
      public Vec3i down(int p_177979_1_) {
         return super.down(p_177979_1_);
      }

      // $FF: synthetic method
      public Vec3i down() {
         return super.down();
      }
   }
}
