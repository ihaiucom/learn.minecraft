package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent {
   private SoundEvent name;
   private SoundCategory category;
   private final float volume;
   private final float pitch;
   private float newVolume;
   private float newPitch;

   public PlaySoundAtEntityEvent(Entity entity, SoundEvent name, SoundCategory category, float volume, float pitch) {
      super(entity);
      this.name = name;
      this.category = category;
      this.volume = volume;
      this.pitch = pitch;
      this.newVolume = volume;
      this.newPitch = pitch;
   }

   public SoundEvent getSound() {
      return this.name;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public float getDefaultVolume() {
      return this.volume;
   }

   public float getDefaultPitch() {
      return this.pitch;
   }

   public float getVolume() {
      return this.newVolume;
   }

   public float getPitch() {
      return this.newPitch;
   }

   public void setSound(SoundEvent value) {
      this.name = value;
   }

   public void setCategory(SoundCategory category) {
      this.category = category;
   }

   public void setVolume(float value) {
      this.newVolume = value;
   }

   public void setPitch(float value) {
      this.newPitch = value;
   }
}
