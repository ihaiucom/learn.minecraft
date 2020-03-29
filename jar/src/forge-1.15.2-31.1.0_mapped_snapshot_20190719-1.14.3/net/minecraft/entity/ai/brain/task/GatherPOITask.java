package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class GatherPOITask extends Task<CreatureEntity> {
   private final PointOfInterestType field_220604_a;
   private final MemoryModuleType<GlobalPos> field_220605_b;
   private final boolean field_220606_c;
   private long field_220607_d;
   private final Long2LongMap field_223013_e = new Long2LongOpenHashMap();
   private int field_223014_f;

   public GatherPOITask(PointOfInterestType p_i50374_1_, MemoryModuleType<GlobalPos> p_i50374_2_, boolean p_i50374_3_) {
      super(ImmutableMap.of(p_i50374_2_, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220604_a = p_i50374_1_;
      this.field_220605_b = p_i50374_2_;
      this.field_220606_c = p_i50374_3_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      if (this.field_220606_c && p_212832_2_.isChild()) {
         return false;
      } else {
         return p_212832_1_.getGameTime() - this.field_220607_d >= 20L;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      this.field_223014_f = 0;
      this.field_220607_d = p_212831_1_.getGameTime() + (long)p_212831_1_.getRandom().nextInt(20);
      PointOfInterestManager lvt_5_1_ = p_212831_1_.func_217443_B();
      Predicate<BlockPos> lvt_6_1_ = (p_220603_1_) -> {
         long lvt_2_1_ = p_220603_1_.toLong();
         if (this.field_223013_e.containsKey(lvt_2_1_)) {
            return false;
         } else if (++this.field_223014_f >= 5) {
            return false;
         } else {
            this.field_223013_e.put(lvt_2_1_, this.field_220607_d + 40L);
            return true;
         }
      };
      Stream<BlockPos> lvt_7_1_ = lvt_5_1_.func_225399_a(this.field_220604_a.func_221045_c(), lvt_6_1_, new BlockPos(p_212831_2_), 48, PointOfInterestManager.Status.HAS_SPACE);
      Path lvt_8_1_ = p_212831_2_.getNavigator().func_225463_a(lvt_7_1_, this.field_220604_a.func_225478_d());
      if (lvt_8_1_ != null && lvt_8_1_.func_224771_h()) {
         BlockPos lvt_9_1_ = lvt_8_1_.func_224770_k();
         lvt_5_1_.func_219148_c(lvt_9_1_).ifPresent((p_225441_5_) -> {
            lvt_5_1_.func_219157_a(this.field_220604_a.func_221045_c(), (p_225442_1_) -> {
               return p_225442_1_.equals(lvt_9_1_);
            }, lvt_9_1_, 1);
            p_212831_2_.getBrain().setMemory(this.field_220605_b, (Object)GlobalPos.of(p_212831_1_.getDimension().getType(), lvt_9_1_));
            DebugPacketSender.func_218801_c(p_212831_1_, lvt_9_1_);
         });
      } else if (this.field_223014_f < 5) {
         this.field_223013_e.long2LongEntrySet().removeIf((p_223011_1_) -> {
            return p_223011_1_.getLongValue() < this.field_220607_d;
         });
      }

   }
}
