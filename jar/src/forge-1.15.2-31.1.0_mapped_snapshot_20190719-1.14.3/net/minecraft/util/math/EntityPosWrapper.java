package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

public class EntityPosWrapper implements IPosWrapper {
   private final Entity entity;

   public EntityPosWrapper(Entity p_i51558_1_) {
      this.entity = p_i51558_1_;
   }

   public BlockPos getBlockPos() {
      return new BlockPos(this.entity);
   }

   public Vec3d getPos() {
      return new Vec3d(this.entity.func_226277_ct_(), this.entity.func_226280_cw_(), this.entity.func_226281_cx_());
   }

   public boolean isVisibleTo(LivingEntity p_220610_1_) {
      Optional<List<LivingEntity>> lvt_2_1_ = p_220610_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
      return this.entity.isAlive() && lvt_2_1_.isPresent() && ((List)lvt_2_1_.get()).contains(this.entity);
   }

   public String toString() {
      return "EntityPosWrapper for " + this.entity;
   }
}
