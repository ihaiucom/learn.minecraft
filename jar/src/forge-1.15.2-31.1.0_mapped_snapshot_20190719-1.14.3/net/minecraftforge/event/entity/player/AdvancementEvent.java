package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;

public class AdvancementEvent extends PlayerEvent {
   private final Advancement advancement;

   public AdvancementEvent(PlayerEntity player, Advancement advancement) {
      super(player);
      this.advancement = advancement;
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }
}
