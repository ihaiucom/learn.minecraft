package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LocatableSound implements ISound {
   protected Sound sound;
   @Nullable
   private SoundEventAccessor soundEvent;
   protected final SoundCategory category;
   protected final ResourceLocation positionedSoundLocation;
   protected float volume;
   protected float pitch;
   protected float x;
   protected float y;
   protected float z;
   protected boolean repeat;
   protected int repeatDelay;
   protected ISound.AttenuationType attenuationType;
   protected boolean priority;
   protected boolean global;

   protected LocatableSound(SoundEvent p_i46533_1_, SoundCategory p_i46533_2_) {
      this(p_i46533_1_.getName(), p_i46533_2_);
   }

   protected LocatableSound(ResourceLocation p_i46534_1_, SoundCategory p_i46534_2_) {
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuationType = ISound.AttenuationType.LINEAR;
      this.positionedSoundLocation = p_i46534_1_;
      this.category = p_i46534_2_;
   }

   public ResourceLocation getSoundLocation() {
      return this.positionedSoundLocation;
   }

   public SoundEventAccessor createAccessor(SoundHandler p_184366_1_) {
      this.soundEvent = p_184366_1_.getAccessor(this.positionedSoundLocation);
      if (this.soundEvent == null) {
         this.sound = SoundHandler.MISSING_SOUND;
      } else {
         this.sound = this.soundEvent.cloneEntry();
      }

      return this.soundEvent;
   }

   public Sound getSound() {
      return this.sound;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public boolean canRepeat() {
      return this.repeat;
   }

   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   public float getVolume() {
      return this.volume * this.sound.getVolume();
   }

   public float getPitch() {
      return this.pitch * this.sound.getPitch();
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public ISound.AttenuationType getAttenuationType() {
      return this.attenuationType;
   }

   public boolean isGlobal() {
      return this.global;
   }
}
