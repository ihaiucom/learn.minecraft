package net.minecraftforge.event.entity.player;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

public class PlayerXpEvent extends PlayerEvent {
   public PlayerXpEvent(PlayerEntity player) {
      super(player);
   }

   @Cancelable
   public static class LevelChange extends PlayerXpEvent {
      private int levels;

      public LevelChange(PlayerEntity player, int levels) {
         super(player);
         this.levels = levels;
      }

      public int getLevels() {
         return this.levels;
      }

      public void setLevels(int levels) {
         this.levels = levels;
      }
   }

   @Cancelable
   public static class XpChange extends PlayerXpEvent {
      private int amount;

      public XpChange(PlayerEntity player, int amount) {
         super(player);
         this.amount = amount;
      }

      public int getAmount() {
         return this.amount;
      }

      public void setAmount(int amount) {
         this.amount = amount;
      }
   }

   @Cancelable
   public static class PickupXp extends PlayerXpEvent {
      private final ExperienceOrbEntity orb;

      public PickupXp(PlayerEntity player, ExperienceOrbEntity orb) {
         super(player);
         this.orb = orb;
      }

      public ExperienceOrbEntity getOrb() {
         return this.orb;
      }
   }
}
