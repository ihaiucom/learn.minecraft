package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundEngine;

public class SoundSetupEvent extends SoundEvent {
   public SoundSetupEvent(SoundEngine manager) {
      super(manager);
   }
}
