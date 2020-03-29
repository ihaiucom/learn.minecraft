package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator {
   @Nullable
   public static Vec3d findRandomTarget(CreatureEntity p_75463_0_, int p_75463_1_, int p_75463_2_) {
      return func_226339_a_(p_75463_0_, p_75463_1_, p_75463_2_, 0, (Vec3d)null, true, 1.5707963705062866D, p_75463_0_::getBlockPathWeight, false, 0, 0, true);
   }

   @Nullable
   public static Vec3d func_226338_a_(CreatureEntity p_226338_0_, int p_226338_1_, int p_226338_2_, int p_226338_3_, @Nullable Vec3d p_226338_4_, double p_226338_5_) {
      return func_226339_a_(p_226338_0_, p_226338_1_, p_226338_2_, p_226338_3_, p_226338_4_, true, p_226338_5_, p_226338_0_::getBlockPathWeight, true, 0, 0, false);
   }

   @Nullable
   public static Vec3d getLandPos(CreatureEntity p_191377_0_, int p_191377_1_, int p_191377_2_) {
      p_191377_0_.getClass();
      return func_221024_a(p_191377_0_, p_191377_1_, p_191377_2_, p_191377_0_::getBlockPathWeight);
   }

   @Nullable
   public static Vec3d func_221024_a(CreatureEntity p_221024_0_, int p_221024_1_, int p_221024_2_, ToDoubleFunction<BlockPos> p_221024_3_) {
      return func_226339_a_(p_221024_0_, p_221024_1_, p_221024_2_, 0, (Vec3d)null, false, 0.0D, p_221024_3_, true, 0, 0, true);
   }

   @Nullable
   public static Vec3d func_226340_a_(CreatureEntity p_226340_0_, int p_226340_1_, int p_226340_2_, Vec3d p_226340_3_, float p_226340_4_, int p_226340_5_, int p_226340_6_) {
      return func_226339_a_(p_226340_0_, p_226340_1_, p_226340_2_, 0, p_226340_3_, false, (double)p_226340_4_, p_226340_0_::getBlockPathWeight, true, p_226340_5_, p_226340_6_, true);
   }

   @Nullable
   public static Vec3d findRandomTargetBlockTowards(CreatureEntity p_75464_0_, int p_75464_1_, int p_75464_2_, Vec3d p_75464_3_) {
      Vec3d lvt_4_1_ = p_75464_3_.subtract(p_75464_0_.func_226277_ct_(), p_75464_0_.func_226278_cu_(), p_75464_0_.func_226281_cx_());
      return func_226339_a_(p_75464_0_, p_75464_1_, p_75464_2_, 0, lvt_4_1_, true, 1.5707963705062866D, p_75464_0_::getBlockPathWeight, false, 0, 0, true);
   }

   @Nullable
   public static Vec3d findRandomTargetTowardsScaled(CreatureEntity p_203155_0_, int p_203155_1_, int p_203155_2_, Vec3d p_203155_3_, double p_203155_4_) {
      Vec3d lvt_6_1_ = p_203155_3_.subtract(p_203155_0_.func_226277_ct_(), p_203155_0_.func_226278_cu_(), p_203155_0_.func_226281_cx_());
      return func_226339_a_(p_203155_0_, p_203155_1_, p_203155_2_, 0, lvt_6_1_, true, p_203155_4_, p_203155_0_::getBlockPathWeight, false, 0, 0, true);
   }

   @Nullable
   public static Vec3d func_226344_b_(CreatureEntity p_226344_0_, int p_226344_1_, int p_226344_2_, int p_226344_3_, Vec3d p_226344_4_, double p_226344_5_) {
      Vec3d lvt_7_1_ = p_226344_4_.subtract(p_226344_0_.func_226277_ct_(), p_226344_0_.func_226278_cu_(), p_226344_0_.func_226281_cx_());
      return func_226339_a_(p_226344_0_, p_226344_1_, p_226344_2_, p_226344_3_, lvt_7_1_, false, p_226344_5_, p_226344_0_::getBlockPathWeight, true, 0, 0, false);
   }

   @Nullable
   public static Vec3d findRandomTargetBlockAwayFrom(CreatureEntity p_75461_0_, int p_75461_1_, int p_75461_2_, Vec3d p_75461_3_) {
      Vec3d lvt_4_1_ = p_75461_0_.getPositionVec().subtract(p_75461_3_);
      return func_226339_a_(p_75461_0_, p_75461_1_, p_75461_2_, 0, lvt_4_1_, true, 1.5707963705062866D, p_75461_0_::getBlockPathWeight, false, 0, 0, true);
   }

   @Nullable
   public static Vec3d func_223548_b(CreatureEntity p_223548_0_, int p_223548_1_, int p_223548_2_, Vec3d p_223548_3_) {
      Vec3d lvt_4_1_ = p_223548_0_.getPositionVec().subtract(p_223548_3_);
      return func_226339_a_(p_223548_0_, p_223548_1_, p_223548_2_, 0, lvt_4_1_, false, 1.5707963705062866D, p_223548_0_::getBlockPathWeight, true, 0, 0, true);
   }

   @Nullable
   private static Vec3d func_226339_a_(CreatureEntity p_226339_0_, int p_226339_1_, int p_226339_2_, int p_226339_3_, @Nullable Vec3d p_226339_4_, boolean p_226339_5_, double p_226339_6_, ToDoubleFunction<BlockPos> p_226339_8_, boolean p_226339_9_, int p_226339_10_, int p_226339_11_, boolean p_226339_12_) {
      PathNavigator lvt_13_1_ = p_226339_0_.getNavigator();
      Random lvt_14_1_ = p_226339_0_.getRNG();
      boolean lvt_15_2_;
      if (p_226339_0_.detachHome()) {
         lvt_15_2_ = p_226339_0_.getHomePosition().withinDistance(p_226339_0_.getPositionVec(), (double)(p_226339_0_.getMaximumHomeDistance() + (float)p_226339_1_) + 1.0D);
      } else {
         lvt_15_2_ = false;
      }

      boolean lvt_16_1_ = false;
      double lvt_17_1_ = Double.NEGATIVE_INFINITY;
      BlockPos lvt_19_1_ = new BlockPos(p_226339_0_);

      for(int lvt_20_1_ = 0; lvt_20_1_ < 10; ++lvt_20_1_) {
         BlockPos lvt_21_1_ = func_226343_a_(lvt_14_1_, p_226339_1_, p_226339_2_, p_226339_3_, p_226339_4_, p_226339_6_);
         if (lvt_21_1_ != null) {
            int lvt_22_1_ = lvt_21_1_.getX();
            int lvt_23_1_ = lvt_21_1_.getY();
            int lvt_24_1_ = lvt_21_1_.getZ();
            BlockPos lvt_25_2_;
            if (p_226339_0_.detachHome() && p_226339_1_ > 1) {
               lvt_25_2_ = p_226339_0_.getHomePosition();
               if (p_226339_0_.func_226277_ct_() > (double)lvt_25_2_.getX()) {
                  lvt_22_1_ -= lvt_14_1_.nextInt(p_226339_1_ / 2);
               } else {
                  lvt_22_1_ += lvt_14_1_.nextInt(p_226339_1_ / 2);
               }

               if (p_226339_0_.func_226281_cx_() > (double)lvt_25_2_.getZ()) {
                  lvt_24_1_ -= lvt_14_1_.nextInt(p_226339_1_ / 2);
               } else {
                  lvt_24_1_ += lvt_14_1_.nextInt(p_226339_1_ / 2);
               }
            }

            lvt_25_2_ = new BlockPos((double)lvt_22_1_ + p_226339_0_.func_226277_ct_(), (double)lvt_23_1_ + p_226339_0_.func_226278_cu_(), (double)lvt_24_1_ + p_226339_0_.func_226281_cx_());
            if (lvt_25_2_.getY() >= 0 && lvt_25_2_.getY() <= p_226339_0_.world.getHeight() && (!lvt_15_2_ || p_226339_0_.isWithinHomeDistanceFromPosition(lvt_25_2_)) && (!p_226339_12_ || lvt_13_1_.canEntityStandOnPos(lvt_25_2_))) {
               if (p_226339_9_) {
                  lvt_25_2_ = func_226342_a_(lvt_25_2_, lvt_14_1_.nextInt(p_226339_10_ + 1) + p_226339_11_, p_226339_0_.world.getHeight(), (p_226341_1_) -> {
                     return p_226339_0_.world.getBlockState(p_226341_1_).getMaterial().isSolid();
                  });
               }

               if (p_226339_5_ || !p_226339_0_.world.getFluidState(lvt_25_2_).isTagged(FluidTags.WATER)) {
                  PathNodeType lvt_26_1_ = WalkNodeProcessor.func_227480_b_(p_226339_0_.world, lvt_25_2_.getX(), lvt_25_2_.getY(), lvt_25_2_.getZ());
                  if (p_226339_0_.getPathPriority(lvt_26_1_) == 0.0F) {
                     double lvt_27_1_ = p_226339_8_.applyAsDouble(lvt_25_2_);
                     if (lvt_27_1_ > lvt_17_1_) {
                        lvt_17_1_ = lvt_27_1_;
                        lvt_19_1_ = lvt_25_2_;
                        lvt_16_1_ = true;
                     }
                  }
               }
            }
         }
      }

      if (lvt_16_1_) {
         return new Vec3d(lvt_19_1_);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos func_226343_a_(Random p_226343_0_, int p_226343_1_, int p_226343_2_, int p_226343_3_, @Nullable Vec3d p_226343_4_, double p_226343_5_) {
      if (p_226343_4_ != null && p_226343_5_ < 3.141592653589793D) {
         double lvt_7_2_ = MathHelper.atan2(p_226343_4_.z, p_226343_4_.x) - 1.5707963705062866D;
         double lvt_9_2_ = lvt_7_2_ + (double)(2.0F * p_226343_0_.nextFloat() - 1.0F) * p_226343_5_;
         double lvt_11_1_ = Math.sqrt(p_226343_0_.nextDouble()) * (double)MathHelper.SQRT_2 * (double)p_226343_1_;
         double lvt_13_1_ = -lvt_11_1_ * Math.sin(lvt_9_2_);
         double lvt_15_1_ = lvt_11_1_ * Math.cos(lvt_9_2_);
         if (Math.abs(lvt_13_1_) <= (double)p_226343_1_ && Math.abs(lvt_15_1_) <= (double)p_226343_1_) {
            int lvt_17_1_ = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
            return new BlockPos(lvt_13_1_, (double)lvt_17_1_, lvt_15_1_);
         } else {
            return null;
         }
      } else {
         int lvt_7_1_ = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         int lvt_8_1_ = p_226343_0_.nextInt(2 * p_226343_2_ + 1) - p_226343_2_ + p_226343_3_;
         int lvt_9_1_ = p_226343_0_.nextInt(2 * p_226343_1_ + 1) - p_226343_1_;
         return new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_);
      }
   }

   static BlockPos func_226342_a_(BlockPos p_226342_0_, int p_226342_1_, int p_226342_2_, Predicate<BlockPos> p_226342_3_) {
      if (p_226342_1_ < 0) {
         throw new IllegalArgumentException("aboveSolidAmount was " + p_226342_1_ + ", expected >= 0");
      } else if (!p_226342_3_.test(p_226342_0_)) {
         return p_226342_0_;
      } else {
         BlockPos lvt_4_1_;
         for(lvt_4_1_ = p_226342_0_.up(); lvt_4_1_.getY() < p_226342_2_ && p_226342_3_.test(lvt_4_1_); lvt_4_1_ = lvt_4_1_.up()) {
         }

         BlockPos lvt_5_1_;
         BlockPos lvt_6_1_;
         for(lvt_5_1_ = lvt_4_1_; lvt_5_1_.getY() < p_226342_2_ && lvt_5_1_.getY() - lvt_4_1_.getY() < p_226342_1_; lvt_5_1_ = lvt_6_1_) {
            lvt_6_1_ = lvt_5_1_.up();
            if (p_226342_3_.test(lvt_6_1_)) {
               break;
            }
         }

         return lvt_5_1_;
      }
   }
}
