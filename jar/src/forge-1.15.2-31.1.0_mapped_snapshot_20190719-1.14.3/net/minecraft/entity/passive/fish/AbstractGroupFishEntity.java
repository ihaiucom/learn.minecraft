package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowSchoolLeaderGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractGroupFishEntity extends AbstractFishEntity {
   private AbstractGroupFishEntity groupLeader;
   private int groupSize = 1;

   public AbstractGroupFishEntity(EntityType<? extends AbstractGroupFishEntity> p_i49856_1_, World p_i49856_2_) {
      super(p_i49856_1_, p_i49856_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowSchoolLeaderGoal(this));
   }

   public int getMaxSpawnedInChunk() {
      return this.getMaxGroupSize();
   }

   public int getMaxGroupSize() {
      return super.getMaxSpawnedInChunk();
   }

   protected boolean func_212800_dy() {
      return !this.hasGroupLeader();
   }

   public boolean hasGroupLeader() {
      return this.groupLeader != null && this.groupLeader.isAlive();
   }

   public AbstractGroupFishEntity func_212803_a(AbstractGroupFishEntity p_212803_1_) {
      this.groupLeader = p_212803_1_;
      p_212803_1_.increaseGroupSize();
      return p_212803_1_;
   }

   public void leaveGroup() {
      this.groupLeader.decreaseGroupSize();
      this.groupLeader = null;
   }

   private void increaseGroupSize() {
      ++this.groupSize;
   }

   private void decreaseGroupSize() {
      --this.groupSize;
   }

   public boolean canGroupGrow() {
      return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
   }

   public void tick() {
      super.tick();
      if (this.isGroupLeader() && this.world.rand.nextInt(200) == 1) {
         List<AbstractFishEntity> lvt_1_1_ = this.world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
         if (lvt_1_1_.size() <= 1) {
            this.groupSize = 1;
         }
      }

   }

   public boolean isGroupLeader() {
      return this.groupSize > 1;
   }

   public boolean inRangeOfGroupLeader() {
      return this.getDistanceSq(this.groupLeader) <= 121.0D;
   }

   public void moveToGroupLeader() {
      if (this.hasGroupLeader()) {
         this.getNavigator().tryMoveToEntityLiving(this.groupLeader, 1.0D);
      }

   }

   public void func_212810_a(Stream<AbstractGroupFishEntity> p_212810_1_) {
      p_212810_1_.limit((long)(this.getMaxGroupSize() - this.groupSize)).filter((p_212801_1_) -> {
         return p_212801_1_ != this;
      }).forEach((p_212804_1_) -> {
         p_212804_1_.func_212803_a(this);
      });
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
      if (p_213386_4_ == null) {
         p_213386_4_ = new AbstractGroupFishEntity.GroupData(this);
      } else {
         this.func_212803_a(((AbstractGroupFishEntity.GroupData)p_213386_4_).groupLeader);
      }

      return (ILivingEntityData)p_213386_4_;
   }

   public static class GroupData implements ILivingEntityData {
      public final AbstractGroupFishEntity groupLeader;

      public GroupData(AbstractGroupFishEntity p_i49858_1_) {
         this.groupLeader = p_i49858_1_;
      }
   }
}
