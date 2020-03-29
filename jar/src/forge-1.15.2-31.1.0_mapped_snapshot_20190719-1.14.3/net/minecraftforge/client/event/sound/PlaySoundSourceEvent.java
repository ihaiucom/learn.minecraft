package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundSource;

public class PlaySoundSourceEvent extends SoundEvent.SoundSourceEvent {
   public PlaySoundSourceEvent(SoundEngine manager, ISound sound, SoundSource source) {
      super(manager, sound, source);
   }
}
