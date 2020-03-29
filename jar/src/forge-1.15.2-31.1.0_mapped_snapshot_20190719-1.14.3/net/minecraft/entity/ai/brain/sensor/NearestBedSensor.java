package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class NearestBedSensor extends Sensor<MobEntity> {
   private final Long2LongMap field_225471_a = new Long2LongOpenHashMap();
   private int field_225472_b;
   private long field_225473_c;

   public NearestBedSensor() {
      super(20);
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
   }

   protected void update(ServerWorld p_212872_1_, MobEntity p_212872_2_) {
      if (p_212872_2_.isChild()) {
         this.field_225472_b = 0;
         this.field_225473_c = p_212872_1_.getGameTime() + (long)p_212872_1_.getRandom().nextInt(20);
         PointOfInterestManager lvt_3_1_ = p_212872_1_.func_217443_B();
         Predicate<BlockPos> lvt_4_1_ = (p_225469_1_) -> {
            long lvt_2_1_ = p_225469_1_.toLong();
            if (this.field_225471_a.containsKey(lvt_2_1_)) {
               return false;
            } else if (++this.field_225472_b >= 5) {
               return false;
            } else {
               this.field_225471_a.put(lvt_2_1_, this.field_225473_c + 40L);
               return true;
            }
         };
         Stream<BlockPos> lvt_5_1_ = lvt_3_1_.func_225399_a(PointOfInterestType.HOME.func_221045_c(), lvt_4_1_, new BlockPos(p_212872_2_), 48, PointOfInterestManager.Status.ANY);
         Path lvt_6_1_ = p_212872_2_.getNavigator().func_225463_a(lvt_5_1_, PointOfInterestType.HOME.func_225478_d());
         if (lvt_6_1_ != null && lvt_6_1_.func_224771_h()) {
            BlockPos lvt_7_1_ = lvt_6_1_.func_224770_k();
            Optional<PointOfInterestType> lvt_8_1_ = lvt_3_1_.func_219148_c(lvt_7_1_);
            if (lvt_8_1_.isPresent()) {
               p_212872_2_.getBrain().setMemory(MemoryModuleType.NEAREST_BED, (Object)lvt_7_1_);
            }
         } else if (this.field_225472_b < 5) {
            this.field_225471_a.long2LongEntrySet().removeIf((p_225470_1_) -> {
               return p_225470_1_.getLongValue() < this.field_225473_c;
            });
         }

      }
   }
}
