package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IllusionerEntity extends SpellcastingIllagerEntity implements IRangedAttackMob {
   private int ghostTime;
   private final Vec3d[][] renderLocations;

   public IllusionerEntity(EntityType<? extends IllusionerEntity> p_i50203_1_, World p_i50203_2_) {
      super(p_i50203_1_, p_i50203_2_);
      this.experienceValue = 5;
      this.renderLocations = new Vec3d[2][4];

      for(int i = 0; i < 4; ++i) {
         this.renderLocations[0][i] = Vec3d.ZERO;
         this.renderLocations[1][i] = Vec3d.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new SpellcastingIllagerEntity.CastingASpellGoal());
      this.goalSelector.addGoal(4, new IllusionerEntity.MirrorSpellGoal());
      this.goalSelector.addGoal(5, new IllusionerEntity.BlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal(this, 0.5D, 20, 15.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(18.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(32.0D);
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void registerData() {
      super.registerData();
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBoundingBox().grow(3.0D, 0.0D, 3.0D);
   }

   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote && this.isInvisible()) {
         --this.ghostTime;
         if (this.ghostTime < 0) {
            this.ghostTime = 0;
         }

         if (this.hurtTime != 1 && this.ticksExisted % 1200 != 0) {
            if (this.hurtTime == this.maxHurtTime - 1) {
               this.ghostTime = 3;

               for(int k = 0; k < 4; ++k) {
                  this.renderLocations[0][k] = this.renderLocations[1][k];
                  this.renderLocations[1][k] = new Vec3d(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.ghostTime = 3;
            float f = -6.0F;
            int i = true;

            int l;
            for(l = 0; l < 4; ++l) {
               this.renderLocations[0][l] = this.renderLocations[1][l];
               this.renderLocations[1][l] = new Vec3d((double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D, (double)Math.max(0, this.rand.nextInt(6) - 4), (double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D);
            }

            for(l = 0; l < 16; ++l) {
               this.world.addParticle(ParticleTypes.CLOUD, this.func_226282_d_(0.5D), this.func_226279_cv_(), this.func_226285_f_(0.5D), 0.0D, 0.0D, 0.0D);
            }

            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d[] getRenderLocations(float p_193098_1_) {
      if (this.ghostTime <= 0) {
         return this.renderLocations[1];
      } else {
         double d0 = (double)(((float)this.ghostTime - p_193098_1_) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vec3d[] avec3d = new Vec3d[4];

         for(int i = 0; i < 4; ++i) {
            avec3d[i] = this.renderLocations[1][i].scale(1.0D - d0).add(this.renderLocations[0][i].scale(d0));
         }

         return avec3d;
      }
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (super.isOnSameTeam(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ILLUSIONER_HURT;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = ProjectileHelper.func_221272_a(this, itemstack, p_82196_2_);
      if (this.getHeldItemMainhand().getItem() instanceof BowItem) {
         abstractarrowentity = ((BowItem)this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
      }

      double d0 = p_82196_1_.func_226277_ct_() - this.func_226277_ct_();
      double d1 = p_82196_1_.func_226283_e_(0.3333333333333333D) - abstractarrowentity.func_226278_cu_();
      double d2 = p_82196_1_.func_226281_cx_() - this.func_226281_cx_();
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(abstractarrowentity);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isSpellcasting()) {
         return AbstractIllagerEntity.ArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllagerEntity.ArmPose.BOW_AND_ARROW : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   class MirrorSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private MirrorSpellGoal() {
         super();
      }

      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            return !IllusionerEntity.this.isPotionActive(Effects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         IllusionerEntity.this.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.DISAPPEAR;
      }

      // $FF: synthetic method
      MirrorSpellGoal(Object p_i47480_2_) {
         this();
      }
   }

   class BlindnessSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private int lastTargetId;

      private BlindnessSpellGoal() {
         super();
      }

      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else if (IllusionerEntity.this.getAttackTarget() == null) {
            return false;
         } else {
            return IllusionerEntity.this.getAttackTarget().getEntityId() == this.lastTargetId ? false : IllusionerEntity.this.world.getDifficultyForLocation(new BlockPos(IllusionerEntity.this)).isHarderThan((float)Difficulty.NORMAL.ordinal());
         }
      }

      public void startExecuting() {
         super.startExecuting();
         this.lastTargetId = IllusionerEntity.this.getAttackTarget().getEntityId();
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void castSpell() {
         IllusionerEntity.this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.BLINDNESS, 400));
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.BLINDNESS;
      }

      // $FF: synthetic method
      BlindnessSpellGoal(Object p_i47482_2_) {
         this();
      }
   }
}
