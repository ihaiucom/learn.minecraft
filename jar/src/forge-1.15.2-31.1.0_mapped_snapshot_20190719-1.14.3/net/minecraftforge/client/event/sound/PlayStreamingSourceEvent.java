package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSource;

public class PlayStreamingSourceEvent extends SoundEvent.SoundSourceEvent {
   public PlayStreamingSourceEvent(SoundEngine manager, ISound sound, SoundSource source) {
      super(manager, sound, source);
   }
}
