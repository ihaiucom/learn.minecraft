package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.MobEntity;

public class LookRandomlyGoal extends Goal {
   private final MobEntity field_75258_a;
   private double lookX;
   private double lookZ;
   private int idleTime;

   public LookRandomlyGoal(MobEntity p_i1647_1_) {
      this.field_75258_a = p_i1647_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean shouldExecute() {
      return this.field_75258_a.getRNG().nextFloat() < 0.02F;
   }

   public boolean shouldContinueExecuting() {
      return this.idleTime >= 0;
   }

   public void startExecuting() {
      double lvt_1_1_ = 6.283185307179586D * this.field_75258_a.getRNG().nextDouble();
      this.lookX = Math.cos(lvt_1_1_);
      this.lookZ = Math.sin(lvt_1_1_);
      this.idleTime = 20 + this.field_75258_a.getRNG().nextInt(20);
   }

   public void tick() {
      --this.idleTime;
      this.field_75258_a.getLookController().func_220679_a(this.field_75258_a.func_226277_ct_() + this.lookX, this.field_75258_a.func_226280_cw_(), this.field_75258_a.func_226281_cx_() + this.lookZ);
   }
}
