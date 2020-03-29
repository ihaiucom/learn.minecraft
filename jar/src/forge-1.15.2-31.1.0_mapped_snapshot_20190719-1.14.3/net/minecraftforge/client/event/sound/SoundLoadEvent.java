package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundEngine;

public class SoundLoadEvent extends SoundEvent {
   public SoundLoadEvent(SoundEngine manager) {
      super(manager);
   }
}
