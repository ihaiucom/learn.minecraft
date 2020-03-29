package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Schedule extends ForgeRegistryEntry<Schedule> {
   public static final Schedule EMPTY;
   public static final Schedule SIMPLE;
   public static final Schedule VILLAGER_BABY;
   public static final Schedule VILLAGER_DEFAULT;
   private final Map<Activity, ScheduleDuties> field_221387_e = Maps.newHashMap();

   protected static ScheduleBuilder register(String p_221380_0_) {
      Schedule schedule = (Schedule)Registry.register((Registry)Registry.SCHEDULE, (String)p_221380_0_, (Object)(new Schedule()));
      return new ScheduleBuilder(schedule);
   }

   protected void createDutiesFor(Activity p_221379_1_) {
      if (!this.field_221387_e.containsKey(p_221379_1_)) {
         this.field_221387_e.put(p_221379_1_, new ScheduleDuties());
      }

   }

   protected ScheduleDuties getDutiesFor(Activity p_221382_1_) {
      return (ScheduleDuties)this.field_221387_e.get(p_221382_1_);
   }

   protected List<ScheduleDuties> getAllDutiesExcept(Activity p_221381_1_) {
      return (List)this.field_221387_e.entrySet().stream().filter((p_lambda$getAllDutiesExcept$0_1_) -> {
         return p_lambda$getAllDutiesExcept$0_1_.getKey() != p_221381_1_;
      }).map(Entry::getValue).collect(Collectors.toList());
   }

   public Activity getScheduledActivity(int p_221377_1_) {
      return (Activity)this.field_221387_e.entrySet().stream().max(Comparator.comparingDouble((p_lambda$getScheduledActivity$1_1_) -> {
         return (double)((ScheduleDuties)p_lambda$getScheduledActivity$1_1_.getValue()).func_221392_a(p_221377_1_);
      })).map(Entry::getKey).orElse(Activity.IDLE);
   }

   static {
      EMPTY = register("empty").add(0, Activity.IDLE).build();
      SIMPLE = register("simple").add(5000, Activity.WORK).add(11000, Activity.REST).build();
      VILLAGER_BABY = register("villager_baby").add(10, Activity.IDLE).add(3000, Activity.PLAY).add(6000, Activity.IDLE).add(10000, Activity.PLAY).add(12000, Activity.REST).build();
      VILLAGER_DEFAULT = register("villager_default").add(10, Activity.IDLE).add(2000, Activity.WORK).add(9000, Activity.MEET).add(11000, Activity.IDLE).add(12000, Activity.REST).build();
   }
}
