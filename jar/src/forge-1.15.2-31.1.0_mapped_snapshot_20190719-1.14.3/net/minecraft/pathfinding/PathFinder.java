package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;

public class PathFinder {
   private final PathHeap path = new PathHeap();
   private final Set<PathPoint> closedSet = Sets.newHashSet();
   private final PathPoint[] pathOptions = new PathPoint[32];
   private final int field_215751_d;
   private final NodeProcessor nodeProcessor;

   public PathFinder(NodeProcessor p_i51280_1_, int p_i51280_2_) {
      this.nodeProcessor = p_i51280_1_;
      this.field_215751_d = p_i51280_2_;
   }

   @Nullable
   public Path func_227478_a_(Region p_227478_1_, MobEntity p_227478_2_, Set<BlockPos> p_227478_3_, float p_227478_4_, int p_227478_5_, float p_227478_6_) {
      this.path.clearPath();
      this.nodeProcessor.func_225578_a_(p_227478_1_, p_227478_2_);
      PathPoint lvt_7_1_ = this.nodeProcessor.getStart();
      Map<FlaggedPathPoint, BlockPos> lvt_8_1_ = (Map)p_227478_3_.stream().collect(Collectors.toMap((p_224782_1_) -> {
         return this.nodeProcessor.func_224768_a((double)p_224782_1_.getX(), (double)p_224782_1_.getY(), (double)p_224782_1_.getZ());
      }, Function.identity()));
      Path lvt_9_1_ = this.func_227479_a_(lvt_7_1_, lvt_8_1_, p_227478_4_, p_227478_5_, p_227478_6_);
      this.nodeProcessor.postProcess();
      return lvt_9_1_;
   }

   @Nullable
   private Path func_227479_a_(PathPoint p_227479_1_, Map<FlaggedPathPoint, BlockPos> p_227479_2_, float p_227479_3_, int p_227479_4_, float p_227479_5_) {
      Set<FlaggedPathPoint> lvt_6_1_ = p_227479_2_.keySet();
      p_227479_1_.totalPathDistance = 0.0F;
      p_227479_1_.distanceToNext = this.func_224776_a(p_227479_1_, lvt_6_1_);
      p_227479_1_.distanceToTarget = p_227479_1_.distanceToNext;
      this.path.clearPath();
      this.closedSet.clear();
      this.path.addPoint(p_227479_1_);
      int lvt_7_1_ = 0;
      int lvt_8_1_ = (int)((float)this.field_215751_d * p_227479_5_);

      while(!this.path.isPathEmpty()) {
         ++lvt_7_1_;
         if (lvt_7_1_ >= lvt_8_1_) {
            break;
         }

         PathPoint lvt_9_1_ = this.path.dequeue();
         lvt_9_1_.visited = true;
         lvt_6_1_.stream().filter((p_224781_2_) -> {
            return lvt_9_1_.func_224757_c(p_224781_2_) <= (float)p_227479_4_;
         }).forEach(FlaggedPathPoint::func_224764_e);
         if (lvt_6_1_.stream().anyMatch(FlaggedPathPoint::func_224762_f)) {
            break;
         }

         if (lvt_9_1_.distanceTo(p_227479_1_) < p_227479_3_) {
            int lvt_10_1_ = this.nodeProcessor.func_222859_a(this.pathOptions, lvt_9_1_);

            for(int lvt_11_1_ = 0; lvt_11_1_ < lvt_10_1_; ++lvt_11_1_) {
               PathPoint lvt_12_1_ = this.pathOptions[lvt_11_1_];
               float lvt_13_1_ = lvt_9_1_.distanceTo(lvt_12_1_);
               lvt_12_1_.field_222861_j = lvt_9_1_.field_222861_j + lvt_13_1_;
               float lvt_14_1_ = lvt_9_1_.totalPathDistance + lvt_13_1_ + lvt_12_1_.costMalus;
               if (lvt_12_1_.field_222861_j < p_227479_3_ && (!lvt_12_1_.isAssigned() || lvt_14_1_ < lvt_12_1_.totalPathDistance)) {
                  lvt_12_1_.previous = lvt_9_1_;
                  lvt_12_1_.totalPathDistance = lvt_14_1_;
                  lvt_12_1_.distanceToNext = this.func_224776_a(lvt_12_1_, lvt_6_1_) * 1.5F;
                  if (lvt_12_1_.isAssigned()) {
                     this.path.changeDistance(lvt_12_1_, lvt_12_1_.totalPathDistance + lvt_12_1_.distanceToNext);
                  } else {
                     lvt_12_1_.distanceToTarget = lvt_12_1_.totalPathDistance + lvt_12_1_.distanceToNext;
                     this.path.addPoint(lvt_12_1_);
                  }
               }
            }
         }
      }

      Stream lvt_9_3_;
      if (lvt_6_1_.stream().anyMatch(FlaggedPathPoint::func_224762_f)) {
         lvt_9_3_ = lvt_6_1_.stream().filter(FlaggedPathPoint::func_224762_f).map((p_224778_2_) -> {
            return this.func_224780_a(p_224778_2_.func_224763_d(), (BlockPos)p_227479_2_.get(p_224778_2_), true);
         }).sorted(Comparator.comparingInt(Path::getCurrentPathLength));
      } else {
         lvt_9_3_ = lvt_6_1_.stream().map((p_224777_2_) -> {
            return this.func_224780_a(p_224777_2_.func_224763_d(), (BlockPos)p_227479_2_.get(p_224777_2_), false);
         }).sorted(Comparator.comparingDouble(Path::func_224769_l).thenComparingInt(Path::getCurrentPathLength));
      }

      Optional<Path> lvt_10_2_ = lvt_9_3_.findFirst();
      if (!lvt_10_2_.isPresent()) {
         return null;
      } else {
         Path lvt_11_2_ = (Path)lvt_10_2_.get();
         return lvt_11_2_;
      }
   }

   private float func_224776_a(PathPoint p_224776_1_, Set<FlaggedPathPoint> p_224776_2_) {
      float lvt_3_1_ = Float.MAX_VALUE;

      float lvt_6_1_;
      for(Iterator var4 = p_224776_2_.iterator(); var4.hasNext(); lvt_3_1_ = Math.min(lvt_6_1_, lvt_3_1_)) {
         FlaggedPathPoint lvt_5_1_ = (FlaggedPathPoint)var4.next();
         lvt_6_1_ = p_224776_1_.distanceTo(lvt_5_1_);
         lvt_5_1_.func_224761_a(lvt_6_1_, p_224776_1_);
      }

      return lvt_3_1_;
   }

   private Path func_224780_a(PathPoint p_224780_1_, BlockPos p_224780_2_, boolean p_224780_3_) {
      List<PathPoint> lvt_4_1_ = Lists.newArrayList();
      PathPoint lvt_5_1_ = p_224780_1_;
      lvt_4_1_.add(0, p_224780_1_);

      while(lvt_5_1_.previous != null) {
         lvt_5_1_ = lvt_5_1_.previous;
         lvt_4_1_.add(0, lvt_5_1_);
      }

      return new Path(lvt_4_1_, p_224780_2_, p_224780_3_);
   }
}
