package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.profiler.IProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PrioritizedGoal DUMMY = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
      public boolean shouldExecute() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, PrioritizedGoal> flagGoals = new EnumMap(Goal.Flag.class);
   private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
   private final IProfiler profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickRate = 3;

   public GoalSelector(IProfiler p_i50327_1_) {
      this.profiler = p_i50327_1_;
   }

   public void addGoal(int p_75776_1_, Goal p_75776_2_) {
      this.goals.add(new PrioritizedGoal(p_75776_1_, p_75776_2_));
   }

   public void removeGoal(Goal p_85156_1_) {
      this.goals.stream().filter((p_220882_1_) -> {
         return p_220882_1_.getGoal() == p_85156_1_;
      }).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::resetTask);
      this.goals.removeIf((p_220884_1_) -> {
         return p_220884_1_.getGoal() == p_85156_1_;
      });
   }

   public void tick() {
      this.profiler.startSection("goalCleanup");
      this.getRunningGoals().filter((p_220881_1_) -> {
         boolean var2;
         if (p_220881_1_.isRunning()) {
            Stream var10000 = p_220881_1_.getMutexFlags().stream();
            EnumSet var10001 = this.disabledFlags;
            var10001.getClass();
            if (!var10000.anyMatch(var10001::contains) && p_220881_1_.shouldContinueExecuting()) {
               var2 = false;
               return var2;
            }
         }

         var2 = true;
         return var2;
      }).forEach(Goal::resetTask);
      this.flagGoals.forEach((p_220885_1_, p_220885_2_) -> {
         if (!p_220885_2_.isRunning()) {
            this.flagGoals.remove(p_220885_1_);
         }

      });
      this.profiler.endSection();
      this.profiler.startSection("goalUpdate");
      this.goals.stream().filter((p_220883_0_) -> {
         return !p_220883_0_.isRunning();
      }).filter((p_220879_1_) -> {
         Stream var10000 = p_220879_1_.getMutexFlags().stream();
         EnumSet var10001 = this.disabledFlags;
         var10001.getClass();
         return var10000.noneMatch(var10001::contains);
      }).filter((p_220889_1_) -> {
         return p_220889_1_.getMutexFlags().stream().allMatch((p_220887_2_) -> {
            return ((PrioritizedGoal)this.flagGoals.getOrDefault(p_220887_2_, DUMMY)).isPreemptedBy(p_220889_1_);
         });
      }).filter(PrioritizedGoal::shouldExecute).forEach((p_220877_1_) -> {
         p_220877_1_.getMutexFlags().forEach((p_220876_2_) -> {
            PrioritizedGoal lvt_3_1_ = (PrioritizedGoal)this.flagGoals.getOrDefault(p_220876_2_, DUMMY);
            lvt_3_1_.resetTask();
            this.flagGoals.put(p_220876_2_, p_220877_1_);
         });
         p_220877_1_.startExecuting();
      });
      this.profiler.endSection();
      this.profiler.startSection("goalTick");
      this.getRunningGoals().forEach(PrioritizedGoal::tick);
      this.profiler.endSection();
   }

   public Stream<PrioritizedGoal> getRunningGoals() {
      return this.goals.stream().filter(PrioritizedGoal::isRunning);
   }

   public void disableFlag(Goal.Flag p_220880_1_) {
      this.disabledFlags.add(p_220880_1_);
   }

   public void enableFlag(Goal.Flag p_220886_1_) {
      this.disabledFlags.remove(p_220886_1_);
   }

   public void setFlag(Goal.Flag p_220878_1_, boolean p_220878_2_) {
      if (p_220878_2_) {
         this.enableFlag(p_220878_1_);
      } else {
         this.disableFlag(p_220878_1_);
      }

   }
}
