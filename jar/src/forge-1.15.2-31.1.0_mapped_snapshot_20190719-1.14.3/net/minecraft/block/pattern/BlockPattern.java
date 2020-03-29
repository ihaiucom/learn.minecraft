package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;

public class BlockPattern {
   private final Predicate<CachedBlockInfo>[][][] blockMatches;
   private final int fingerLength;
   private final int thumbLength;
   private final int palmLength;

   public BlockPattern(Predicate<CachedBlockInfo>[][][] p_i48279_1_) {
      this.blockMatches = p_i48279_1_;
      this.fingerLength = p_i48279_1_.length;
      if (this.fingerLength > 0) {
         this.thumbLength = p_i48279_1_[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = p_i48279_1_[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }

   }

   public int getFingerLength() {
      return this.fingerLength;
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   public int getPalmLength() {
      return this.palmLength;
   }

   @Nullable
   private BlockPattern.PatternHelper checkPatternAt(BlockPos p_177682_1_, Direction p_177682_2_, Direction p_177682_3_, LoadingCache<BlockPos, CachedBlockInfo> p_177682_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < this.palmLength; ++lvt_5_1_) {
         for(int lvt_6_1_ = 0; lvt_6_1_ < this.thumbLength; ++lvt_6_1_) {
            for(int lvt_7_1_ = 0; lvt_7_1_ < this.fingerLength; ++lvt_7_1_) {
               if (!this.blockMatches[lvt_7_1_][lvt_6_1_][lvt_5_1_].test(p_177682_4_.getUnchecked(translateOffset(p_177682_1_, p_177682_2_, p_177682_3_, lvt_5_1_, lvt_6_1_, lvt_7_1_)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(p_177682_1_, p_177682_2_, p_177682_3_, p_177682_4_, this.palmLength, this.thumbLength, this.fingerLength);
   }

   @Nullable
   public BlockPattern.PatternHelper match(IWorldReader p_177681_1_, BlockPos p_177681_2_) {
      LoadingCache<BlockPos, CachedBlockInfo> lvt_3_1_ = createLoadingCache(p_177681_1_, false);
      int lvt_4_1_ = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);
      Iterator var5 = BlockPos.getAllInBoxMutable(p_177681_2_, p_177681_2_.add(lvt_4_1_ - 1, lvt_4_1_ - 1, lvt_4_1_ - 1)).iterator();

      while(var5.hasNext()) {
         BlockPos lvt_6_1_ = (BlockPos)var5.next();
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction lvt_10_1_ = var7[var9];
            Direction[] var11 = Direction.values();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               Direction lvt_14_1_ = var11[var13];
               if (lvt_14_1_ != lvt_10_1_ && lvt_14_1_ != lvt_10_1_.getOpposite()) {
                  BlockPattern.PatternHelper lvt_15_1_ = this.checkPatternAt(lvt_6_1_, lvt_10_1_, lvt_14_1_, lvt_3_1_);
                  if (lvt_15_1_ != null) {
                     return lvt_15_1_;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, CachedBlockInfo> createLoadingCache(IWorldReader p_181627_0_, boolean p_181627_1_) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(p_181627_0_, p_181627_1_));
   }

   protected static BlockPos translateOffset(BlockPos p_177683_0_, Direction p_177683_1_, Direction p_177683_2_, int p_177683_3_, int p_177683_4_, int p_177683_5_) {
      if (p_177683_1_ != p_177683_2_ && p_177683_1_ != p_177683_2_.getOpposite()) {
         Vec3i lvt_6_1_ = new Vec3i(p_177683_1_.getXOffset(), p_177683_1_.getYOffset(), p_177683_1_.getZOffset());
         Vec3i lvt_7_1_ = new Vec3i(p_177683_2_.getXOffset(), p_177683_2_.getYOffset(), p_177683_2_.getZOffset());
         Vec3i lvt_8_1_ = lvt_6_1_.crossProduct(lvt_7_1_);
         return p_177683_0_.add(lvt_7_1_.getX() * -p_177683_4_ + lvt_8_1_.getX() * p_177683_3_ + lvt_6_1_.getX() * p_177683_5_, lvt_7_1_.getY() * -p_177683_4_ + lvt_8_1_.getY() * p_177683_3_ + lvt_6_1_.getY() * p_177683_5_, lvt_7_1_.getZ() * -p_177683_4_ + lvt_8_1_.getZ() * p_177683_3_ + lvt_6_1_.getZ() * p_177683_5_);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   public static class PortalInfo {
      public final Vec3d field_222505_a;
      public final Vec3d field_222506_b;
      public final int field_222507_c;

      public PortalInfo(Vec3d p_i50457_1_, Vec3d p_i50457_2_, int p_i50457_3_) {
         this.field_222505_a = p_i50457_1_;
         this.field_222506_b = p_i50457_2_;
         this.field_222507_c = p_i50457_3_;
      }
   }

   public static class PatternHelper {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, CachedBlockInfo> lcache;
      private final int width;
      private final int height;
      private final int depth;

      public PatternHelper(BlockPos p_i46378_1_, Direction p_i46378_2_, Direction p_i46378_3_, LoadingCache<BlockPos, CachedBlockInfo> p_i46378_4_, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
         this.frontTopLeft = p_i46378_1_;
         this.forwards = p_i46378_2_;
         this.up = p_i46378_3_;
         this.lcache = p_i46378_4_;
         this.width = p_i46378_5_;
         this.height = p_i46378_6_;
         this.depth = p_i46378_7_;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public CachedBlockInfo translateOffset(int p_177670_1_, int p_177670_2_, int p_177670_3_) {
         return (CachedBlockInfo)this.lcache.getUnchecked(BlockPattern.translateOffset(this.frontTopLeft, this.getForwards(), this.getUp(), p_177670_1_, p_177670_2_, p_177670_3_));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }

      public BlockPattern.PortalInfo func_222504_a(Direction p_222504_1_, BlockPos p_222504_2_, double p_222504_3_, Vec3d p_222504_5_, double p_222504_6_) {
         Direction lvt_8_1_ = this.getForwards();
         Direction lvt_9_1_ = lvt_8_1_.rotateY();
         double lvt_12_1_ = (double)(this.getFrontTopLeft().getY() + 1) - p_222504_3_ * (double)this.getHeight();
         double lvt_10_4_;
         double lvt_14_4_;
         if (lvt_9_1_ == Direction.NORTH) {
            lvt_10_4_ = (double)p_222504_2_.getX() + 0.5D;
            lvt_14_4_ = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (lvt_9_1_ == Direction.SOUTH) {
            lvt_10_4_ = (double)p_222504_2_.getX() + 0.5D;
            lvt_14_4_ = (double)this.getFrontTopLeft().getZ() + (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (lvt_9_1_ == Direction.WEST) {
            lvt_10_4_ = (double)(this.getFrontTopLeft().getX() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
            lvt_14_4_ = (double)p_222504_2_.getZ() + 0.5D;
         } else {
            lvt_10_4_ = (double)this.getFrontTopLeft().getX() + (1.0D - p_222504_6_) * (double)this.getWidth();
            lvt_14_4_ = (double)p_222504_2_.getZ() + 0.5D;
         }

         double lvt_16_3_;
         double lvt_18_4_;
         if (lvt_8_1_.getOpposite() == p_222504_1_) {
            lvt_16_3_ = p_222504_5_.x;
            lvt_18_4_ = p_222504_5_.z;
         } else if (lvt_8_1_.getOpposite() == p_222504_1_.getOpposite()) {
            lvt_16_3_ = -p_222504_5_.x;
            lvt_18_4_ = -p_222504_5_.z;
         } else if (lvt_8_1_.getOpposite() == p_222504_1_.rotateY()) {
            lvt_16_3_ = -p_222504_5_.z;
            lvt_18_4_ = p_222504_5_.x;
         } else {
            lvt_16_3_ = p_222504_5_.z;
            lvt_18_4_ = -p_222504_5_.x;
         }

         int lvt_20_1_ = (lvt_8_1_.getHorizontalIndex() - p_222504_1_.getOpposite().getHorizontalIndex()) * 90;
         return new BlockPattern.PortalInfo(new Vec3d(lvt_10_4_, lvt_12_1_, lvt_14_4_), new Vec3d(lvt_16_3_, p_222504_5_.y, lvt_18_4_), lvt_20_1_);
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, CachedBlockInfo> {
      private final IWorldReader world;
      private final boolean forceLoad;

      public CacheLoader(IWorldReader p_i48983_1_, boolean p_i48983_2_) {
         this.world = p_i48983_1_;
         this.forceLoad = p_i48983_2_;
      }

      public CachedBlockInfo load(BlockPos p_load_1_) throws Exception {
         return new CachedBlockInfo(this.world, p_load_1_, this.forceLoad);
      }

      // $FF: synthetic method
      public Object load(Object p_load_1_) throws Exception {
         return this.load((BlockPos)p_load_1_);
      }
   }
}
