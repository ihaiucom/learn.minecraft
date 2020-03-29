package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetExpiringGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.ToggleableNearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitchEntity extends AbstractRaiderEntity implements IRangedAttackMob {
   private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier MODIFIER;
   private static final DataParameter<Boolean> IS_DRINKING;
   private int potionUseTimer;
   private NearestAttackableTargetExpiringGoal<AbstractRaiderEntity> field_213694_bC;
   private ToggleableNearestAttackableTargetGoal<PlayerEntity> field_213695_bD;

   public WitchEntity(EntityType<? extends WitchEntity> p_i50188_1_, World p_i50188_2_) {
      super(p_i50188_1_, p_i50188_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.field_213694_bC = new NearestAttackableTargetExpiringGoal(this, AbstractRaiderEntity.class, true, (p_213693_1_) -> {
         return p_213693_1_ != null && this.isRaidActive() && p_213693_1_.getType() != EntityType.WITCH;
      });
      this.field_213695_bD = new ToggleableNearestAttackableTargetGoal(this, PlayerEntity.class, 10, true, false, (Predicate)null);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class}));
      this.targetSelector.addGoal(2, this.field_213694_bC);
      this.targetSelector.addGoal(3, this.field_213695_bD);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(IS_DRINKING, false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITCH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITCH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITCH_DEATH;
   }

   public void setDrinkingPotion(boolean p_82197_1_) {
      this.getDataManager().set(IS_DRINKING, p_82197_1_);
   }

   public boolean isDrinkingPotion() {
      return (Boolean)this.getDataManager().get(IS_DRINKING);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public void livingTick() {
      if (!this.world.isRemote && this.isAlive()) {
         this.field_213694_bC.func_220780_j();
         if (this.field_213694_bC.func_220781_h() <= 0) {
            this.field_213695_bD.func_220783_a(true);
         } else {
            this.field_213695_bD.func_220783_a(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.potionUseTimer-- <= 0) {
               this.setDrinkingPotion(false);
               ItemStack lvt_1_1_ = this.getHeldItemMainhand();
               this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               if (lvt_1_1_.getItem() == Items.POTION) {
                  List<EffectInstance> lvt_2_1_ = PotionUtils.getEffectsFromStack(lvt_1_1_);
                  if (lvt_2_1_ != null) {
                     Iterator var3 = lvt_2_1_.iterator();

                     while(var3.hasNext()) {
                        EffectInstance lvt_4_1_ = (EffectInstance)var3.next();
                        this.addPotionEffect(new EffectInstance(lvt_4_1_));
                     }
                  }
               }

               this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
            }
         } else {
            Potion lvt_1_2_ = null;
            if (this.rand.nextFloat() < 0.15F && this.areEyesInFluid(FluidTags.WATER) && !this.isPotionActive(Effects.WATER_BREATHING)) {
               lvt_1_2_ = Potions.WATER_BREATHING;
            } else if (this.rand.nextFloat() < 0.15F && (this.isBurning() || this.getLastDamageSource() != null && this.getLastDamageSource().isFireDamage()) && !this.isPotionActive(Effects.FIRE_RESISTANCE)) {
               lvt_1_2_ = Potions.FIRE_RESISTANCE;
            } else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               lvt_1_2_ = Potions.HEALING;
            } else if (this.rand.nextFloat() < 0.5F && this.getAttackTarget() != null && !this.isPotionActive(Effects.SPEED) && this.getAttackTarget().getDistanceSq(this) > 121.0D) {
               lvt_1_2_ = Potions.SWIFTNESS;
            }

            if (lvt_1_2_ != null) {
               this.setItemStackToSlot(EquipmentSlotType.MAINHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), lvt_1_2_));
               this.potionUseTimer = this.getHeldItemMainhand().getUseDuration();
               this.setDrinkingPotion(true);
               this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
               IAttributeInstance lvt_2_2_ = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
               lvt_2_2_.removeModifier(MODIFIER);
               lvt_2_2_.applyModifier(MODIFIER);
            }
         }

         if (this.rand.nextFloat() < 7.5E-4F) {
            this.world.setEntityState(this, (byte)15);
         }
      }

      super.livingTick();
   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_WITCH_CELEBRATE;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 15) {
         for(int lvt_2_1_ = 0; lvt_2_1_ < this.rand.nextInt(35) + 10; ++lvt_2_1_) {
            this.world.addParticle(ParticleTypes.WITCH, this.func_226277_ct_() + this.rand.nextGaussian() * 0.12999999523162842D, this.getBoundingBox().maxY + 0.5D + this.rand.nextGaussian() * 0.12999999523162842D, this.func_226281_cx_() + this.rand.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   protected float applyPotionDamageCalculations(DamageSource p_70672_1_, float p_70672_2_) {
      p_70672_2_ = super.applyPotionDamageCalculations(p_70672_1_, p_70672_2_);
      if (p_70672_1_.getTrueSource() == this) {
         p_70672_2_ = 0.0F;
      }

      if (p_70672_1_.isMagicDamage()) {
         p_70672_2_ = (float)((double)p_70672_2_ * 0.15D);
      }

      return p_70672_2_;
   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      if (!this.isDrinkingPotion()) {
         Vec3d lvt_3_1_ = p_82196_1_.getMotion();
         double lvt_4_1_ = p_82196_1_.func_226277_ct_() + lvt_3_1_.x - this.func_226277_ct_();
         double lvt_6_1_ = p_82196_1_.func_226280_cw_() - 1.100000023841858D - this.func_226278_cu_();
         double lvt_8_1_ = p_82196_1_.func_226281_cx_() + lvt_3_1_.z - this.func_226281_cx_();
         float lvt_10_1_ = MathHelper.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_8_1_ * lvt_8_1_);
         Potion lvt_11_1_ = Potions.HARMING;
         if (p_82196_1_ instanceof AbstractRaiderEntity) {
            if (p_82196_1_.getHealth() <= 4.0F) {
               lvt_11_1_ = Potions.HEALING;
            } else {
               lvt_11_1_ = Potions.REGENERATION;
            }

            this.setAttackTarget((LivingEntity)null);
         } else if (lvt_10_1_ >= 8.0F && !p_82196_1_.isPotionActive(Effects.SLOWNESS)) {
            lvt_11_1_ = Potions.SLOWNESS;
         } else if (p_82196_1_.getHealth() >= 8.0F && !p_82196_1_.isPotionActive(Effects.POISON)) {
            lvt_11_1_ = Potions.POISON;
         } else if (lvt_10_1_ <= 3.0F && !p_82196_1_.isPotionActive(Effects.WEAKNESS) && this.rand.nextFloat() < 0.25F) {
            lvt_11_1_ = Potions.WEAKNESS;
         }

         PotionEntity lvt_12_1_ = new PotionEntity(this.world, this);
         lvt_12_1_.setItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), lvt_11_1_));
         lvt_12_1_.rotationPitch -= -20.0F;
         lvt_12_1_.shoot(lvt_4_1_, lvt_6_1_ + (double)(lvt_10_1_ * 0.2F), lvt_8_1_, 0.75F, 8.0F);
         this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
         this.world.addEntity(lvt_12_1_);
      }
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 1.62F;
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
   }

   public boolean canBeLeader() {
      return false;
   }

   static {
      MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).setSaved(false);
      IS_DRINKING = EntityDataManager.createKey(WitchEntity.class, DataSerializers.BOOLEAN);
   }
}
