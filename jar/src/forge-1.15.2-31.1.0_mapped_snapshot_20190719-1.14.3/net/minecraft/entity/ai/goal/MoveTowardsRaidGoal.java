package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsRaidGoal<T extends AbstractRaiderEntity> extends Goal {
   private final T field_220744_a;

   public MoveTowardsRaidGoal(T p_i50323_1_) {
      this.field_220744_a = p_i50323_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      return this.field_220744_a.getAttackTarget() == null && !this.field_220744_a.isBeingRidden() && this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().func_221319_a() && !((ServerWorld)this.field_220744_a.world).func_217483_b_(new BlockPos(this.field_220744_a));
   }

   public boolean shouldContinueExecuting() {
      return this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().func_221319_a() && this.field_220744_a.world instanceof ServerWorld && !((ServerWorld)this.field_220744_a.world).func_217483_b_(new BlockPos(this.field_220744_a));
   }

   public void tick() {
      if (this.field_220744_a.isRaidActive()) {
         Raid lvt_1_1_ = this.field_220744_a.getRaid();
         if (this.field_220744_a.ticksExisted % 20 == 0) {
            this.func_220743_a(lvt_1_1_);
         }

         if (!this.field_220744_a.hasPath()) {
            Vec3d lvt_2_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_220744_a, 15, 4, new Vec3d(lvt_1_1_.func_221304_t()));
            if (lvt_2_1_ != null) {
               this.field_220744_a.getNavigator().tryMoveToXYZ(lvt_2_1_.x, lvt_2_1_.y, lvt_2_1_.z, 1.0D);
            }
         }
      }

   }

   private void func_220743_a(Raid p_220743_1_) {
      if (p_220743_1_.isActive()) {
         Set<AbstractRaiderEntity> lvt_2_1_ = Sets.newHashSet();
         List<AbstractRaiderEntity> lvt_3_1_ = this.field_220744_a.world.getEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220744_a.getBoundingBox().grow(16.0D), (p_220742_1_) -> {
            return !p_220742_1_.isRaidActive() && RaidManager.func_215165_a(p_220742_1_, p_220743_1_);
         });
         lvt_2_1_.addAll(lvt_3_1_);
         Iterator var4 = lvt_2_1_.iterator();

         while(var4.hasNext()) {
            AbstractRaiderEntity lvt_5_1_ = (AbstractRaiderEntity)var4.next();
            p_220743_1_.func_221317_a(p_220743_1_.func_221315_l(), lvt_5_1_, (BlockPos)null, true);
         }
      }

   }
}
