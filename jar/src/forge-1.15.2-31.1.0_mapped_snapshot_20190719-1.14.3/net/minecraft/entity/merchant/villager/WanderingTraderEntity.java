package net.minecraft.entity.merchant.villager;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookAtWithoutMovingGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.entity.ai.goal.UseItemGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WanderingTraderEntity extends AbstractVillagerEntity {
   @Nullable
   private BlockPos wanderTarget;
   private int despawnDelay;

   public WanderingTraderEntity(EntityType<? extends WanderingTraderEntity> p_i50178_1_, World p_i50178_2_) {
      super(p_i50178_1_, p_i50178_2_);
      this.forceSpawn = true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(0, new UseItemGoal(this, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEvents.ENTITY_WANDERING_TRADER_DISAPPEARED, (p_213733_1_) -> {
         return !this.world.isDaytime() && !p_213733_1_.isInvisible();
      }));
      this.goalSelector.addGoal(0, new UseItemGoal(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED, (p_213736_1_) -> {
         return this.world.isDaytime() && p_213736_1_.isInvisible();
      }));
      this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, ZombieEntity.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, EvokerEntity.class, 12.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, VexEntity.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, PillagerEntity.class, 15.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new PanicGoal(this, 0.5D));
      this.goalSelector.addGoal(1, new LookAtCustomerGoal(this));
      this.goalSelector.addGoal(2, new WanderingTraderEntity.MoveToGoal(this, 2.0D, 0.35D));
      this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 0.35D));
      this.goalSelector.addGoal(9, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      return null;
   }

   public boolean func_213705_dZ() {
      return false;
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      boolean lvt_4_1_ = lvt_3_1_.getItem() == Items.NAME_TAG;
      if (lvt_4_1_) {
         lvt_3_1_.interactWithEntity(p_184645_1_, this, p_184645_2_);
         return true;
      } else if (lvt_3_1_.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.func_213716_dX() && !this.isChild()) {
         if (p_184645_2_ == Hand.MAIN_HAND) {
            p_184645_1_.addStat(Stats.TALKED_TO_VILLAGER);
         }

         if (this.getOffers().isEmpty()) {
            return super.processInteract(p_184645_1_, p_184645_2_);
         } else {
            if (!this.world.isRemote) {
               this.setCustomer(p_184645_1_);
               this.func_213707_a(p_184645_1_, this.getDisplayName(), 1);
            }

            return true;
         }
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   protected void populateTradeData() {
      VillagerTrades.ITrade[] lvt_1_1_ = (VillagerTrades.ITrade[])VillagerTrades.field_221240_b.get(1);
      VillagerTrades.ITrade[] lvt_2_1_ = (VillagerTrades.ITrade[])VillagerTrades.field_221240_b.get(2);
      if (lvt_1_1_ != null && lvt_2_1_ != null) {
         MerchantOffers lvt_3_1_ = this.getOffers();
         this.addTrades(lvt_3_1_, lvt_1_1_, 5);
         int lvt_4_1_ = this.rand.nextInt(lvt_2_1_.length);
         VillagerTrades.ITrade lvt_5_1_ = lvt_2_1_[lvt_4_1_];
         MerchantOffer lvt_6_1_ = lvt_5_1_.getOffer(this, this.rand);
         if (lvt_6_1_ != null) {
            lvt_3_1_.add(lvt_6_1_);
         }

      }
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("DespawnDelay", this.despawnDelay);
      if (this.wanderTarget != null) {
         p_213281_1_.put("WanderTarget", NBTUtil.writeBlockPos(this.wanderTarget));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("DespawnDelay", 99)) {
         this.despawnDelay = p_70037_1_.getInt("DespawnDelay");
      }

      if (p_70037_1_.contains("WanderTarget")) {
         this.wanderTarget = NBTUtil.readBlockPos(p_70037_1_.getCompound("WanderTarget"));
      }

      this.setGrowingAge(Math.max(0, this.getGrowingAge()));
   }

   public boolean canDespawn(double p_213397_1_) {
      return false;
   }

   protected void func_213713_b(MerchantOffer p_213713_1_) {
      if (p_213713_1_.func_222221_q()) {
         int lvt_2_1_ = 3 + this.rand.nextInt(4);
         this.world.addEntity(new ExperienceOrbEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_() + 0.5D, this.func_226281_cx_(), lvt_2_1_));
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.func_213716_dX() ? SoundEvents.ENTITY_WANDERING_TRADER_TRADE : SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WANDERING_TRADER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WANDERING_TRADER_DEATH;
   }

   protected SoundEvent getDrinkSound(ItemStack p_213351_1_) {
      Item lvt_2_1_ = p_213351_1_.getItem();
      return lvt_2_1_ == Items.MILK_BUCKET ? SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK : SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION;
   }

   protected SoundEvent func_213721_r(boolean p_213721_1_) {
      return p_213721_1_ ? SoundEvents.ENTITY_WANDERING_TRADER_YES : SoundEvents.ENTITY_WANDERING_TRADER_NO;
   }

   public SoundEvent func_213714_ea() {
      return SoundEvents.ENTITY_WANDERING_TRADER_YES;
   }

   public void func_213728_s(int p_213728_1_) {
      this.despawnDelay = p_213728_1_;
   }

   public int func_213735_eg() {
      return this.despawnDelay;
   }

   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote) {
         this.func_222821_eh();
      }

   }

   private void func_222821_eh() {
      if (this.despawnDelay > 0 && !this.func_213716_dX() && --this.despawnDelay == 0) {
         this.remove();
      }

   }

   public void func_213726_g(@Nullable BlockPos p_213726_1_) {
      this.wanderTarget = p_213726_1_;
   }

   @Nullable
   private BlockPos func_213727_eh() {
      return this.wanderTarget;
   }

   class MoveToGoal extends Goal {
      final WanderingTraderEntity field_220847_a;
      final double field_220848_b;
      final double field_220849_c;

      MoveToGoal(WanderingTraderEntity p_i50459_2_, double p_i50459_3_, double p_i50459_5_) {
         this.field_220847_a = p_i50459_2_;
         this.field_220848_b = p_i50459_3_;
         this.field_220849_c = p_i50459_5_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public void resetTask() {
         this.field_220847_a.func_213726_g((BlockPos)null);
         WanderingTraderEntity.this.navigator.clearPath();
      }

      public boolean shouldExecute() {
         BlockPos lvt_1_1_ = this.field_220847_a.func_213727_eh();
         return lvt_1_1_ != null && this.func_220846_a(lvt_1_1_, this.field_220848_b);
      }

      public void tick() {
         BlockPos lvt_1_1_ = this.field_220847_a.func_213727_eh();
         if (lvt_1_1_ != null && WanderingTraderEntity.this.navigator.noPath()) {
            if (this.func_220846_a(lvt_1_1_, 10.0D)) {
               Vec3d lvt_2_1_ = (new Vec3d((double)lvt_1_1_.getX() - this.field_220847_a.func_226277_ct_(), (double)lvt_1_1_.getY() - this.field_220847_a.func_226278_cu_(), (double)lvt_1_1_.getZ() - this.field_220847_a.func_226281_cx_())).normalize();
               Vec3d lvt_3_1_ = lvt_2_1_.scale(10.0D).add(this.field_220847_a.func_226277_ct_(), this.field_220847_a.func_226278_cu_(), this.field_220847_a.func_226281_cx_());
               WanderingTraderEntity.this.navigator.tryMoveToXYZ(lvt_3_1_.x, lvt_3_1_.y, lvt_3_1_.z, this.field_220849_c);
            } else {
               WanderingTraderEntity.this.navigator.tryMoveToXYZ((double)lvt_1_1_.getX(), (double)lvt_1_1_.getY(), (double)lvt_1_1_.getZ(), this.field_220849_c);
            }
         }

      }

      private boolean func_220846_a(BlockPos p_220846_1_, double p_220846_2_) {
         return !p_220846_1_.withinDistance(this.field_220847_a.getPositionVec(), p_220846_2_);
      }
   }
}
