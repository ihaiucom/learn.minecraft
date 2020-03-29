package net.minecraft.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class SpellcastingIllagerEntity extends AbstractIllagerEntity {
   private static final DataParameter<Byte> SPELL;
   protected int spellTicks;
   private SpellcastingIllagerEntity.SpellType activeSpell;

   protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> p_i48551_1_, World p_i48551_2_) {
      super(p_i48551_1_, p_i48551_2_);
      this.activeSpell = SpellcastingIllagerEntity.SpellType.NONE;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SPELL, (byte)0);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.spellTicks = p_70037_1_.getInt("SpellTicks");
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("SpellTicks", this.spellTicks);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isSpellcasting()) {
         return AbstractIllagerEntity.ArmPose.SPELLCASTING;
      } else {
         return this.func_213656_en() ? AbstractIllagerEntity.ArmPose.CELEBRATING : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   public boolean isSpellcasting() {
      if (this.world.isRemote) {
         return (Byte)this.dataManager.get(SPELL) > 0;
      } else {
         return this.spellTicks > 0;
      }
   }

   public void setSpellType(SpellcastingIllagerEntity.SpellType p_193081_1_) {
      this.activeSpell = p_193081_1_;
      this.dataManager.set(SPELL, (byte)p_193081_1_.id);
   }

   protected SpellcastingIllagerEntity.SpellType getSpellType() {
      return !this.world.isRemote ? this.activeSpell : SpellcastingIllagerEntity.SpellType.getFromId((Byte)this.dataManager.get(SPELL));
   }

   protected void updateAITasks() {
      super.updateAITasks();
      if (this.spellTicks > 0) {
         --this.spellTicks;
      }

   }

   public void tick() {
      super.tick();
      if (this.world.isRemote && this.isSpellcasting()) {
         SpellcastingIllagerEntity.SpellType lvt_1_1_ = this.getSpellType();
         double lvt_2_1_ = lvt_1_1_.particleSpeed[0];
         double lvt_4_1_ = lvt_1_1_.particleSpeed[1];
         double lvt_6_1_ = lvt_1_1_.particleSpeed[2];
         float lvt_8_1_ = this.renderYawOffset * 0.017453292F + MathHelper.cos((float)this.ticksExisted * 0.6662F) * 0.25F;
         float lvt_9_1_ = MathHelper.cos(lvt_8_1_);
         float lvt_10_1_ = MathHelper.sin(lvt_8_1_);
         this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.func_226277_ct_() + (double)lvt_9_1_ * 0.6D, this.func_226278_cu_() + 1.8D, this.func_226281_cx_() + (double)lvt_10_1_ * 0.6D, lvt_2_1_, lvt_4_1_, lvt_6_1_);
         this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.func_226277_ct_() - (double)lvt_9_1_ * 0.6D, this.func_226278_cu_() + 1.8D, this.func_226281_cx_() - (double)lvt_10_1_ * 0.6D, lvt_2_1_, lvt_4_1_, lvt_6_1_);
      }

   }

   protected int getSpellTicks() {
      return this.spellTicks;
   }

   protected abstract SoundEvent getSpellSound();

   static {
      SPELL = EntityDataManager.createKey(SpellcastingIllagerEntity.class, DataSerializers.BYTE);
   }

   public static enum SpellType {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private final int id;
      private final double[] particleSpeed;

      private SpellType(int p_i47561_3_, double p_i47561_4_, double p_i47561_6_, double p_i47561_8_) {
         this.id = p_i47561_3_;
         this.particleSpeed = new double[]{p_i47561_4_, p_i47561_6_, p_i47561_8_};
      }

      public static SpellcastingIllagerEntity.SpellType getFromId(int p_193337_0_) {
         SpellcastingIllagerEntity.SpellType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            SpellcastingIllagerEntity.SpellType lvt_4_1_ = var1[var3];
            if (p_193337_0_ == lvt_4_1_.id) {
               return lvt_4_1_;
            }
         }

         return NONE;
      }
   }

   public abstract class UseSpellGoal extends Goal {
      protected int spellWarmup;
      protected int spellCooldown;

      protected UseSpellGoal() {
      }

      public boolean shouldExecute() {
         LivingEntity lvt_1_1_ = SpellcastingIllagerEntity.this.getAttackTarget();
         if (lvt_1_1_ != null && lvt_1_1_.isAlive()) {
            if (SpellcastingIllagerEntity.this.isSpellcasting()) {
               return false;
            } else {
               return SpellcastingIllagerEntity.this.ticksExisted >= this.spellCooldown;
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         LivingEntity lvt_1_1_ = SpellcastingIllagerEntity.this.getAttackTarget();
         return lvt_1_1_ != null && lvt_1_1_.isAlive() && this.spellWarmup > 0;
      }

      public void startExecuting() {
         this.spellWarmup = this.getCastWarmupTime();
         SpellcastingIllagerEntity.this.spellTicks = this.getCastingTime();
         this.spellCooldown = SpellcastingIllagerEntity.this.ticksExisted + this.getCastingInterval();
         SoundEvent lvt_1_1_ = this.getSpellPrepareSound();
         if (lvt_1_1_ != null) {
            SpellcastingIllagerEntity.this.playSound(lvt_1_1_, 1.0F, 1.0F);
         }

         SpellcastingIllagerEntity.this.setSpellType(this.getSpellType());
      }

      public void tick() {
         --this.spellWarmup;
         if (this.spellWarmup == 0) {
            this.castSpell();
            SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getSpellSound(), 1.0F, 1.0F);
         }

      }

      protected abstract void castSpell();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcastingIllagerEntity.SpellType getSpellType();
   }

   public class CastingASpellGoal extends Goal {
      public CastingASpellGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
      }

      public void startExecuting() {
         super.startExecuting();
         SpellcastingIllagerEntity.this.navigator.clearPath();
      }

      public void resetTask() {
         super.resetTask();
         SpellcastingIllagerEntity.this.setSpellType(SpellcastingIllagerEntity.SpellType.NONE);
      }

      public void tick() {
         if (SpellcastingIllagerEntity.this.getAttackTarget() != null) {
            SpellcastingIllagerEntity.this.getLookController().setLookPositionWithEntity(SpellcastingIllagerEntity.this.getAttackTarget(), (float)SpellcastingIllagerEntity.this.getHorizontalFaceSpeed(), (float)SpellcastingIllagerEntity.this.getVerticalFaceSpeed());
         }

      }
   }
}
