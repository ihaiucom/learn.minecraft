package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingExperienceDropEvent extends LivingEvent {
   private final PlayerEntity attackingPlayer;
   private final int originalExperiencePoints;
   private int droppedExperiencePoints;

   public LivingExperienceDropEvent(LivingEntity entity, PlayerEntity attackingPlayer, int originalExperience) {
      super(entity);
      this.attackingPlayer = attackingPlayer;
      this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
   }

   public int getDroppedExperience() {
      return this.droppedExperiencePoints;
   }

   public void setDroppedExperience(int droppedExperience) {
      this.droppedExperiencePoints = droppedExperience;
   }

   public PlayerEntity getAttackingPlayer() {
      return this.attackingPlayer;
   }

   public int getOriginalExperience() {
      return this.originalExperiencePoints;
   }
}
