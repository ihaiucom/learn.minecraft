package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

public class TriggerSkeletonTrapGoal extends Goal {
   private final SkeletonHorseEntity horse;

   public TriggerSkeletonTrapGoal(SkeletonHorseEntity p_i46797_1_) {
      this.horse = p_i46797_1_;
   }

   public boolean shouldExecute() {
      return this.horse.world.isPlayerWithin(this.horse.func_226277_ct_(), this.horse.func_226278_cu_(), this.horse.func_226281_cx_(), 10.0D);
   }

   public void tick() {
      DifficultyInstance lvt_1_1_ = this.horse.world.getDifficultyForLocation(new BlockPos(this.horse));
      this.horse.setTrap(false);
      this.horse.setHorseTamed(true);
      this.horse.setGrowingAge(0);
      ((ServerWorld)this.horse.world).addLightningBolt(new LightningBoltEntity(this.horse.world, this.horse.func_226277_ct_(), this.horse.func_226278_cu_(), this.horse.func_226281_cx_(), true));
      SkeletonEntity lvt_2_1_ = this.createSkeleton(lvt_1_1_, this.horse);
      lvt_2_1_.startRiding(this.horse);

      for(int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_) {
         AbstractHorseEntity lvt_4_1_ = this.createHorse(lvt_1_1_);
         SkeletonEntity lvt_5_1_ = this.createSkeleton(lvt_1_1_, lvt_4_1_);
         lvt_5_1_.startRiding(lvt_4_1_);
         lvt_4_1_.addVelocity(this.horse.getRNG().nextGaussian() * 0.5D, 0.0D, this.horse.getRNG().nextGaussian() * 0.5D);
      }

   }

   private AbstractHorseEntity createHorse(DifficultyInstance p_188515_1_) {
      SkeletonHorseEntity lvt_2_1_ = (SkeletonHorseEntity)EntityType.SKELETON_HORSE.create(this.horse.world);
      lvt_2_1_.onInitialSpawn(this.horse.world, p_188515_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      lvt_2_1_.setPosition(this.horse.func_226277_ct_(), this.horse.func_226278_cu_(), this.horse.func_226281_cx_());
      lvt_2_1_.hurtResistantTime = 60;
      lvt_2_1_.enablePersistence();
      lvt_2_1_.setHorseTamed(true);
      lvt_2_1_.setGrowingAge(0);
      lvt_2_1_.world.addEntity(lvt_2_1_);
      return lvt_2_1_;
   }

   private SkeletonEntity createSkeleton(DifficultyInstance p_188514_1_, AbstractHorseEntity p_188514_2_) {
      SkeletonEntity lvt_3_1_ = (SkeletonEntity)EntityType.SKELETON.create(p_188514_2_.world);
      lvt_3_1_.onInitialSpawn(p_188514_2_.world, p_188514_1_, SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
      lvt_3_1_.setPosition(p_188514_2_.func_226277_ct_(), p_188514_2_.func_226278_cu_(), p_188514_2_.func_226281_cx_());
      lvt_3_1_.hurtResistantTime = 60;
      lvt_3_1_.enablePersistence();
      if (lvt_3_1_.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
         lvt_3_1_.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      lvt_3_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(lvt_3_1_.getRNG(), lvt_3_1_.getHeldItemMainhand(), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)lvt_3_1_.getRNG().nextInt(18)), false));
      lvt_3_1_.setItemStackToSlot(EquipmentSlotType.HEAD, EnchantmentHelper.addRandomEnchantment(lvt_3_1_.getRNG(), lvt_3_1_.getItemStackFromSlot(EquipmentSlotType.HEAD), (int)(5.0F + p_188514_1_.getClampedAdditionalDifficulty() * (float)lvt_3_1_.getRNG().nextInt(18)), false));
      lvt_3_1_.world.addEntity(lvt_3_1_);
      return lvt_3_1_;
   }
}
