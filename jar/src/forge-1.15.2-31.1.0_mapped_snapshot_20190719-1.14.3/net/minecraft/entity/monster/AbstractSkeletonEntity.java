package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractSkeletonEntity extends MonsterEntity implements IRangedAttackMob {
   private final RangedBowAttackGoal<AbstractSkeletonEntity> aiArrowAttack = new RangedBowAttackGoal(this, 1.0D, 20, 15.0F);
   private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
      public void resetTask() {
         super.resetTask();
         AbstractSkeletonEntity.this.setAggroed(false);
      }

      public void startExecuting() {
         super.startExecuting();
         AbstractSkeletonEntity.this.setAggroed(true);
      }
   };

   protected AbstractSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> p_i48555_1_, World p_i48555_2_) {
      super(p_i48555_1_, p_i48555_2_);
      this.setCombatTask();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RestrictSunGoal(this));
      this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, WolfEntity.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected abstract SoundEvent getStepSound();

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   public void livingTick() {
      boolean flag = this.isInDaylight();
      if (flag) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
         if (!itemstack.isEmpty()) {
            if (itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
               if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
                  this.sendBreakAnimation(EquipmentSlotType.HEAD);
                  this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
               }
            }

            flag = false;
         }

         if (flag) {
            this.setFire(8);
         }
      }

      super.livingTick();
   }

   public void updateRidden() {
      super.updateRidden();
      if (this.getRidingEntity() instanceof CreatureEntity) {
         CreatureEntity creatureentity = (CreatureEntity)this.getRidingEntity();
         this.renderYawOffset = creatureentity.renderYawOffset;
      }

   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      super.setEquipmentBasedOnDifficulty(p_180481_1_);
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      this.setEquipmentBasedOnDifficulty(p_213386_2_);
      this.setEnchantmentBasedOnDifficulty(p_213386_2_);
      this.setCombatTask();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * p_213386_2_.getClampedAdditionalDifficulty());
      if (this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F) {
            this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.inventoryArmorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
         }
      }

      return p_213386_4_;
   }

   public void setCombatTask() {
      if (this.world != null && !this.world.isRemote) {
         this.goalSelector.removeGoal(this.aiAttackOnCollide);
         this.goalSelector.removeGoal(this.aiArrowAttack);
         ItemStack itemstack = this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW));
         if (itemstack.getItem() instanceof BowItem) {
            int i = 20;
            if (this.world.getDifficulty() != Difficulty.HARD) {
               i = 40;
            }

            this.aiArrowAttack.setAttackCooldown(i);
            this.goalSelector.addGoal(4, this.aiArrowAttack);
         } else {
            this.goalSelector.addGoal(4, this.aiAttackOnCollide);
         }
      }

   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
      AbstractArrowEntity abstractarrowentity = this.func_213624_b(itemstack, p_82196_2_);
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

   protected AbstractArrowEntity func_213624_b(ItemStack p_213624_1_, float p_213624_2_) {
      return ProjectileHelper.func_221272_a(this, p_213624_1_, p_213624_2_);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setCombatTask();
   }

   public void setItemStackToSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      super.setItemStackToSlot(p_184201_1_, p_184201_2_);
      if (!this.world.isRemote) {
         this.setCombatTask();
      }

   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 1.74F;
   }

   public double getYOffset() {
      return -0.6D;
   }
}
