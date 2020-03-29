package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

public class HurtBySensor extends Sensor<LivingEntity> {
   protected void update(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      Brain<?> lvt_3_1_ = p_212872_2_.getBrain();
      if (p_212872_2_.getLastDamageSource() != null) {
         lvt_3_1_.setMemory(MemoryModuleType.HURT_BY, (Object)p_212872_2_.getLastDamageSource());
         Entity lvt_4_1_ = ((DamageSource)lvt_3_1_.getMemory(MemoryModuleType.HURT_BY).get()).getTrueSource();
         if (lvt_4_1_ instanceof LivingEntity) {
            lvt_3_1_.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object)((LivingEntity)lvt_4_1_));
         }
      } else {
         lvt_3_1_.removeMemory(MemoryModuleType.HURT_BY);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }
}
