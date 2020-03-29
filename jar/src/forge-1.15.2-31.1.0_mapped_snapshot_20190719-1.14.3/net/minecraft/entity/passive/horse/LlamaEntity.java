package net.minecraft.entity.passive.horse;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LlamaEntity extends AbstractChestedHorseEntity implements IRangedAttackMob {
   private static final DataParameter<Integer> DATA_STRENGTH_ID;
   private static final DataParameter<Integer> DATA_COLOR_ID;
   private static final DataParameter<Integer> DATA_VARIANT_ID;
   private boolean didSpit;
   @Nullable
   private LlamaEntity caravanHead;
   @Nullable
   private LlamaEntity caravanTail;

   public LlamaEntity(EntityType<? extends LlamaEntity> p_i50237_1_, World p_i50237_2_) {
      super(p_i50237_1_, p_i50237_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_213800_eB() {
      return false;
   }

   private void setStrength(int p_190706_1_) {
      this.dataManager.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, p_190706_1_)));
   }

   private void setRandomStrength() {
      int lvt_1_1_ = this.rand.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + this.rand.nextInt(lvt_1_1_));
   }

   public int getStrength() {
      return (Integer)this.dataManager.get(DATA_STRENGTH_ID);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Variant", this.getVariant());
      p_213281_1_.putInt("Strength", this.getStrength());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         p_213281_1_.put("DecorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.setStrength(p_70037_1_.getInt("Strength"));
      super.readAdditional(p_70037_1_);
      this.setVariant(p_70037_1_.getInt("Variant"));
      if (p_70037_1_.contains("DecorItem", 10)) {
         this.horseChest.setInventorySlotContents(1, ItemStack.read(p_70037_1_.getCompound("DecorItem")));
      }

      this.updateHorseSlots();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.0999999046325684D));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25D, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new LlamaEntity.HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new LlamaEntity.DefendTargetGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_STRENGTH_ID, 0);
      this.dataManager.register(DATA_COLOR_ID, -1);
      this.dataManager.register(DATA_VARIANT_ID, 0);
   }

   public int getVariant() {
      return MathHelper.clamp((Integer)this.dataManager.get(DATA_VARIANT_ID), 0, 3);
   }

   public void setVariant(int p_190710_1_) {
      this.dataManager.set(DATA_VARIANT_ID, p_190710_1_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
   }

   public void updatePassenger(Entity p_184232_1_) {
      if (this.isPassenger(p_184232_1_)) {
         float lvt_2_1_ = MathHelper.cos(this.renderYawOffset * 0.017453292F);
         float lvt_3_1_ = MathHelper.sin(this.renderYawOffset * 0.017453292F);
         float lvt_4_1_ = 0.3F;
         p_184232_1_.setPosition(this.func_226277_ct_() + (double)(0.3F * lvt_3_1_), this.func_226278_cu_() + this.getMountedYOffset() + p_184232_1_.getYOffset(), this.func_226281_cx_() - (double)(0.3F * lvt_2_1_));
      }
   }

   public double getMountedYOffset() {
      return (double)this.getHeight() * 0.67D;
   }

   public boolean canBeSteered() {
      return false;
   }

   protected boolean handleEating(PlayerEntity p_190678_1_, ItemStack p_190678_2_) {
      int lvt_3_1_ = 0;
      int lvt_4_1_ = 0;
      float lvt_5_1_ = 0.0F;
      boolean lvt_6_1_ = false;
      Item lvt_7_1_ = p_190678_2_.getItem();
      if (lvt_7_1_ == Items.WHEAT) {
         lvt_3_1_ = 10;
         lvt_4_1_ = 3;
         lvt_5_1_ = 2.0F;
      } else if (lvt_7_1_ == Blocks.HAY_BLOCK.asItem()) {
         lvt_3_1_ = 90;
         lvt_4_1_ = 6;
         lvt_5_1_ = 10.0F;
         if (this.isTame() && this.getGrowingAge() == 0 && this.canBreed()) {
            lvt_6_1_ = true;
            this.setInLove(p_190678_1_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && lvt_5_1_ > 0.0F) {
         this.heal(lvt_5_1_);
         lvt_6_1_ = true;
      }

      if (this.isChild() && lvt_3_1_ > 0) {
         this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.addGrowth(lvt_3_1_);
         }

         lvt_6_1_ = true;
      }

      if (lvt_4_1_ > 0 && (lvt_6_1_ || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
         lvt_6_1_ = true;
         if (!this.world.isRemote) {
            this.increaseTemper(lvt_4_1_);
         }
      }

      if (lvt_6_1_ && !this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }

      return lvt_6_1_;
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.isEatingHaystack();
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setRandomStrength();
      int lvt_6_2_;
      if (p_213386_4_ instanceof LlamaEntity.LlamaData) {
         lvt_6_2_ = ((LlamaEntity.LlamaData)p_213386_4_).variant;
      } else {
         lvt_6_2_ = this.rand.nextInt(4);
         p_213386_4_ = new LlamaEntity.LlamaData(lvt_6_2_);
      }

      this.setVariant(lvt_6_2_);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_LLAMA_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public void makeMad() {
      SoundEvent lvt_1_1_ = this.getAngrySound();
      if (lvt_1_1_ != null) {
         this.playSound(lvt_1_1_, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public int getInventoryColumns() {
      return this.getStrength();
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack p_190682_1_) {
      Item lvt_2_1_ = p_190682_1_.getItem();
      return ItemTags.CARPETS.contains(lvt_2_1_);
   }

   public boolean canBeSaddled() {
      return false;
   }

   public void onInventoryChanged(IInventory p_76316_1_) {
      DyeColor lvt_2_1_ = this.getColor();
      super.onInventoryChanged(p_76316_1_);
      DyeColor lvt_3_1_ = this.getColor();
      if (this.ticksExisted > 20 && lvt_3_1_ != null && lvt_3_1_ != lvt_2_1_) {
         this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
      }

   }

   protected void updateHorseSlots() {
      if (!this.world.isRemote) {
         super.updateHorseSlots();
         this.setColor(getCarpetColor(this.horseChest.getStackInSlot(1)));
      }
   }

   private void setColor(@Nullable DyeColor p_190711_1_) {
      this.dataManager.set(DATA_COLOR_ID, p_190711_1_ == null ? -1 : p_190711_1_.getId());
   }

   @Nullable
   private static DyeColor getCarpetColor(ItemStack p_195403_0_) {
      Block lvt_1_1_ = Block.getBlockFromItem(p_195403_0_.getItem());
      return lvt_1_1_ instanceof CarpetBlock ? ((CarpetBlock)lvt_1_1_).getColor() : null;
   }

   @Nullable
   public DyeColor getColor() {
      int lvt_1_1_ = (Integer)this.dataManager.get(DATA_COLOR_ID);
      return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
   }

   public int getMaxTemper() {
      return 30;
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      return p_70878_1_ != this && p_70878_1_ instanceof LlamaEntity && this.canMate() && ((LlamaEntity)p_70878_1_).canMate();
   }

   public LlamaEntity createChild(AgeableEntity p_90011_1_) {
      LlamaEntity lvt_2_1_ = this.createChild();
      this.setOffspringAttributes(p_90011_1_, lvt_2_1_);
      LlamaEntity lvt_3_1_ = (LlamaEntity)p_90011_1_;
      int lvt_4_1_ = this.rand.nextInt(Math.max(this.getStrength(), lvt_3_1_.getStrength())) + 1;
      if (this.rand.nextFloat() < 0.03F) {
         ++lvt_4_1_;
      }

      lvt_2_1_.setStrength(lvt_4_1_);
      lvt_2_1_.setVariant(this.rand.nextBoolean() ? this.getVariant() : lvt_3_1_.getVariant());
      return lvt_2_1_;
   }

   protected LlamaEntity createChild() {
      return (LlamaEntity)EntityType.LLAMA.create(this.world);
   }

   private void spit(LivingEntity p_190713_1_) {
      LlamaSpitEntity lvt_2_1_ = new LlamaSpitEntity(this.world, this);
      double lvt_3_1_ = p_190713_1_.func_226277_ct_() - this.func_226277_ct_();
      double lvt_5_1_ = p_190713_1_.func_226283_e_(0.3333333333333333D) - lvt_2_1_.func_226278_cu_();
      double lvt_7_1_ = p_190713_1_.func_226281_cx_() - this.func_226281_cx_();
      float lvt_9_1_ = MathHelper.sqrt(lvt_3_1_ * lvt_3_1_ + lvt_7_1_ * lvt_7_1_) * 0.2F;
      lvt_2_1_.shoot(lvt_3_1_, lvt_5_1_ + (double)lvt_9_1_, lvt_7_1_, 1.5F, 10.0F);
      this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      this.world.addEntity(lvt_2_1_);
      this.didSpit = true;
   }

   private void setDidSpit(boolean p_190714_1_) {
      this.didSpit = p_190714_1_;
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      int lvt_3_1_ = this.func_225508_e_(p_225503_1_, p_225503_2_);
      if (lvt_3_1_ <= 0) {
         return false;
      } else {
         if (p_225503_1_ >= 6.0F) {
            this.attackEntityFrom(DamageSource.FALL, (float)lvt_3_1_);
            if (this.isBeingRidden()) {
               Iterator var4 = this.getRecursivePassengers().iterator();

               while(var4.hasNext()) {
                  Entity lvt_5_1_ = (Entity)var4.next();
                  lvt_5_1_.attackEntityFrom(DamageSource.FALL, (float)lvt_3_1_);
               }
            }
         }

         this.func_226295_cZ_();
         return true;
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(LlamaEntity p_190715_1_) {
      this.caravanHead = p_190715_1_;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public LlamaEntity getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   protected void followMother() {
      if (!this.inCaravan() && this.isChild()) {
         super.followMother();
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      this.spit(p_82196_1_);
   }

   // $FF: synthetic method
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return this.createChild(p_90011_1_);
   }

   static {
      DATA_STRENGTH_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
      DATA_COLOR_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
      DATA_VARIANT_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
   }

   static class DefendTargetGoal extends NearestAttackableTargetGoal<WolfEntity> {
      public DefendTargetGoal(LlamaEntity p_i47285_1_) {
         super(p_i47285_1_, WolfEntity.class, 16, false, true, (p_220789_0_) -> {
            return !((WolfEntity)p_220789_0_).isTamed();
         });
      }

      protected double getTargetDistance() {
         return super.getTargetDistance() * 0.25D;
      }
   }

   static class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
      public HurtByTargetGoal(LlamaEntity p_i47282_1_) {
         super(p_i47282_1_);
      }

      public boolean shouldContinueExecuting() {
         if (this.goalOwner instanceof LlamaEntity) {
            LlamaEntity lvt_1_1_ = (LlamaEntity)this.goalOwner;
            if (lvt_1_1_.didSpit) {
               lvt_1_1_.setDidSpit(false);
               return false;
            }
         }

         return super.shouldContinueExecuting();
      }
   }

   static class LlamaData extends AgeableEntity.AgeableData {
      public final int variant;

      private LlamaData(int p_i47283_1_) {
         this.variant = p_i47283_1_;
      }

      // $FF: synthetic method
      LlamaData(int p_i47284_1_, Object p_i47284_2_) {
         this(p_i47284_1_);
      }
   }
}
