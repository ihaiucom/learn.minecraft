package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class ScheduleDuties {
   private final List<DutyTime> field_221396_a = Lists.newArrayList();
   private int field_221397_b;

   public ScheduleDuties func_221394_a(int p_221394_1_, float p_221394_2_) {
      this.field_221396_a.add(new DutyTime(p_221394_1_, p_221394_2_));
      this.func_221395_b();
      return this;
   }

   private void func_221395_b() {
      Int2ObjectSortedMap<DutyTime> lvt_1_1_ = new Int2ObjectAVLTreeMap();
      this.field_221396_a.forEach((p_221393_1_) -> {
         DutyTime var10000 = (DutyTime)lvt_1_1_.put(p_221393_1_.func_221388_a(), p_221393_1_);
      });
      this.field_221396_a.clear();
      this.field_221396_a.addAll(lvt_1_1_.values());
      this.field_221397_b = 0;
   }

   public float func_221392_a(int p_221392_1_) {
      if (this.field_221396_a.size() <= 0) {
         return 0.0F;
      } else {
         DutyTime lvt_2_1_ = (DutyTime)this.field_221396_a.get(this.field_221397_b);
         DutyTime lvt_3_1_ = (DutyTime)this.field_221396_a.get(this.field_221396_a.size() - 1);
         boolean lvt_4_1_ = p_221392_1_ < lvt_2_1_.func_221388_a();
         int lvt_5_1_ = lvt_4_1_ ? 0 : this.field_221397_b;
         float lvt_6_1_ = lvt_4_1_ ? lvt_3_1_.func_221389_b() : lvt_2_1_.func_221389_b();

         for(int lvt_7_1_ = lvt_5_1_; lvt_7_1_ < this.field_221396_a.size(); ++lvt_7_1_) {
            DutyTime lvt_8_1_ = (DutyTime)this.field_221396_a.get(lvt_7_1_);
            if (lvt_8_1_.func_221388_a() > p_221392_1_) {
               break;
            }

            this.field_221397_b = lvt_7_1_;
            lvt_6_1_ = lvt_8_1_.func_221389_b();
         }

         return lvt_6_1_;
      }
   }
}
