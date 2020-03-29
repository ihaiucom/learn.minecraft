package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AgeableEntity extends CreatureEntity {
   private static final DataParameter<Boolean> BABY;
   protected int growingAge;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgeableEntity(EntityType<? extends AgeableEntity> p_i48581_1_, World p_i48581_2_) {
      super(p_i48581_1_, p_i48581_2_);
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
      }

      AgeableEntity.AgeableData lvt_6_1_ = (AgeableEntity.AgeableData)p_213386_4_;
      if (lvt_6_1_.func_226261_c_() && lvt_6_1_.func_226257_a_() > 0 && this.rand.nextFloat() <= lvt_6_1_.func_226262_d_()) {
         this.setGrowingAge(-24000);
      }

      lvt_6_1_.func_226260_b_();
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   @Nullable
   public abstract AgeableEntity createChild(AgeableEntity var1);

   protected void onChildSpawnFromEgg(PlayerEntity p_213406_1_, AgeableEntity p_213406_2_) {
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      Item lvt_4_1_ = lvt_3_1_.getItem();
      if (lvt_4_1_ instanceof SpawnEggItem && ((SpawnEggItem)lvt_4_1_).hasType(lvt_3_1_.getTag(), this.getType())) {
         if (!this.world.isRemote) {
            AgeableEntity lvt_5_1_ = this.createChild(this);
            if (lvt_5_1_ != null) {
               lvt_5_1_.setGrowingAge(-24000);
               lvt_5_1_.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 0.0F, 0.0F);
               this.world.addEntity(lvt_5_1_);
               if (lvt_3_1_.hasDisplayName()) {
                  lvt_5_1_.setCustomName(lvt_3_1_.getDisplayName());
               }

               this.onChildSpawnFromEgg(p_184645_1_, lvt_5_1_);
               if (!p_184645_1_.abilities.isCreativeMode) {
                  lvt_3_1_.shrink(1);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(BABY, false);
   }

   public int getGrowingAge() {
      if (this.world.isRemote) {
         return (Boolean)this.dataManager.get(BABY) ? -1 : 1;
      } else {
         return this.growingAge;
      }
   }

   public void ageUp(int p_175501_1_, boolean p_175501_2_) {
      int lvt_3_1_ = this.getGrowingAge();
      int lvt_4_1_ = lvt_3_1_;
      lvt_3_1_ += p_175501_1_ * 20;
      if (lvt_3_1_ > 0) {
         lvt_3_1_ = 0;
      }

      int lvt_5_1_ = lvt_3_1_ - lvt_4_1_;
      this.setGrowingAge(lvt_3_1_);
      if (p_175501_2_) {
         this.forcedAge += lvt_5_1_;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.forcedAge);
      }

   }

   public void addGrowth(int p_110195_1_) {
      this.ageUp(p_110195_1_, false);
   }

   public void setGrowingAge(int p_70873_1_) {
      int lvt_2_1_ = this.growingAge;
      this.growingAge = p_70873_1_;
      if (lvt_2_1_ < 0 && p_70873_1_ >= 0 || lvt_2_1_ >= 0 && p_70873_1_ < 0) {
         this.dataManager.set(BABY, p_70873_1_ < 0);
         this.onGrowingAdult();
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("Age", this.getGrowingAge());
      p_213281_1_.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setGrowingAge(p_70037_1_.getInt("Age"));
      this.forcedAge = p_70037_1_.getInt("ForcedAge");
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (BABY.equals(p_184206_1_)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else if (this.isAlive()) {
         int lvt_1_1_ = this.getGrowingAge();
         if (lvt_1_1_ < 0) {
            ++lvt_1_1_;
            this.setGrowingAge(lvt_1_1_);
         } else if (lvt_1_1_ > 0) {
            --lvt_1_1_;
            this.setGrowingAge(lvt_1_1_);
         }
      }

   }

   protected void onGrowingAdult() {
   }

   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   static {
      BABY = EntityDataManager.createKey(AgeableEntity.class, DataSerializers.BOOLEAN);
   }

   public static class AgeableData implements ILivingEntityData {
      private int field_226254_a_;
      private boolean field_226255_b_ = true;
      private float field_226256_c_ = 0.05F;

      public int func_226257_a_() {
         return this.field_226254_a_;
      }

      public void func_226260_b_() {
         ++this.field_226254_a_;
      }

      public boolean func_226261_c_() {
         return this.field_226255_b_;
      }

      public void func_226259_a_(boolean p_226259_1_) {
         this.field_226255_b_ = p_226259_1_;
      }

      public float func_226262_d_() {
         return this.field_226256_c_;
      }

      public void func_226258_a_(float p_226258_1_) {
         this.field_226256_c_ = p_226258_1_;
      }
   }
}
