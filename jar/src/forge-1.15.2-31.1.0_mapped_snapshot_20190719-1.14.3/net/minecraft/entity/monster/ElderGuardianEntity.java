package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ElderGuardianEntity extends GuardianEntity {
   public static final float field_213629_b;

   public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> p_i50211_1_, World p_i50211_2_) {
      super(p_i50211_1_, p_i50211_2_);
      this.enablePersistence();
      if (this.wander != null) {
         this.wander.setExecutionChance(400);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
   }

   public int getAttackDuration() {
      return 60;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
   }

   protected void updateAITasks() {
      super.updateAITasks();
      int lvt_1_1_ = true;
      if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
         Effect lvt_2_1_ = Effects.MINING_FATIGUE;
         List<ServerPlayerEntity> lvt_3_1_ = ((ServerWorld)this.world).getPlayers((p_210138_1_) -> {
            return this.getDistanceSq(p_210138_1_) < 2500.0D && p_210138_1_.interactionManager.survivalOrAdventure();
         });
         int lvt_4_1_ = true;
         int lvt_5_1_ = true;
         int lvt_6_1_ = true;
         Iterator var7 = lvt_3_1_.iterator();

         label28:
         while(true) {
            ServerPlayerEntity lvt_8_1_;
            do {
               if (!var7.hasNext()) {
                  break label28;
               }

               lvt_8_1_ = (ServerPlayerEntity)var7.next();
            } while(lvt_8_1_.isPotionActive(lvt_2_1_) && lvt_8_1_.getActivePotionEffect(lvt_2_1_).getAmplifier() >= 2 && lvt_8_1_.getActivePotionEffect(lvt_2_1_).getDuration() >= 1200);

            lvt_8_1_.connection.sendPacket(new SChangeGameStatePacket(10, 0.0F));
            lvt_8_1_.addPotionEffect(new EffectInstance(lvt_2_1_, 6000, 2));
         }
      }

      if (!this.detachHome()) {
         this.setHomePosAndDistance(new BlockPos(this), 16);
      }

   }

   static {
      field_213629_b = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();
   }
}
