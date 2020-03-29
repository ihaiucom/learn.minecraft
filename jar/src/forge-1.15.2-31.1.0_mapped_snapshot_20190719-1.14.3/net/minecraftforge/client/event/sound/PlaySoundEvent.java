package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;

public class PlaySoundEvent extends SoundEvent {
   private final String name;
   private final ISound sound;
   private ISound result;

   public PlaySoundEvent(SoundEngine manager, ISound sound) {
      super(manager);
      this.sound = sound;
      this.name = sound.getSoundLocation().getPath();
      this.setResultSound(sound);
   }

   public String getName() {
      return this.name;
   }

   public ISound getSound() {
      return this.sound;
   }

   public ISound getResultSound() {
      return this.result;
   }

   public void setResultSound(ISound result) {
      this.result = result;
   }
}
