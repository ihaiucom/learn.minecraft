package net.minecraft.entity.passive;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

public class SnowGolemEntity extends GolemEntity implements IRangedAttackMob, IShearable {
   private static final DataParameter<Byte> PUMPKIN_EQUIPPED;

   public SnowGolemEntity(EntityType<? extends SnowGolemEntity> p_i50244_1_, World p_i50244_2_) {
      super(p_i50244_1_, p_i50244_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, MobEntity.class, 10, true, false, (p_lambda$registerGoals$0_0_) -> {
         return p_lambda$registerGoals$0_0_ instanceof IMob;
      }));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUMPKIN_EQUIPPED, (byte)16);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("Pumpkin", this.isPumpkinEquipped());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("Pumpkin")) {
         this.setPumpkinEquipped(p_70037_1_.getBoolean("Pumpkin"));
      }

   }

   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote) {
         int i = MathHelper.floor(this.func_226277_ct_());
         int j = MathHelper.floor(this.func_226278_cu_());
         int k = MathHelper.floor(this.func_226281_cx_());
         if (this.isInWaterRainOrBubbleColumn()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
         }

         if (this.world.func_226691_t_(new BlockPos(i, 0, k)).func_225486_c(new BlockPos(i, j, k)) > 1.0F) {
            this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
         }

         if (!ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
            return;
         }

         BlockState blockstate = Blocks.SNOW.getDefaultState();

         for(int l = 0; l < 4; ++l) {
            i = MathHelper.floor(this.func_226277_ct_() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            j = MathHelper.floor(this.func_226278_cu_());
            k = MathHelper.floor(this.func_226281_cx_() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos = new BlockPos(i, j, k);
            if (this.world.isAirBlock(blockpos) && this.world.func_226691_t_(blockpos).func_225486_c(blockpos) < 0.8F && blockstate.isValidPosition(this.world, blockpos)) {
               this.world.setBlockState(blockpos, blockstate);
            }
         }
      }

   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      SnowballEntity snowballentity = new SnowballEntity(this.world, this);
      double d0 = p_82196_1_.func_226280_cw_() - 1.100000023841858D;
      double d1 = p_82196_1_.func_226277_ct_() - this.func_226277_ct_();
      double d2 = d0 - snowballentity.func_226278_cu_();
      double d3 = p_82196_1_.func_226281_cx_() - this.func_226281_cx_();
      float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      snowballentity.shoot(d1, d2 + (double)f, d3, 1.6F, 12.0F);
      this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(snowballentity);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 1.7F;
   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      p_184645_1_.getHeldItem(p_184645_2_);
      return false;
   }

   public boolean isPumpkinEquipped() {
      return ((Byte)this.dataManager.get(PUMPKIN_EQUIPPED) & 16) != 0;
   }

   public void setPumpkinEquipped(boolean p_184747_1_) {
      byte b0 = (Byte)this.dataManager.get(PUMPKIN_EQUIPPED);
      if (p_184747_1_) {
         this.dataManager.set(PUMPKIN_EQUIPPED, (byte)(b0 | 16));
      } else {
         this.dataManager.set(PUMPKIN_EQUIPPED, (byte)(b0 & -17));
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
   }

   public boolean isShearable(ItemStack p_isShearable_1_, IWorldReader p_isShearable_2_, BlockPos p_isShearable_3_) {
      return this.isPumpkinEquipped();
   }

   public List<ItemStack> onSheared(ItemStack p_onSheared_1_, IWorld p_onSheared_2_, BlockPos p_onSheared_3_, int p_onSheared_4_) {
      this.setPumpkinEquipped(false);
      return new ArrayList();
   }

   // $FF: synthetic method
   private static void lambda$processInteract$1(Hand p_lambda$processInteract$1_0_, PlayerEntity p_lambda$processInteract$1_1_) {
      p_lambda$processInteract$1_1_.sendBreakAnimation(p_lambda$processInteract$1_0_);
   }

   static {
      PUMPKIN_EQUIPPED = EntityDataManager.createKey(SnowGolemEntity.class, DataSerializers.BYTE);
   }
}
