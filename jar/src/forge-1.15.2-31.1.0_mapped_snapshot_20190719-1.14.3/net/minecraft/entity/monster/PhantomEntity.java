package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PhantomEntity extends FlyingEntity implements IMob {
   private static final DataParameter<Integer> SIZE;
   private Vec3d orbitOffset;
   private BlockPos orbitPosition;
   private PhantomEntity.AttackPhase attackPhase;

   public PhantomEntity(EntityType<? extends PhantomEntity> p_i50200_1_, World p_i50200_2_) {
      super(p_i50200_1_, p_i50200_2_);
      this.orbitOffset = Vec3d.ZERO;
      this.orbitPosition = BlockPos.ZERO;
      this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
      this.experienceValue = 5;
      this.moveController = new PhantomEntity.MoveHelperController(this);
      this.lookController = new PhantomEntity.LookHelperController(this);
   }

   protected BodyController createBodyController() {
      return new PhantomEntity.BodyHelperController(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PhantomEntity.PickAttackGoal());
      this.goalSelector.addGoal(2, new PhantomEntity.SweepAttackGoal());
      this.goalSelector.addGoal(3, new PhantomEntity.OrbitPointGoal());
      this.targetSelector.addGoal(1, new PhantomEntity.AttackPlayerGoal());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SIZE, 0);
   }

   public void setPhantomSize(int p_203034_1_) {
      this.dataManager.set(SIZE, MathHelper.clamp(p_203034_1_, 0, 64));
   }

   private void updatePhantomSize() {
      this.recalculateSize();
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
   }

   public int getPhantomSize() {
      return (Integer)this.dataManager.get(SIZE);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.35F;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (SIZE.equals(p_184206_1_)) {
         this.updatePhantomSize();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   protected boolean func_225511_J_() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         float lvt_1_1_ = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + 3.1415927F);
         float lvt_2_1_ = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + 3.1415927F);
         if (lvt_1_1_ > 0.0F && lvt_2_1_ <= 0.0F) {
            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
         }

         int lvt_3_1_ = this.getPhantomSize();
         float lvt_4_1_ = MathHelper.cos(this.rotationYaw * 0.017453292F) * (1.3F + 0.21F * (float)lvt_3_1_);
         float lvt_5_1_ = MathHelper.sin(this.rotationYaw * 0.017453292F) * (1.3F + 0.21F * (float)lvt_3_1_);
         float lvt_6_1_ = (0.3F + lvt_1_1_ * 0.45F) * ((float)lvt_3_1_ * 0.2F + 1.0F);
         this.world.addParticle(ParticleTypes.MYCELIUM, this.func_226277_ct_() + (double)lvt_4_1_, this.func_226278_cu_() + (double)lvt_6_1_, this.func_226281_cx_() + (double)lvt_5_1_, 0.0D, 0.0D, 0.0D);
         this.world.addParticle(ParticleTypes.MYCELIUM, this.func_226277_ct_() - (double)lvt_4_1_, this.func_226278_cu_() + (double)lvt_6_1_, this.func_226281_cx_() - (double)lvt_5_1_, 0.0D, 0.0D, 0.0D);
      }

   }

   public void livingTick() {
      if (this.isAlive() && this.isInDaylight()) {
         this.setFire(8);
      }

      super.livingTick();
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.orbitPosition = (new BlockPos(this)).up(5);
      this.setPhantomSize(0);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("AX")) {
         this.orbitPosition = new BlockPos(p_70037_1_.getInt("AX"), p_70037_1_.getInt("AY"), p_70037_1_.getInt("AZ"));
      }

      this.setPhantomSize(p_70037_1_.getInt("Size"));
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("AX", this.orbitPosition.getX());
      p_213281_1_.putInt("AY", this.orbitPosition.getY());
      p_213281_1_.putInt("AZ", this.orbitPosition.getZ());
      p_213281_1_.putInt("Size", this.getPhantomSize());
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return true;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PHANTOM_DEATH;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttack(EntityType<?> p_213358_1_) {
      return true;
   }

   public EntitySize getSize(Pose p_213305_1_) {
      int lvt_2_1_ = this.getPhantomSize();
      EntitySize lvt_3_1_ = super.getSize(p_213305_1_);
      float lvt_4_1_ = (lvt_3_1_.width + 0.2F * (float)lvt_2_1_) / lvt_3_1_.width;
      return lvt_3_1_.scale(lvt_4_1_);
   }

   static {
      SIZE = EntityDataManager.createKey(PhantomEntity.class, DataSerializers.VARINT);
   }

   class AttackPlayerGoal extends Goal {
      private final EntityPredicate field_220842_b;
      private int tickDelay;

      private AttackPlayerGoal() {
         this.field_220842_b = (new EntityPredicate()).setDistance(64.0D);
         this.tickDelay = 20;
      }

      public boolean shouldExecute() {
         if (this.tickDelay > 0) {
            --this.tickDelay;
            return false;
         } else {
            this.tickDelay = 60;
            List<PlayerEntity> lvt_1_1_ = PhantomEntity.this.world.getTargettablePlayersWithinAABB(this.field_220842_b, PhantomEntity.this, PhantomEntity.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));
            if (!lvt_1_1_.isEmpty()) {
               lvt_1_1_.sort((p_203140_0_, p_203140_1_) -> {
                  return p_203140_0_.func_226278_cu_() > p_203140_1_.func_226278_cu_() ? -1 : 1;
               });
               Iterator var2 = lvt_1_1_.iterator();

               while(var2.hasNext()) {
                  PlayerEntity lvt_3_1_ = (PlayerEntity)var2.next();
                  if (PhantomEntity.this.func_213344_a(lvt_3_1_, EntityPredicate.DEFAULT)) {
                     PhantomEntity.this.setAttackTarget(lvt_3_1_);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         LivingEntity lvt_1_1_ = PhantomEntity.this.getAttackTarget();
         return lvt_1_1_ != null ? PhantomEntity.this.func_213344_a(lvt_1_1_, EntityPredicate.DEFAULT) : false;
      }

      // $FF: synthetic method
      AttackPlayerGoal(Object p_i48809_2_) {
         this();
      }
   }

   class PickAttackGoal extends Goal {
      private int tickDelay;

      private PickAttackGoal() {
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = PhantomEntity.this.getAttackTarget();
         return lvt_1_1_ != null ? PhantomEntity.this.func_213344_a(PhantomEntity.this.getAttackTarget(), EntityPredicate.DEFAULT) : false;
      }

      public void startExecuting() {
         this.tickDelay = 10;
         PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
         this.func_203143_f();
      }

      public void resetTask() {
         PhantomEntity.this.orbitPosition = PhantomEntity.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.orbitPosition).up(10 + PhantomEntity.this.rand.nextInt(20));
      }

      public void tick() {
         if (PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.CIRCLE) {
            --this.tickDelay;
            if (this.tickDelay <= 0) {
               PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.SWOOP;
               this.func_203143_f();
               this.tickDelay = (8 + PhantomEntity.this.rand.nextInt(4)) * 20;
               PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + PhantomEntity.this.rand.nextFloat() * 0.1F);
            }
         }

      }

      private void func_203143_f() {
         PhantomEntity.this.orbitPosition = (new BlockPos(PhantomEntity.this.getAttackTarget())).up(20 + PhantomEntity.this.rand.nextInt(20));
         if (PhantomEntity.this.orbitPosition.getY() < PhantomEntity.this.world.getSeaLevel()) {
            PhantomEntity.this.orbitPosition = new BlockPos(PhantomEntity.this.orbitPosition.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.orbitPosition.getZ());
         }

      }

      // $FF: synthetic method
      PickAttackGoal(Object p_i48807_2_) {
         this();
      }
   }

   class SweepAttackGoal extends PhantomEntity.MoveGoal {
      private SweepAttackGoal() {
         super();
      }

      public boolean shouldExecute() {
         return PhantomEntity.this.getAttackTarget() != null && PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.SWOOP;
      }

      public boolean shouldContinueExecuting() {
         LivingEntity lvt_1_1_ = PhantomEntity.this.getAttackTarget();
         if (lvt_1_1_ == null) {
            return false;
         } else if (!lvt_1_1_.isAlive()) {
            return false;
         } else if (lvt_1_1_ instanceof PlayerEntity && (((PlayerEntity)lvt_1_1_).isSpectator() || ((PlayerEntity)lvt_1_1_).isCreative())) {
            return false;
         } else if (!this.shouldExecute()) {
            return false;
         } else {
            if (PhantomEntity.this.ticksExisted % 20 == 0) {
               List<CatEntity> lvt_2_1_ = PhantomEntity.this.world.getEntitiesWithinAABB(CatEntity.class, PhantomEntity.this.getBoundingBox().grow(16.0D), EntityPredicates.IS_ALIVE);
               if (!lvt_2_1_.isEmpty()) {
                  Iterator var3 = lvt_2_1_.iterator();

                  while(var3.hasNext()) {
                     CatEntity lvt_4_1_ = (CatEntity)var3.next();
                     lvt_4_1_.func_213420_ej();
                  }

                  return false;
               }
            }

            return true;
         }
      }

      public void startExecuting() {
      }

      public void resetTask() {
         PhantomEntity.this.setAttackTarget((LivingEntity)null);
         PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
      }

      public void tick() {
         LivingEntity lvt_1_1_ = PhantomEntity.this.getAttackTarget();
         PhantomEntity.this.orbitOffset = new Vec3d(lvt_1_1_.func_226277_ct_(), lvt_1_1_.func_226283_e_(0.5D), lvt_1_1_.func_226281_cx_());
         if (PhantomEntity.this.getBoundingBox().grow(0.20000000298023224D).intersects(lvt_1_1_.getBoundingBox())) {
            PhantomEntity.this.attackEntityAsMob(lvt_1_1_);
            PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
            PhantomEntity.this.world.playEvent(1039, new BlockPos(PhantomEntity.this), 0);
         } else if (PhantomEntity.this.collidedHorizontally || PhantomEntity.this.hurtTime > 0) {
            PhantomEntity.this.attackPhase = PhantomEntity.AttackPhase.CIRCLE;
         }

      }

      // $FF: synthetic method
      SweepAttackGoal(Object p_i48799_2_) {
         this();
      }
   }

   class OrbitPointGoal extends PhantomEntity.MoveGoal {
      private float field_203150_c;
      private float field_203151_d;
      private float field_203152_e;
      private float field_203153_f;

      private OrbitPointGoal() {
         super();
      }

      public boolean shouldExecute() {
         return PhantomEntity.this.getAttackTarget() == null || PhantomEntity.this.attackPhase == PhantomEntity.AttackPhase.CIRCLE;
      }

      public void startExecuting() {
         this.field_203151_d = 5.0F + PhantomEntity.this.rand.nextFloat() * 10.0F;
         this.field_203152_e = -4.0F + PhantomEntity.this.rand.nextFloat() * 9.0F;
         this.field_203153_f = PhantomEntity.this.rand.nextBoolean() ? 1.0F : -1.0F;
         this.func_203148_i();
      }

      public void tick() {
         if (PhantomEntity.this.rand.nextInt(350) == 0) {
            this.field_203152_e = -4.0F + PhantomEntity.this.rand.nextFloat() * 9.0F;
         }

         if (PhantomEntity.this.rand.nextInt(250) == 0) {
            ++this.field_203151_d;
            if (this.field_203151_d > 15.0F) {
               this.field_203151_d = 5.0F;
               this.field_203153_f = -this.field_203153_f;
            }
         }

         if (PhantomEntity.this.rand.nextInt(450) == 0) {
            this.field_203150_c = PhantomEntity.this.rand.nextFloat() * 2.0F * 3.1415927F;
            this.func_203148_i();
         }

         if (this.func_203146_f()) {
            this.func_203148_i();
         }

         if (PhantomEntity.this.orbitOffset.y < PhantomEntity.this.func_226278_cu_() && !PhantomEntity.this.world.isAirBlock((new BlockPos(PhantomEntity.this)).down(1))) {
            this.field_203152_e = Math.max(1.0F, this.field_203152_e);
            this.func_203148_i();
         }

         if (PhantomEntity.this.orbitOffset.y > PhantomEntity.this.func_226278_cu_() && !PhantomEntity.this.world.isAirBlock((new BlockPos(PhantomEntity.this)).up(1))) {
            this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
            this.func_203148_i();
         }

      }

      private void func_203148_i() {
         if (BlockPos.ZERO.equals(PhantomEntity.this.orbitPosition)) {
            PhantomEntity.this.orbitPosition = new BlockPos(PhantomEntity.this);
         }

         this.field_203150_c += this.field_203153_f * 15.0F * 0.017453292F;
         PhantomEntity.this.orbitOffset = (new Vec3d(PhantomEntity.this.orbitPosition)).add((double)(this.field_203151_d * MathHelper.cos(this.field_203150_c)), (double)(-4.0F + this.field_203152_e), (double)(this.field_203151_d * MathHelper.sin(this.field_203150_c)));
      }

      // $FF: synthetic method
      OrbitPointGoal(Object p_i48804_2_) {
         this();
      }
   }

   abstract class MoveGoal extends Goal {
      public MoveGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      protected boolean func_203146_f() {
         return PhantomEntity.this.orbitOffset.squareDistanceTo(PhantomEntity.this.func_226277_ct_(), PhantomEntity.this.func_226278_cu_(), PhantomEntity.this.func_226281_cx_()) < 4.0D;
      }
   }

   class LookHelperController extends LookController {
      public LookHelperController(MobEntity p_i48802_2_) {
         super(p_i48802_2_);
      }

      public void tick() {
      }
   }

   class BodyHelperController extends BodyController {
      public BodyHelperController(MobEntity p_i49925_2_) {
         super(p_i49925_2_);
      }

      public void updateRenderAngles() {
         PhantomEntity.this.rotationYawHead = PhantomEntity.this.renderYawOffset;
         PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw;
      }
   }

   class MoveHelperController extends MovementController {
      private float speedFactor = 0.1F;

      public MoveHelperController(MobEntity p_i48801_2_) {
         super(p_i48801_2_);
      }

      public void tick() {
         if (PhantomEntity.this.collidedHorizontally) {
            PhantomEntity var10000 = PhantomEntity.this;
            var10000.rotationYaw += 180.0F;
            this.speedFactor = 0.1F;
         }

         float lvt_1_1_ = (float)(PhantomEntity.this.orbitOffset.x - PhantomEntity.this.func_226277_ct_());
         float lvt_2_1_ = (float)(PhantomEntity.this.orbitOffset.y - PhantomEntity.this.func_226278_cu_());
         float lvt_3_1_ = (float)(PhantomEntity.this.orbitOffset.z - PhantomEntity.this.func_226281_cx_());
         double lvt_4_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_);
         double lvt_6_1_ = 1.0D - (double)MathHelper.abs(lvt_2_1_ * 0.7F) / lvt_4_1_;
         lvt_1_1_ = (float)((double)lvt_1_1_ * lvt_6_1_);
         lvt_3_1_ = (float)((double)lvt_3_1_ * lvt_6_1_);
         lvt_4_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_);
         double lvt_8_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_2_1_ * lvt_2_1_);
         float lvt_10_1_ = PhantomEntity.this.rotationYaw;
         float lvt_11_1_ = (float)MathHelper.atan2((double)lvt_3_1_, (double)lvt_1_1_);
         float lvt_12_1_ = MathHelper.wrapDegrees(PhantomEntity.this.rotationYaw + 90.0F);
         float lvt_13_1_ = MathHelper.wrapDegrees(lvt_11_1_ * 57.295776F);
         PhantomEntity.this.rotationYaw = MathHelper.approachDegrees(lvt_12_1_, lvt_13_1_, 4.0F) - 90.0F;
         PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw;
         if (MathHelper.degreesDifferenceAbs(lvt_10_1_, PhantomEntity.this.rotationYaw) < 3.0F) {
            this.speedFactor = MathHelper.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
         } else {
            this.speedFactor = MathHelper.approach(this.speedFactor, 0.2F, 0.025F);
         }

         float lvt_14_1_ = (float)(-(MathHelper.atan2((double)(-lvt_2_1_), lvt_4_1_) * 57.2957763671875D));
         PhantomEntity.this.rotationPitch = lvt_14_1_;
         float lvt_15_1_ = PhantomEntity.this.rotationYaw + 90.0F;
         double lvt_16_1_ = (double)(this.speedFactor * MathHelper.cos(lvt_15_1_ * 0.017453292F)) * Math.abs((double)lvt_1_1_ / lvt_8_1_);
         double lvt_18_1_ = (double)(this.speedFactor * MathHelper.sin(lvt_15_1_ * 0.017453292F)) * Math.abs((double)lvt_3_1_ / lvt_8_1_);
         double lvt_20_1_ = (double)(this.speedFactor * MathHelper.sin(lvt_14_1_ * 0.017453292F)) * Math.abs((double)lvt_2_1_ / lvt_8_1_);
         Vec3d lvt_22_1_ = PhantomEntity.this.getMotion();
         PhantomEntity.this.setMotion(lvt_22_1_.add((new Vec3d(lvt_16_1_, lvt_20_1_, lvt_18_1_)).subtract(lvt_22_1_).scale(0.2D)));
      }
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;
   }
}
