package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.raid.Raid;

public abstract class PatrollerEntity extends MonsterEntity {
   private BlockPos patrolTarget;
   private boolean patrolLeader;
   private boolean patrolling;

   protected PatrollerEntity(EntityType<? extends PatrollerEntity> p_i50201_1_, World p_i50201_2_) {
      super(p_i50201_1_, p_i50201_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(4, new PatrollerEntity.PatrolGoal(this, 0.7D, 0.595D));
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.patrolTarget != null) {
         p_213281_1_.put("PatrolTarget", NBTUtil.writeBlockPos(this.patrolTarget));
      }

      p_213281_1_.putBoolean("PatrolLeader", this.patrolLeader);
      p_213281_1_.putBoolean("Patrolling", this.patrolling);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("PatrolTarget")) {
         this.patrolTarget = NBTUtil.readBlockPos(p_70037_1_.getCompound("PatrolTarget"));
      }

      this.patrolLeader = p_70037_1_.getBoolean("PatrolLeader");
      this.patrolling = p_70037_1_.getBoolean("Patrolling");
   }

   public double getYOffset() {
      return -0.45D;
   }

   public boolean canBeLeader() {
      return true;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_3_ != SpawnReason.PATROL && p_213386_3_ != SpawnReason.EVENT && p_213386_3_ != SpawnReason.STRUCTURE && this.rand.nextFloat() < 0.06F && this.canBeLeader()) {
         this.patrolLeader = true;
      }

      if (this.isLeader()) {
         this.setItemStackToSlot(EquipmentSlotType.HEAD, Raid.createIllagerBanner());
         this.setDropChance(EquipmentSlotType.HEAD, 2.0F);
      }

      if (p_213386_3_ == SpawnReason.PATROL) {
         this.patrolling = true;
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public static boolean func_223330_b(EntityType<? extends PatrollerEntity> p_223330_0_, IWorld p_223330_1_, SpawnReason p_223330_2_, BlockPos p_223330_3_, Random p_223330_4_) {
      return p_223330_1_.func_226658_a_(LightType.BLOCK, p_223330_3_) > 8 ? false : func_223324_d(p_223330_0_, p_223330_1_, p_223330_2_, p_223330_3_, p_223330_4_);
   }

   public boolean canDespawn(double p_213397_1_) {
      return !this.patrolling || p_213397_1_ > 16384.0D;
   }

   public void setPatrolTarget(BlockPos p_213631_1_) {
      this.patrolTarget = p_213631_1_;
      this.patrolling = true;
   }

   public BlockPos getPatrolTarget() {
      return this.patrolTarget;
   }

   public boolean hasPatrolTarget() {
      return this.patrolTarget != null;
   }

   public void setLeader(boolean p_213635_1_) {
      this.patrolLeader = p_213635_1_;
      this.patrolling = true;
   }

   public boolean isLeader() {
      return this.patrolLeader;
   }

   public boolean func_213634_ed() {
      return true;
   }

   public void resetPatrolTarget() {
      this.patrolTarget = (new BlockPos(this)).add(-500 + this.rand.nextInt(1000), 0, -500 + this.rand.nextInt(1000));
      this.patrolling = true;
   }

   protected boolean isPatrolling() {
      return this.patrolling;
   }

   protected void func_226541_s_(boolean p_226541_1_) {
      this.patrolling = p_226541_1_;
   }

   public static class PatrolGoal<T extends PatrollerEntity> extends Goal {
      private final T owner;
      private final double field_220840_b;
      private final double field_220841_c;
      private long field_226542_d_;

      public PatrolGoal(T p_i50070_1_, double p_i50070_2_, double p_i50070_4_) {
         this.owner = p_i50070_1_;
         this.field_220840_b = p_i50070_2_;
         this.field_220841_c = p_i50070_4_;
         this.field_226542_d_ = -1L;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         boolean lvt_1_1_ = this.owner.world.getGameTime() < this.field_226542_d_;
         return this.owner.isPatrolling() && this.owner.getAttackTarget() == null && !this.owner.isBeingRidden() && this.owner.hasPatrolTarget() && !lvt_1_1_;
      }

      public void startExecuting() {
      }

      public void resetTask() {
      }

      public void tick() {
         boolean lvt_1_1_ = this.owner.isLeader();
         PathNavigator lvt_2_1_ = this.owner.getNavigator();
         if (lvt_2_1_.noPath()) {
            List<PatrollerEntity> lvt_3_1_ = this.func_226544_g_();
            if (this.owner.isPatrolling() && lvt_3_1_.isEmpty()) {
               this.owner.func_226541_s_(false);
            } else if (lvt_1_1_ && this.owner.getPatrolTarget().withinDistance(this.owner.getPositionVec(), 10.0D)) {
               this.owner.resetPatrolTarget();
            } else {
               Vec3d lvt_4_1_ = new Vec3d(this.owner.getPatrolTarget());
               Vec3d lvt_5_1_ = this.owner.getPositionVec();
               Vec3d lvt_6_1_ = lvt_5_1_.subtract(lvt_4_1_);
               lvt_4_1_ = lvt_6_1_.rotateYaw(90.0F).scale(0.4D).add(lvt_4_1_);
               Vec3d lvt_7_1_ = lvt_4_1_.subtract(lvt_5_1_).normalize().scale(10.0D).add(lvt_5_1_);
               BlockPos lvt_8_1_ = new BlockPos(lvt_7_1_);
               lvt_8_1_ = this.owner.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_8_1_);
               if (!lvt_2_1_.tryMoveToXYZ((double)lvt_8_1_.getX(), (double)lvt_8_1_.getY(), (double)lvt_8_1_.getZ(), lvt_1_1_ ? this.field_220841_c : this.field_220840_b)) {
                  this.func_226545_h_();
                  this.field_226542_d_ = this.owner.world.getGameTime() + 200L;
               } else if (lvt_1_1_) {
                  Iterator var9 = lvt_3_1_.iterator();

                  while(var9.hasNext()) {
                     PatrollerEntity lvt_10_1_ = (PatrollerEntity)var9.next();
                     lvt_10_1_.setPatrolTarget(lvt_8_1_);
                  }
               }
            }
         }

      }

      private List<PatrollerEntity> func_226544_g_() {
         return this.owner.world.getEntitiesWithinAABB(PatrollerEntity.class, this.owner.getBoundingBox().grow(16.0D), (p_226543_1_) -> {
            return p_226543_1_.func_213634_ed() && !p_226543_1_.isEntityEqual(this.owner);
         });
      }

      private boolean func_226545_h_() {
         Random lvt_1_1_ = this.owner.getRNG();
         BlockPos lvt_2_1_ = this.owner.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.owner)).add(-8 + lvt_1_1_.nextInt(16), 0, -8 + lvt_1_1_.nextInt(16)));
         return this.owner.getNavigator().tryMoveToXYZ((double)lvt_2_1_.getX(), (double)lvt_2_1_.getY(), (double)lvt_2_1_.getZ(), this.field_220840_b);
      }
   }
}
