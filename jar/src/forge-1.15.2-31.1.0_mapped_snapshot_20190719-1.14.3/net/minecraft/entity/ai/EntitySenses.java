package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

public class EntitySenses {
   private final MobEntity entity;
   private final List<Entity> seenEntities = Lists.newArrayList();
   private final List<Entity> unseenEntities = Lists.newArrayList();

   public EntitySenses(MobEntity p_i1672_1_) {
      this.entity = p_i1672_1_;
   }

   public void tick() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public boolean canSee(Entity p_75522_1_) {
      if (this.seenEntities.contains(p_75522_1_)) {
         return true;
      } else if (this.unseenEntities.contains(p_75522_1_)) {
         return false;
      } else {
         this.entity.world.getProfiler().startSection("canSee");
         boolean lvt_2_1_ = this.entity.canEntityBeSeen(p_75522_1_);
         this.entity.world.getProfiler().endSection();
         if (lvt_2_1_) {
            this.seenEntities.add(p_75522_1_);
         } else {
            this.unseenEntities.add(p_75522_1_);
         }

         return lvt_2_1_;
      }
   }
}
