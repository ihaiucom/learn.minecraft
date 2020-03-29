package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AnimalEntity extends AgeableEntity {
   private int inLove;
   private UUID playerInLove;

   protected AnimalEntity(EntityType<? extends AnimalEntity> p_i48568_1_, World p_i48568_2_) {
      super(p_i48568_1_, p_i48568_2_);
   }

   protected void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   public void livingTick() {
      super.livingTick();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double lvt_1_1_ = this.rand.nextGaussian() * 0.02D;
            double lvt_3_1_ = this.rand.nextGaussian() * 0.02D;
            double lvt_5_1_ = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), lvt_1_1_, lvt_3_1_, lvt_5_1_);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.inLove = 0;
         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getBlockState(p_205022_1_.down()).getBlock() == Blocks.GRASS_BLOCK ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("InLove", this.inLove);
      if (this.playerInLove != null) {
         p_213281_1_.putUniqueId("LoveCause", this.playerInLove);
      }

   }

   public double getYOffset() {
      return 0.14D;
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.inLove = p_70037_1_.getInt("InLove");
      this.playerInLove = p_70037_1_.hasUniqueId("LoveCause") ? p_70037_1_.getUniqueId("LoveCause") : null;
   }

   public static boolean func_223316_b(EntityType<? extends AnimalEntity> p_223316_0_, IWorld p_223316_1_, SpawnReason p_223316_2_, BlockPos p_223316_3_, Random p_223316_4_) {
      return p_223316_1_.getBlockState(p_223316_3_.down()).getBlock() == Blocks.GRASS_BLOCK && p_223316_1_.func_226659_b_(p_223316_3_, 0) > 8;
   }

   public int getTalkInterval() {
      return 120;
   }

   public boolean canDespawn(double p_213397_1_) {
      return false;
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      return 1 + this.world.rand.nextInt(3);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Items.WHEAT;
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (this.isBreedingItem(lvt_3_1_)) {
         if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
            this.setInLove(p_184645_1_);
            p_184645_1_.func_226292_a_(p_184645_2_, true);
            return true;
         }

         if (this.isChild()) {
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
            this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   protected void consumeItemFromStack(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
      if (!p_175505_1_.abilities.isCreativeMode) {
         p_175505_2_.shrink(1);
      }

   }

   public boolean canBreed() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable PlayerEntity p_146082_1_) {
      this.inLove = 600;
      if (p_146082_1_ != null) {
         this.playerInLove = p_146082_1_.getUniqueID();
      }

      this.world.setEntityState(this, (byte)18);
   }

   public void setInLove(int p_204700_1_) {
      this.inLove = p_204700_1_;
   }

   @Nullable
   public ServerPlayerEntity getLoveCause() {
      if (this.playerInLove == null) {
         return null;
      } else {
         PlayerEntity lvt_1_1_ = this.world.getPlayerByUuid(this.playerInLove);
         return lvt_1_1_ instanceof ServerPlayerEntity ? (ServerPlayerEntity)lvt_1_1_ : null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   public boolean canMateWith(AnimalEntity p_70878_1_) {
      if (p_70878_1_ == this) {
         return false;
      } else if (p_70878_1_.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && p_70878_1_.isInLove();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 18) {
         for(int lvt_2_1_ = 0; lvt_2_1_ < 7; ++lvt_2_1_) {
            double lvt_3_1_ = this.rand.nextGaussian() * 0.02D;
            double lvt_5_1_ = this.rand.nextGaussian() * 0.02D;
            double lvt_7_1_ = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.func_226282_d_(1.0D), this.func_226279_cv_() + 0.5D, this.func_226287_g_(1.0D), lvt_3_1_, lvt_5_1_, lvt_7_1_);
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }
}
