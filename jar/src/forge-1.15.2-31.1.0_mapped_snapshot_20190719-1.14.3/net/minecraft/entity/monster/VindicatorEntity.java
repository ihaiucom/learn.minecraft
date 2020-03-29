package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VindicatorEntity extends AbstractIllagerEntity {
   private static final Predicate<Difficulty> field_213681_b = (p_213678_0_) -> {
      return p_213678_0_ == Difficulty.NORMAL || p_213678_0_ == Difficulty.HARD;
   };
   private boolean johnny;

   public VindicatorEntity(EntityType<? extends VindicatorEntity> p_i50189_1_, World p_i50189_2_) {
      super(p_i50189_1_, p_i50189_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new VindicatorEntity.BreakDoorGoal(this));
      this.goalSelector.addGoal(2, new AbstractIllagerEntity.RaidOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new VindicatorEntity.AttackGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(4, new VindicatorEntity.JohnnyAttackGoal(this));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
   }

   protected void updateAITasks() {
      if (!this.isAIDisabled()) {
         PathNavigator lvt_1_1_ = this.getNavigator();
         if (lvt_1_1_ instanceof GroundPathNavigator) {
            boolean lvt_2_1_ = ((ServerWorld)this.world).hasRaid(new BlockPos(this));
            ((GroundPathNavigator)lvt_1_1_).setBreakDoors(lvt_2_1_);
         }
      }

      super.updateAITasks();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.johnny) {
         p_213281_1_.putBoolean("Johnny", true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isAggressive()) {
         return AbstractIllagerEntity.ArmPose.ATTACKING;
      } else {
         return this.func_213656_en() ? AbstractIllagerEntity.ArmPose.CELEBRATING : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("Johnny", 99)) {
         this.johnny = p_70037_1_.getBoolean("Johnny");
      }

   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      ILivingEntityData lvt_6_1_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
      this.setEquipmentBasedOnDifficulty(p_213386_2_);
      this.setEnchantmentBasedOnDifficulty(p_213386_2_);
      return lvt_6_1_;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      if (this.getRaid() == null) {
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_AXE));
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

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      super.setCustomName(p_200203_1_);
      if (!this.johnny && p_200203_1_ != null && p_200203_1_.getString().equals("Johnny")) {
         this.johnny = true;
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_VINDICATOR_HURT;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
      ItemStack lvt_3_1_ = new ItemStack(Items.IRON_AXE);
      Raid lvt_4_1_ = this.getRaid();
      int lvt_5_1_ = 1;
      if (p_213660_1_ > lvt_4_1_.getWaves(Difficulty.NORMAL)) {
         lvt_5_1_ = 2;
      }

      boolean lvt_6_1_ = this.rand.nextFloat() <= lvt_4_1_.func_221308_w();
      if (lvt_6_1_) {
         Map<Enchantment, Integer> lvt_7_1_ = Maps.newHashMap();
         lvt_7_1_.put(Enchantments.SHARPNESS, Integer.valueOf(lvt_5_1_));
         EnchantmentHelper.setEnchantments(lvt_7_1_, lvt_3_1_);
      }

      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, lvt_3_1_);
   }

   static class JohnnyAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public JohnnyAttackGoal(VindicatorEntity p_i47345_1_) {
         super(p_i47345_1_, LivingEntity.class, 0, true, true, LivingEntity::attackable);
      }

      public boolean shouldExecute() {
         return ((VindicatorEntity)this.goalOwner).johnny && super.shouldExecute();
      }

      public void startExecuting() {
         super.startExecuting();
         this.goalOwner.setIdleTime(0);
      }
   }

   static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
      public BreakDoorGoal(MobEntity p_i50578_1_) {
         super(p_i50578_1_, 6, VindicatorEntity.field_213681_b);
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldContinueExecuting() {
         VindicatorEntity lvt_1_1_ = (VindicatorEntity)this.entity;
         return lvt_1_1_.isRaidActive() && super.shouldContinueExecuting();
      }

      public boolean shouldExecute() {
         VindicatorEntity lvt_1_1_ = (VindicatorEntity)this.entity;
         return lvt_1_1_.isRaidActive() && lvt_1_1_.rand.nextInt(10) == 0 && super.shouldExecute();
      }

      public void startExecuting() {
         super.startExecuting();
         this.entity.setIdleTime(0);
      }
   }

   class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(VindicatorEntity p_i50577_2_) {
         super(p_i50577_2_, 1.0D, false);
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         if (this.attacker.getRidingEntity() instanceof RavagerEntity) {
            float lvt_2_1_ = this.attacker.getRidingEntity().getWidth() - 0.1F;
            return (double)(lvt_2_1_ * 2.0F * lvt_2_1_ * 2.0F + p_179512_1_.getWidth());
         } else {
            return super.getAttackReachSqr(p_179512_1_);
         }
      }
   }
}
