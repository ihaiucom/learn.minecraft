package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class WalkToHouseTask extends Task<LivingEntity> {
   private final float field_220524_a;
   private final Long2LongMap field_225455_b = new Long2LongOpenHashMap();
   private int field_225456_c;
   private long field_220525_b;

   public WalkToHouseTask(float p_i50353_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220524_a = p_i50353_1_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      if (p_212832_1_.getGameTime() - this.field_220525_b < 20L) {
         return false;
      } else {
         CreatureEntity lvt_3_1_ = (CreatureEntity)p_212832_2_;
         PointOfInterestManager lvt_4_1_ = p_212832_1_.func_217443_B();
         Optional<BlockPos> lvt_5_1_ = lvt_4_1_.func_219147_b(PointOfInterestType.HOME.func_221045_c(), (p_220522_0_) -> {
            return true;
         }, new BlockPos(p_212832_2_), 48, PointOfInterestManager.Status.ANY);
         return lvt_5_1_.isPresent() && ((BlockPos)lvt_5_1_.get()).distanceSq(new BlockPos(lvt_3_1_)) > 4.0D;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.field_225456_c = 0;
      this.field_220525_b = p_212831_1_.getGameTime() + (long)p_212831_1_.getRandom().nextInt(20);
      CreatureEntity lvt_5_1_ = (CreatureEntity)p_212831_2_;
      PointOfInterestManager lvt_6_1_ = p_212831_1_.func_217443_B();
      Predicate<BlockPos> lvt_7_1_ = (p_225453_1_) -> {
         long lvt_2_1_ = p_225453_1_.toLong();
         if (this.field_225455_b.containsKey(lvt_2_1_)) {
            return false;
         } else if (++this.field_225456_c >= 5) {
            return false;
         } else {
            this.field_225455_b.put(lvt_2_1_, this.field_220525_b + 40L);
            return true;
         }
      };
      Stream<BlockPos> lvt_8_1_ = lvt_6_1_.func_225399_a(PointOfInterestType.HOME.func_221045_c(), lvt_7_1_, new BlockPos(p_212831_2_), 48, PointOfInterestManager.Status.ANY);
      Path lvt_9_1_ = lvt_5_1_.getNavigator().func_225463_a(lvt_8_1_, PointOfInterestType.HOME.func_225478_d());
      if (lvt_9_1_ != null && lvt_9_1_.func_224771_h()) {
         BlockPos lvt_10_1_ = lvt_9_1_.func_224770_k();
         Optional<PointOfInterestType> lvt_11_1_ = lvt_6_1_.func_219148_c(lvt_10_1_);
         if (lvt_11_1_.isPresent()) {
            p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(lvt_10_1_, this.field_220524_a, 1)));
            DebugPacketSender.func_218801_c(p_212831_1_, lvt_10_1_);
         }
      } else if (this.field_225456_c < 5) {
         this.field_225455_b.long2LongEntrySet().removeIf((p_225454_1_) -> {
            return p_225454_1_.getLongValue() < this.field_220525_b;
         });
      }

   }
}
