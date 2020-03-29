package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.DefendVillageTargetGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.MoveTowardsVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ShowVillagerFlowerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IronGolemEntity extends GolemEntity {
   protected static final DataParameter<Byte> PLAYER_CREATED;
   private int attackTimer;
   private int holdRoseTick;

   public IronGolemEntity(EntityType<? extends IronGolemEntity> p_i50267_1_, World p_i50267_2_) {
      super(p_i50267_1_, p_i50267_2_);
      this.stepHeight = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
      this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> {
         return false;
      }));
      this.goalSelector.addGoal(5, new ShowVillagerFlowerGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, MobEntity.class, 5, false, false, (p_lambda$registerGoals$1_0_) -> {
         return p_lambda$registerGoals$1_0_ instanceof IMob && !(p_lambda$registerGoals$1_0_ instanceof CreeperEntity);
      }));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PLAYER_CREATED, (byte)0);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D);
   }

   protected int decreaseAirSupply(int p_70682_1_) {
      return p_70682_1_;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
      if (p_82167_1_ instanceof IMob && !(p_82167_1_ instanceof CreeperEntity) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((LivingEntity)p_82167_1_);
      }

      super.collideWithEntity(p_82167_1_);
   }

   public void livingTick() {
      super.livingTick();
      if (this.attackTimer > 0) {
         --this.attackTimer;
      }

      if (this.holdRoseTick > 0) {
         --this.holdRoseTick;
      }

      if (func_213296_b(this.getMotion()) > 2.500000277905201E-7D && this.rand.nextInt(5) == 0) {
         int i = MathHelper.floor(this.func_226277_ct_());
         int j = MathHelper.floor(this.func_226278_cu_() - 0.20000000298023224D);
         int k = MathHelper.floor(this.func_226281_cx_());
         BlockPos pos = new BlockPos(i, j, k);
         BlockState blockstate = this.world.getBlockState(pos);
         if (!blockstate.isAir(this.world, pos)) {
            this.world.addParticle((new BlockParticleData(ParticleTypes.BLOCK, blockstate)).setPos(pos), this.func_226277_ct_() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), this.func_226278_cu_() + 0.1D, this.func_226281_cx_() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean canAttack(EntityType<?> p_213358_1_) {
      if (this.isPlayerCreated() && p_213358_1_ == EntityType.PLAYER) {
         return false;
      } else {
         return p_213358_1_ == EntityType.CREEPER ? false : super.canAttack(p_213358_1_);
      }
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setPlayerCreated(p_70037_1_.getBoolean("PlayerCreated"));
   }

   private float func_226511_et_() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      this.attackTimer = 10;
      this.world.setEntityState(this, (byte)4);
      float f = this.func_226511_et_();
      float f1 = f > 0.0F ? f / 2.0F + (float)this.rand.nextInt((int)f) : 0.0F;
      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f1);
      if (flag) {
         p_70652_1_.setMotion(p_70652_1_.getMotion().add(0.0D, 0.4000000059604645D, 0.0D));
         this.applyEnchantments(this, p_70652_1_);
      }

      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return flag;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      IronGolemEntity.Cracks irongolementity$cracks = this.func_226512_l_();
      boolean flag = super.attackEntityFrom(p_70097_1_, p_70097_2_);
      if (flag && this.func_226512_l_() != irongolementity$cracks) {
         this.playSound(SoundEvents.field_226142_fM_, 1.0F, 1.0F);
      }

      return flag;
   }

   public IronGolemEntity.Cracks func_226512_l_() {
      return IronGolemEntity.Cracks.func_226515_a_(this.getHealth() / this.getMaxHealth());
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackTimer = 10;
         this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (p_70103_1_ == 11) {
         this.holdRoseTick = 400;
      } else if (p_70103_1_ == 34) {
         this.holdRoseTick = 0;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean p_70851_1_) {
      if (p_70851_1_) {
         this.holdRoseTick = 400;
         this.world.setEntityState(this, (byte)11);
      } else {
         this.holdRoseTick = 0;
         this.world.setEntityState(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      Item item = itemstack.getItem();
      if (item != Items.IRON_INGOT) {
         return false;
      } else {
         float f = this.getHealth();
         this.heal(25.0F);
         if (this.getHealth() == f) {
            return false;
         } else {
            float f1 = 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.field_226143_fP_, 1.0F, f1);
            if (!p_184645_1_.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }

            return true;
         }
      }
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return ((Byte)this.dataManager.get(PLAYER_CREATED) & 1) != 0;
   }

   public void setPlayerCreated(boolean p_70849_1_) {
      byte b0 = (Byte)this.dataManager.get(PLAYER_CREATED);
      if (p_70849_1_) {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 | 1));
      } else {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 & -2));
      }

   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.down();
      BlockState blockstate = p_205019_1_.getBlockState(blockpos1);
      if (!blockstate.func_215682_a(p_205019_1_, blockpos1, this)) {
         return false;
      } else {
         for(int i = 1; i < 3; ++i) {
            BlockPos blockpos2 = blockpos.up(i);
            BlockState blockstate1 = p_205019_1_.getBlockState(blockpos2);
            if (!WorldEntitySpawner.isSpawnableSpace(p_205019_1_, blockpos2, blockstate1, blockstate1.getFluidState())) {
               return false;
            }
         }

         return WorldEntitySpawner.isSpawnableSpace(p_205019_1_, blockpos, p_205019_1_.getBlockState(blockpos), Fluids.EMPTY.getDefaultState()) && p_205019_1_.func_226668_i_(this);
      }
   }

   static {
      PLAYER_CREATED = EntityDataManager.createKey(IronGolemEntity.class, DataSerializers.BYTE);
   }

   public static enum Cracks {
      NONE(1.0F),
      LOW(0.75F),
      MEDIUM(0.5F),
      HIGH(0.25F);

      private static final List<IronGolemEntity.Cracks> field_226513_e_ = (List)Stream.of(values()).sorted(Comparator.comparingDouble((p_lambda$static$0_0_) -> {
         return (double)p_lambda$static$0_0_.field_226514_f_;
      })).collect(ImmutableList.toImmutableList());
      private final float field_226514_f_;

      private Cracks(float p_i225732_3_) {
         this.field_226514_f_ = p_i225732_3_;
      }

      public static IronGolemEntity.Cracks func_226515_a_(float p_226515_0_) {
         Iterator var1 = field_226513_e_.iterator();

         IronGolemEntity.Cracks irongolementity$cracks;
         do {
            if (!var1.hasNext()) {
               return NONE;
            }

            irongolementity$cracks = (IronGolemEntity.Cracks)var1.next();
         } while(p_226515_0_ >= irongolementity$cracks.field_226514_f_);

         return irongolementity$cracks;
      }
   }
}
