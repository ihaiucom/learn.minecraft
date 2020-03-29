package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity> {
   private static final EntityPredicate field_220982_b = (new EntityPredicate()).setDistance(16.0D).allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();

   protected void update(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      List<LivingEntity> lvt_3_1_ = p_212872_1_.getEntitiesWithinAABB(LivingEntity.class, p_212872_2_.getBoundingBox().grow(16.0D, 16.0D, 16.0D), (p_220980_1_) -> {
         return p_220980_1_ != p_212872_2_ && p_220980_1_.isAlive();
      });
      p_212872_2_.getClass();
      lvt_3_1_.sort(Comparator.comparingDouble(p_212872_2_::getDistanceSq));
      Brain<?> lvt_4_1_ = p_212872_2_.getBrain();
      lvt_4_1_.setMemory(MemoryModuleType.MOBS, (Object)lvt_3_1_);
      MemoryModuleType var10001 = MemoryModuleType.VISIBLE_MOBS;
      Stream var10002 = lvt_3_1_.stream().filter((p_220981_1_) -> {
         return field_220982_b.canTarget(p_212872_2_, p_220981_1_);
      });
      p_212872_2_.getClass();
      lvt_4_1_.setMemory(var10001, var10002.filter(p_212872_2_::canEntityBeSeen).collect(Collectors.toList()));
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
   }
}
