package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class ExpirePOITask extends Task<LivingEntity> {
   private final MemoryModuleType<GlobalPos> field_220591_a;
   private final Predicate<PointOfInterestType> field_220592_b;

   public ExpirePOITask(PointOfInterestType p_i50338_1_, MemoryModuleType<GlobalPos> p_i50338_2_) {
      super(ImmutableMap.of(p_i50338_2_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220592_b = p_i50338_1_.func_221045_c();
      this.field_220591_a = p_i50338_2_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      GlobalPos lvt_3_1_ = (GlobalPos)p_212832_2_.getBrain().getMemory(this.field_220591_a).get();
      return Objects.equals(p_212832_1_.getDimension().getType(), lvt_3_1_.getDimension()) && lvt_3_1_.getPos().withinDistance(p_212832_2_.getPositionVec(), 5.0D);
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      GlobalPos lvt_6_1_ = (GlobalPos)lvt_5_1_.getMemory(this.field_220591_a).get();
      BlockPos lvt_7_1_ = lvt_6_1_.getPos();
      ServerWorld lvt_8_1_ = p_212831_1_.getServer().getWorld(lvt_6_1_.getDimension());
      if (this.func_223020_a(lvt_8_1_, lvt_7_1_)) {
         lvt_5_1_.removeMemory(this.field_220591_a);
      } else if (this.func_223019_a(lvt_8_1_, lvt_7_1_, p_212831_2_)) {
         lvt_5_1_.removeMemory(this.field_220591_a);
         p_212831_1_.func_217443_B().func_219142_b(lvt_7_1_);
         DebugPacketSender.func_218801_c(p_212831_1_, lvt_7_1_);
      }

   }

   private boolean func_223019_a(ServerWorld p_223019_1_, BlockPos p_223019_2_, LivingEntity p_223019_3_) {
      BlockState lvt_4_1_ = p_223019_1_.getBlockState(p_223019_2_);
      return lvt_4_1_.getBlock().isIn(BlockTags.BEDS) && (Boolean)lvt_4_1_.get(BedBlock.OCCUPIED) && !p_223019_3_.isSleeping();
   }

   private boolean func_223020_a(ServerWorld p_223020_1_, BlockPos p_223020_2_) {
      return !p_223020_1_.func_217443_B().func_219138_a(p_223020_2_, this.field_220592_b);
   }
}
