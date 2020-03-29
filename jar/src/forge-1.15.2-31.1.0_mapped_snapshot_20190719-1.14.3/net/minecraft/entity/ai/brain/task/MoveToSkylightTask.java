package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveToSkylightTask extends Task<LivingEntity> {
   private final float field_220494_a;

   public MoveToSkylightTask(float p_i50357_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220494_a = p_i50357_1_;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Optional<Vec3d> lvt_5_1_ = Optional.ofNullable(this.func_220493_b(p_212831_1_, p_212831_2_));
      if (lvt_5_1_.isPresent()) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, lvt_5_1_.map((p_220492_1_) -> {
            return new WalkTarget(p_220492_1_, this.field_220494_a, 0);
         }));
      }

   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return !p_212832_1_.func_226660_f_(new BlockPos(p_212832_2_));
   }

   @Nullable
   private Vec3d func_220493_b(ServerWorld p_220493_1_, LivingEntity p_220493_2_) {
      Random lvt_3_1_ = p_220493_2_.getRNG();
      BlockPos lvt_4_1_ = new BlockPos(p_220493_2_);

      for(int lvt_5_1_ = 0; lvt_5_1_ < 10; ++lvt_5_1_) {
         BlockPos lvt_6_1_ = lvt_4_1_.add(lvt_3_1_.nextInt(20) - 10, lvt_3_1_.nextInt(6) - 3, lvt_3_1_.nextInt(20) - 10);
         if (func_226306_a_(p_220493_1_, p_220493_2_, lvt_6_1_)) {
            return new Vec3d(lvt_6_1_);
         }
      }

      return null;
   }

   public static boolean func_226306_a_(ServerWorld p_226306_0_, LivingEntity p_226306_1_, BlockPos p_226306_2_) {
      return p_226306_0_.func_226660_f_(p_226306_2_) && (double)p_226306_0_.getHeight(Heightmap.Type.MOTION_BLOCKING, p_226306_2_).getY() <= p_226306_1_.func_226278_cu_();
   }
}
