package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSource;
import net.minecraftforge.eventbus.api.Event;

public class SoundEvent extends Event {
   private final SoundEngine manager;

   public SoundEvent(SoundEngine manager) {
      this.manager = manager;
   }

   public SoundEngine getManager() {
      return this.manager;
   }

   public static class SoundSourceEvent extends SoundEvent {
      private final ISound sound;
      private final SoundSource source;
      private final String name;

      public SoundSourceEvent(SoundEngine manager, ISound sound, SoundSource source) {
         super(manager);
         this.name = sound.getSoundLocation().getPath();
         this.sound = sound;
         this.source = source;
      }

      public ISound getSound() {
         return this.sound;
      }

      public SoundSource getSource() {
         return this.source;
      }

      public String getName() {
         return this.name;
      }
   }
}
