package net.minecraft.entity.passive.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TraderLlamaEntity extends LlamaEntity {
   private int despawnDelay = 47999;

   public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> p_i50234_1_, World p_i50234_2_) {
      super(p_i50234_1_, p_i50234_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_213800_eB() {
      return true;
   }

   protected LlamaEntity createChild() {
      return (LlamaEntity)EntityType.TRADER_LLAMA.create(this.world);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("DespawnDelay", this.despawnDelay);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("DespawnDelay", 99)) {
         this.despawnDelay = p_70037_1_.getInt("DespawnDelay");
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.targetSelector.addGoal(1, new TraderLlamaEntity.FollowTraderGoal(this));
   }

   protected void mountTo(PlayerEntity p_110237_1_) {
      Entity lvt_2_1_ = this.getLeashHolder();
      if (!(lvt_2_1_ instanceof WanderingTraderEntity)) {
         super.mountTo(p_110237_1_);
      }
   }

   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote) {
         this.tryDespawn();
      }

   }

   private void tryDespawn() {
      if (this.canDespawn()) {
         this.despawnDelay = this.isLeashedToTrader() ? ((WanderingTraderEntity)this.getLeashHolder()).func_213735_eg() - 1 : this.despawnDelay - 1;
         if (this.despawnDelay <= 0) {
            this.clearLeashed(true, false);
            this.remove();
         }

      }
   }

   private boolean canDespawn() {
      return !this.isTame() && !this.isLeashedToStranger() && !this.isOnePlayerRiding();
   }

   private boolean isLeashedToTrader() {
      return this.getLeashHolder() instanceof WanderingTraderEntity;
   }

   private boolean isLeashedToStranger() {
      return this.getLeashed() && !this.isLeashedToTrader();
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_3_ == SpawnReason.EVENT) {
         this.setGrowingAge(0);
      }

      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226259_a_(false);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   public class FollowTraderGoal extends TargetGoal {
      private final LlamaEntity field_220800_b;
      private LivingEntity field_220801_c;
      private int field_220802_d;

      public FollowTraderGoal(LlamaEntity p_i50458_2_) {
         super(p_i50458_2_, false);
         this.field_220800_b = p_i50458_2_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
      }

      public boolean shouldExecute() {
         if (!this.field_220800_b.getLeashed()) {
            return false;
         } else {
            Entity lvt_1_1_ = this.field_220800_b.getLeashHolder();
            if (!(lvt_1_1_ instanceof WanderingTraderEntity)) {
               return false;
            } else {
               WanderingTraderEntity lvt_2_1_ = (WanderingTraderEntity)lvt_1_1_;
               this.field_220801_c = lvt_2_1_.getRevengeTarget();
               int lvt_3_1_ = lvt_2_1_.getRevengeTimer();
               return lvt_3_1_ != this.field_220802_d && this.isSuitableTarget(this.field_220801_c, EntityPredicate.DEFAULT);
            }
         }
      }

      public void startExecuting() {
         this.goalOwner.setAttackTarget(this.field_220801_c);
         Entity lvt_1_1_ = this.field_220800_b.getLeashHolder();
         if (lvt_1_1_ instanceof WanderingTraderEntity) {
            this.field_220802_d = ((WanderingTraderEntity)lvt_1_1_).getRevengeTimer();
         }

         super.startExecuting();
      }
   }
}
