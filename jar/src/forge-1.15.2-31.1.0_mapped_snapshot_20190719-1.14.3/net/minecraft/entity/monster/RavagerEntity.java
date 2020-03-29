package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class RavagerEntity extends AbstractRaiderEntity {
   private static final Predicate<Entity> field_213690_b = (p_lambda$static$0_0_) -> {
      return p_lambda$static$0_0_.isAlive() && !(p_lambda$static$0_0_ instanceof RavagerEntity);
   };
   private int attackTick;
   private int stunTick;
   private int roarTick;

   public RavagerEntity(EntityType<? extends RavagerEntity> p_i50197_1_, World p_i50197_2_) {
      super(p_i50197_1_, p_i50197_2_);
      this.stepHeight = 1.0F;
      this.experienceValue = 20;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(4, new RavagerEntity.AttackGoal());
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
   }

   protected void func_213385_F() {
      boolean flag = !(this.getControllingPassenger() instanceof MobEntity) || this.getControllingPassenger().getType().isContained(EntityTypeTags.RAIDERS);
      boolean flag1 = !(this.getRidingEntity() instanceof BoatEntity);
      this.goalSelector.setFlag(Goal.Flag.MOVE, flag);
      this.goalSelector.setFlag(Goal.Flag.JUMP, flag && flag1);
      this.goalSelector.setFlag(Goal.Flag.LOOK, flag);
      this.goalSelector.setFlag(Goal.Flag.TARGET, flag);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("AttackTick", this.attackTick);
      p_213281_1_.putInt("StunTick", this.stunTick);
      p_213281_1_.putInt("RoarTick", this.roarTick);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.attackTick = p_70037_1_.getInt("AttackTick");
      this.stunTick = p_70037_1_.getInt("StunTick");
      this.roarTick = p_70037_1_.getInt("RoarTick");
   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_RAVAGER_CELEBRATE;
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new RavagerEntity.Navigator(this, p_175447_1_);
   }

   public int getHorizontalFaceSpeed() {
      return 45;
   }

   public double getMountedYOffset() {
      return 2.1D;
   }

   public boolean canBeSteered() {
      return !this.isAIDisabled() && this.getControllingPassenger() instanceof LivingEntity;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   public void livingTick() {
      super.livingTick();
      if (this.isAlive()) {
         if (this.isMovementBlocked()) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         } else {
            double d0 = this.getAttackTarget() != null ? 0.35D : 0.3D;
            double d1 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1D, d1, d0));
         }

         if (this.collidedHorizontally && ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
            boolean flag = false;
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(0.2D);
            Iterator var8 = BlockPos.getAllInBoxMutable(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ)).iterator();

            label62:
            while(true) {
               BlockPos blockpos;
               Block block;
               do {
                  if (!var8.hasNext()) {
                     if (!flag && this.onGround) {
                        this.jump();
                     }
                     break label62;
                  }

                  blockpos = (BlockPos)var8.next();
                  BlockState blockstate = this.world.getBlockState(blockpos);
                  block = blockstate.getBlock();
               } while(!(block instanceof LeavesBlock));

               flag = this.world.func_225521_a_(blockpos, true, this) || flag;
            }
         }

         if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
               this.roar();
            }
         }

         if (this.attackTick > 0) {
            --this.attackTick;
         }

         if (this.stunTick > 0) {
            --this.stunTick;
            this.func_213682_eh();
            if (this.stunTick == 0) {
               this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
               this.roarTick = 20;
            }
         }
      }

   }

   private void func_213682_eh() {
      if (this.rand.nextInt(6) == 0) {
         double d0 = this.func_226277_ct_() - (double)this.getWidth() * Math.sin((double)(this.renderYawOffset * 0.017453292F)) + (this.rand.nextDouble() * 0.6D - 0.3D);
         double d1 = this.func_226278_cu_() + (double)this.getHeight() - 0.3D;
         double d2 = this.func_226281_cx_() + (double)this.getWidth() * Math.cos((double)(this.renderYawOffset * 0.017453292F)) + (this.rand.nextDouble() * 0.6D - 0.3D);
         this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
      }

   }

   protected boolean isMovementBlocked() {
      return super.isMovementBlocked() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
   }

   public boolean canEntityBeSeen(Entity p_70685_1_) {
      return this.stunTick <= 0 && this.roarTick <= 0 ? super.canEntityBeSeen(p_70685_1_) : false;
   }

   protected void func_213371_e(LivingEntity p_213371_1_) {
      if (this.roarTick == 0) {
         if (this.rand.nextDouble() < 0.5D) {
            this.stunTick = 40;
            this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
            this.world.setEntityState(this, (byte)39);
            p_213371_1_.applyEntityCollision(this);
         } else {
            this.launch(p_213371_1_);
         }

         p_213371_1_.velocityChanged = true;
      }

   }

   private void roar() {
      if (this.isAlive()) {
         Entity entity;
         for(Iterator var1 = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(4.0D), field_213690_b).iterator(); var1.hasNext(); this.launch(entity)) {
            entity = (Entity)var1.next();
            if (!(entity instanceof AbstractIllagerEntity)) {
               entity.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F);
            }
         }

         Vec3d vec3d = this.getBoundingBox().getCenter();

         for(int i = 0; i < 40; ++i) {
            double d0 = this.rand.nextGaussian() * 0.2D;
            double d1 = this.rand.nextGaussian() * 0.2D;
            double d2 = this.rand.nextGaussian() * 0.2D;
            this.world.addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
         }
      }

   }

   private void launch(Entity p_213688_1_) {
      double d0 = p_213688_1_.func_226277_ct_() - this.func_226277_ct_();
      double d1 = p_213688_1_.func_226281_cx_() - this.func_226281_cx_();
      double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
      p_213688_1_.addVelocity(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackTick = 10;
         this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
      } else if (p_70103_1_ == 39) {
         this.stunTick = 40;
      }

      super.handleStatusUpdate(p_70103_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_213683_l() {
      return this.attackTick;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_213684_dX() {
      return this.stunTick;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_213687_eg() {
      return this.roarTick;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      this.attackTick = 10;
      this.world.setEntityState(this, (byte)4);
      this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.attackEntityAsMob(p_70652_1_);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RAVAGER_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   public boolean canBeLeader() {
      return false;
   }

   static class Processor extends WalkNodeProcessor {
      private Processor() {
      }

      protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
         return p_215744_5_ == PathNodeType.LEAVES ? PathNodeType.OPEN : super.func_215744_a(p_215744_1_, p_215744_2_, p_215744_3_, p_215744_4_, p_215744_5_);
      }

      // $FF: synthetic method
      Processor(Object p_i50753_1_) {
         this();
      }
   }

   static class Navigator extends GroundPathNavigator {
      public Navigator(MobEntity p_i50754_1_, World p_i50754_2_) {
         super(p_i50754_1_, p_i50754_2_);
      }

      protected PathFinder getPathFinder(int p_179679_1_) {
         this.nodeProcessor = new RavagerEntity.Processor();
         return new PathFinder(this.nodeProcessor, p_179679_1_);
      }
   }

   class AttackGoal extends MeleeAttackGoal {
      public AttackGoal() {
         super(RavagerEntity.this, 1.0D, true);
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         float f = RavagerEntity.this.getWidth() - 0.1F;
         return (double)(f * 2.0F * f * 2.0F + p_179512_1_.getWidth());
      }
   }
}
