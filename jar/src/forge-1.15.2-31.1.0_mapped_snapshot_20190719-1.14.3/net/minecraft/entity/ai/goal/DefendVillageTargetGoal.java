package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolemEntity field_75305_a;
   private LivingEntity field_75304_b;
   private final EntityPredicate field_223190_c = (new EntityPredicate()).setDistance(64.0D);

   public DefendVillageTargetGoal(IronGolemEntity p_i1659_1_) {
      super(p_i1659_1_, false, true);
      this.field_75305_a = p_i1659_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean shouldExecute() {
      AxisAlignedBB lvt_1_1_ = this.field_75305_a.getBoundingBox().grow(10.0D, 8.0D, 10.0D);
      List<LivingEntity> lvt_2_1_ = this.field_75305_a.world.getTargettableEntitiesWithinAABB(VillagerEntity.class, this.field_223190_c, this.field_75305_a, lvt_1_1_);
      List<PlayerEntity> lvt_3_1_ = this.field_75305_a.world.getTargettablePlayersWithinAABB(this.field_223190_c, this.field_75305_a, lvt_1_1_);
      Iterator var4 = lvt_2_1_.iterator();

      while(var4.hasNext()) {
         LivingEntity lvt_5_1_ = (LivingEntity)var4.next();
         VillagerEntity lvt_6_1_ = (VillagerEntity)lvt_5_1_;
         Iterator var7 = lvt_3_1_.iterator();

         while(var7.hasNext()) {
            PlayerEntity lvt_8_1_ = (PlayerEntity)var7.next();
            int lvt_9_1_ = lvt_6_1_.func_223107_f(lvt_8_1_);
            if (lvt_9_1_ <= -100) {
               this.field_75304_b = lvt_8_1_;
            }
         }
      }

      if (this.field_75304_b == null) {
         return false;
      } else if (!(this.field_75304_b instanceof PlayerEntity) || !this.field_75304_b.isSpectator() && !((PlayerEntity)this.field_75304_b).isCreative()) {
         return true;
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.field_75305_a.setAttackTarget(this.field_75304_b);
      super.startExecuting();
   }
}
