package net.minecraftforge.event.entity.living;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class AnimalTameEvent extends LivingEvent {
   private final AnimalEntity animal;
   private final PlayerEntity tamer;

   public AnimalTameEvent(AnimalEntity animal, PlayerEntity tamer) {
      super(animal);
      this.animal = animal;
      this.tamer = tamer;
   }

   public AnimalEntity getAnimal() {
      return this.animal;
   }

   public PlayerEntity getTamer() {
      return this.tamer;
   }
}
