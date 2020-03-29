package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicTicker {
   private final Random random = new Random();
   private final Minecraft client;
   private ISound currentMusic;
   private int timeUntilNextMusic = 100;

   public MusicTicker(Minecraft p_i45112_1_) {
      this.client = p_i45112_1_;
   }

   public void tick() {
      MusicTicker.MusicType lvt_1_1_ = this.client.getAmbientMusicType();
      if (this.currentMusic != null) {
         if (!lvt_1_1_.getSound().getName().equals(this.currentMusic.getSoundLocation())) {
            this.client.getSoundHandler().stop(this.currentMusic);
            this.timeUntilNextMusic = MathHelper.nextInt(this.random, 0, lvt_1_1_.getMinDelay() / 2);
         }

         if (!this.client.getSoundHandler().isPlaying(this.currentMusic)) {
            this.currentMusic = null;
            this.timeUntilNextMusic = Math.min(MathHelper.nextInt(this.random, lvt_1_1_.getMinDelay(), lvt_1_1_.getMaxDelay()), this.timeUntilNextMusic);
         }
      }

      this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, lvt_1_1_.getMaxDelay());
      if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
         this.play(lvt_1_1_);
      }

   }

   public void play(MusicTicker.MusicType p_181558_1_) {
      this.currentMusic = SimpleSound.music(p_181558_1_.getSound());
      this.client.getSoundHandler().play(this.currentMusic);
      this.timeUntilNextMusic = Integer.MAX_VALUE;
   }

   public void stop() {
      if (this.currentMusic != null) {
         this.client.getSoundHandler().stop(this.currentMusic);
         this.currentMusic = null;
         this.timeUntilNextMusic = 0;
      }

   }

   public boolean isPlaying(MusicTicker.MusicType p_209100_1_) {
      return this.currentMusic == null ? false : p_209100_1_.getSound().getName().equals(this.currentMusic.getSoundLocation());
   }

   @OnlyIn(Dist.CLIENT)
   public static enum MusicType {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent sound;
      private final int minDelay;
      private final int maxDelay;

      private MusicType(SoundEvent p_i47050_3_, int p_i47050_4_, int p_i47050_5_) {
         this.sound = p_i47050_3_;
         this.minDelay = p_i47050_4_;
         this.maxDelay = p_i47050_5_;
      }

      public SoundEvent getSound() {
         return this.sound;
      }

      public int getMinDelay() {
         return this.minDelay;
      }

      public int getMaxDelay() {
         return this.maxDelay;
      }
   }
}
