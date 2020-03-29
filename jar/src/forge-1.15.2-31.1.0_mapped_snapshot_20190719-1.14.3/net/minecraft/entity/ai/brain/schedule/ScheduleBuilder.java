package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List<ScheduleBuilder.ActivityEntry> entries = Lists.newArrayList();

   public ScheduleBuilder(Schedule p_i50135_1_) {
      this.schedule = p_i50135_1_;
   }

   public ScheduleBuilder add(int p_221402_1_, Activity p_221402_2_) {
      this.entries.add(new ScheduleBuilder.ActivityEntry(p_221402_1_, p_221402_2_));
      return this;
   }

   public Schedule build() {
      Set var10000 = (Set)this.entries.stream().map(ScheduleBuilder.ActivityEntry::getActivity).collect(Collectors.toSet());
      Schedule var10001 = this.schedule;
      var10000.forEach(var10001::createDutiesFor);
      this.entries.forEach((p_221405_1_) -> {
         Activity lvt_2_1_ = p_221405_1_.getActivity();
         this.schedule.getAllDutiesExcept(lvt_2_1_).forEach((p_221403_1_) -> {
            p_221403_1_.func_221394_a(p_221405_1_.getDuration(), 0.0F);
         });
         this.schedule.getDutiesFor(lvt_2_1_).func_221394_a(p_221405_1_.getDuration(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityEntry {
      private final int duration;
      private final Activity activity;

      public ActivityEntry(int p_i51309_1_, Activity p_i51309_2_) {
         this.duration = p_i51309_1_;
         this.activity = p_i51309_2_;
      }

      public int getDuration() {
         return this.duration;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}
