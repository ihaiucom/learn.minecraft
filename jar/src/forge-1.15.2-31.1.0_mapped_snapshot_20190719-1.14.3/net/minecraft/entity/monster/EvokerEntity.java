package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EvokerEntity extends SpellcastingIllagerEntity {
   private SheepEntity wololoTarget;

   public EvokerEntity(EntityType<? extends EvokerEntity> p_i50207_1_, World p_i50207_2_) {
      super(p_i50207_1_, p_i50207_2_);
      this.experienceValue = 10;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EvokerEntity.CastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal(this, PlayerEntity.class, 8.0F, 0.6D, 1.0D));
      this.goalSelector.addGoal(4, new EvokerEntity.SummonSpellGoal());
      this.goalSelector.addGoal(5, new EvokerEntity.AttackSpellGoal());
      this.goalSelector.addGoal(6, new EvokerEntity.WololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
   }

   protected void registerData() {
      super.registerData();
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_EVOKER_CELEBRATE;
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (p_184191_1_ == null) {
         return false;
      } else if (p_184191_1_ == this) {
         return true;
      } else if (super.isOnSameTeam(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof VexEntity) {
         return this.isOnSameTeam(((VexEntity)p_184191_1_).getOwner());
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_EVOKER_HURT;
   }

   private void setWololoTarget(@Nullable SheepEntity p_190748_1_) {
      this.wololoTarget = p_190748_1_;
   }

   @Nullable
   private SheepEntity getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   public class WololoSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate field_220845_e = (new EntityPredicate()).setDistance(16.0D).allowInvulnerable().setCustomPredicate((p_lambda$new$0_0_) -> {
         return ((SheepEntity)p_lambda$new$0_0_).getFleeceColor() == DyeColor.BLUE;
      });

      public WololoSpellGoal() {
         super();
      }

      public boolean shouldExecute() {
         if (EvokerEntity.this.getAttackTarget() != null) {
            return false;
         } else if (EvokerEntity.this.isSpellcasting()) {
            return false;
         } else if (EvokerEntity.this.ticksExisted < this.spellCooldown) {
            return false;
         } else if (!ForgeEventFactory.getMobGriefingEvent(EvokerEntity.this.world, EvokerEntity.this)) {
            return false;
         } else {
            List<SheepEntity> list = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(SheepEntity.class, this.field_220845_e, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0D, 4.0D, 16.0D));
            if (list.isEmpty()) {
               return false;
            } else {
               EvokerEntity.this.setWololoTarget((SheepEntity)list.get(EvokerEntity.this.rand.nextInt(list.size())));
               return true;
            }
         }
      }

      public boolean shouldContinueExecuting() {
         return EvokerEntity.this.getWololoTarget() != null && this.spellWarmup > 0;
      }

      public void resetTask() {
         super.resetTask();
         EvokerEntity.this.setWololoTarget((SheepEntity)null);
      }

      protected void castSpell() {
         SheepEntity sheepentity = EvokerEntity.this.getWololoTarget();
         if (sheepentity != null && sheepentity.isAlive()) {
            sheepentity.setFleeceColor(DyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.WOLOLO;
      }
   }

   class SummonSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private final EntityPredicate field_220843_e;

      private SummonSpellGoal() {
         super();
         this.field_220843_e = (new EntityPredicate()).setDistance(16.0D).setLineOfSiteRequired().setUseInvisibilityCheck().allowInvulnerable().allowFriendlyFire();
      }

      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            int i = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(VexEntity.class, this.field_220843_e, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0D)).size();
            return EvokerEntity.this.rand.nextInt(8) + 1 > i;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = (new BlockPos(EvokerEntity.this)).add(-2 + EvokerEntity.this.rand.nextInt(5), 1, -2 + EvokerEntity.this.rand.nextInt(5));
            VexEntity vexentity = (VexEntity)EntityType.VEX.create(EvokerEntity.this.world);
            vexentity.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
            vexentity.onInitialSpawn(EvokerEntity.this.world, EvokerEntity.this.world.getDifficultyForLocation(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
            vexentity.setOwner(EvokerEntity.this);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(20 * (30 + EvokerEntity.this.rand.nextInt(90)));
            EvokerEntity.this.world.addEntity(vexentity);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.SUMMON_VEX;
      }

      // $FF: synthetic method
      SummonSpellGoal(Object p_i47178_2_) {
         this();
      }
   }

   class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
      private CastingSpellGoal() {
         super();
      }

      public void tick() {
         if (EvokerEntity.this.getAttackTarget() != null) {
            EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getAttackTarget(), (float)EvokerEntity.this.getHorizontalFaceSpeed(), (float)EvokerEntity.this.getVerticalFaceSpeed());
         } else if (EvokerEntity.this.getWololoTarget() != null) {
            EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.getHorizontalFaceSpeed(), (float)EvokerEntity.this.getVerticalFaceSpeed());
         }

      }

      // $FF: synthetic method
      CastingSpellGoal(Object p_i47569_2_) {
         this();
      }
   }

   class AttackSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
      private AttackSpellGoal() {
         super();
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void castSpell() {
         LivingEntity livingentity = EvokerEntity.this.getAttackTarget();
         double d0 = Math.min(livingentity.func_226278_cu_(), EvokerEntity.this.func_226278_cu_());
         double d1 = Math.max(livingentity.func_226278_cu_(), EvokerEntity.this.func_226278_cu_()) + 1.0D;
         float f = (float)MathHelper.atan2(livingentity.func_226281_cx_() - EvokerEntity.this.func_226281_cx_(), livingentity.func_226277_ct_() - EvokerEntity.this.func_226277_ct_());
         int k;
         if (EvokerEntity.this.getDistanceSq(livingentity) < 9.0D) {
            float f2;
            for(k = 0; k < 5; ++k) {
               f2 = f + (float)k * 3.1415927F * 0.4F;
               this.spawnFangs(EvokerEntity.this.func_226277_ct_() + (double)MathHelper.cos(f2) * 1.5D, EvokerEntity.this.func_226281_cx_() + (double)MathHelper.sin(f2) * 1.5D, d0, d1, f2, 0);
            }

            for(k = 0; k < 8; ++k) {
               f2 = f + (float)k * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.spawnFangs(EvokerEntity.this.func_226277_ct_() + (double)MathHelper.cos(f2) * 2.5D, EvokerEntity.this.func_226281_cx_() + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
            }
         } else {
            for(k = 0; k < 16; ++k) {
               double d2 = 1.25D * (double)(k + 1);
               int j = 1 * k;
               this.spawnFangs(EvokerEntity.this.func_226277_ct_() + (double)MathHelper.cos(f) * d2, EvokerEntity.this.func_226281_cx_() + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
            }
         }

      }

      private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
         BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
         boolean flag = false;
         double d0 = 0.0D;

         do {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = EvokerEntity.this.world.getBlockState(blockpos1);
            if (blockstate.func_224755_d(EvokerEntity.this.world, blockpos1, Direction.UP)) {
               if (!EvokerEntity.this.world.isAirBlock(blockpos)) {
                  BlockState blockstate1 = EvokerEntity.this.world.getBlockState(blockpos);
                  VoxelShape voxelshape = blockstate1.getCollisionShape(EvokerEntity.this.world, blockpos);
                  if (!voxelshape.isEmpty()) {
                     d0 = voxelshape.getEnd(Direction.Axis.Y);
                  }
               }

               flag = true;
               break;
            }

            blockpos = blockpos.down();
         } while(blockpos.getY() >= MathHelper.floor(p_190876_5_) - 1);

         if (flag) {
            EvokerEntity.this.world.addEntity(new EvokerFangsEntity(EvokerEntity.this.world, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EvokerEntity.this));
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
      }

      protected SpellcastingIllagerEntity.SpellType getSpellType() {
         return SpellcastingIllagerEntity.SpellType.FANGS;
      }

      // $FF: synthetic method
      AttackSpellGoal(Object p_i47181_2_) {
         this();
      }
   }
}
