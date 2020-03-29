package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class SoundSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private long field_216411_b;
   private long field_216412_c;
   private static final SoundSystem.IHandler DUMMY_HANDLER = new SoundSystem.IHandler() {
      @Nullable
      public SoundSource func_216398_a() {
         return null;
      }

      public boolean func_216396_a(SoundSource p_216396_1_) {
         return false;
      }

      public void func_216399_b() {
      }

      public int func_216395_c() {
         return 0;
      }

      public int func_216397_d() {
         return 0;
      }
   };
   private SoundSystem.IHandler staticHandler;
   private SoundSystem.IHandler streamingHandler;
   private final Listener field_216416_g;

   public SoundSystem() {
      this.staticHandler = DUMMY_HANDLER;
      this.streamingHandler = DUMMY_HANDLER;
      this.field_216416_g = new Listener();
   }

   public void func_216404_a() {
      this.field_216411_b = func_216406_f();
      ALCCapabilities lvt_1_1_ = ALC.createCapabilities(this.field_216411_b);
      if (ALUtils.func_216481_a(this.field_216411_b, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!lvt_1_1_.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         this.field_216412_c = ALC10.alcCreateContext(this.field_216411_b, (IntBuffer)null);
         ALC10.alcMakeContextCurrent(this.field_216412_c);
         int lvt_2_1_ = this.func_216405_e();
         int lvt_3_1_ = MathHelper.clamp((int)MathHelper.sqrt((float)lvt_2_1_), 2, 8);
         int lvt_4_1_ = MathHelper.clamp(lvt_2_1_ - lvt_3_1_, 8, 255);
         this.staticHandler = new SoundSystem.HandlerImpl(lvt_4_1_);
         this.streamingHandler = new SoundSystem.HandlerImpl(lvt_3_1_);
         ALCapabilities lvt_5_1_ = AL.createCapabilities(lvt_1_1_);
         ALUtils.func_216483_a("Initialization");
         if (!lvt_5_1_.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
         } else {
            AL10.alEnable(512);
            if (!lvt_5_1_.AL_EXT_LINEAR_DISTANCE) {
               throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
            } else {
               ALUtils.func_216483_a("Enable per-source distance models");
               LOGGER.info("OpenAL initialized.");
            }
         }
      }
   }

   private int func_216405_e() {
      MemoryStack lvt_1_1_ = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         int lvt_3_1_ = ALC10.alcGetInteger(this.field_216411_b, 4098);
         if (ALUtils.func_216481_a(this.field_216411_b, "Get attributes size")) {
            throw new IllegalStateException("Failed to get OpenAL attributes");
         } else {
            IntBuffer lvt_4_1_ = lvt_1_1_.mallocInt(lvt_3_1_);
            ALC10.alcGetIntegerv(this.field_216411_b, 4099, lvt_4_1_);
            if (ALUtils.func_216481_a(this.field_216411_b, "Get attributes")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            } else {
               int lvt_5_1_ = 0;

               int lvt_6_1_;
               int lvt_7_1_;
               do {
                  if (lvt_5_1_ >= lvt_3_1_) {
                     return 30;
                  }

                  lvt_6_1_ = lvt_4_1_.get(lvt_5_1_++);
                  if (lvt_6_1_ == 0) {
                     return 30;
                  }

                  lvt_7_1_ = lvt_4_1_.get(lvt_5_1_++);
               } while(lvt_6_1_ != 4112);

               int var8 = lvt_7_1_;
               return var8;
            }
         }
      } catch (Throwable var18) {
         var2 = var18;
         throw var18;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var17) {
                  var2.addSuppressed(var17);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }
   }

   private static long func_216406_f() {
      for(int lvt_0_1_ = 0; lvt_0_1_ < 3; ++lvt_0_1_) {
         long lvt_1_1_ = ALC10.alcOpenDevice((ByteBuffer)null);
         if (lvt_1_1_ != 0L && !ALUtils.func_216481_a(lvt_1_1_, "Open device")) {
            return lvt_1_1_;
         }
      }

      throw new IllegalStateException("Failed to open OpenAL device");
   }

   public void func_216409_b() {
      this.staticHandler.func_216399_b();
      this.streamingHandler.func_216399_b();
      ALC10.alcDestroyContext(this.field_216412_c);
      if (this.field_216411_b != 0L) {
         ALC10.alcCloseDevice(this.field_216411_b);
      }

   }

   public Listener getListener() {
      return this.field_216416_g;
   }

   @Nullable
   public SoundSource func_216403_a(SoundSystem.Mode p_216403_1_) {
      return (p_216403_1_ == SoundSystem.Mode.STREAMING ? this.streamingHandler : this.staticHandler).func_216398_a();
   }

   public void release(SoundSource p_216408_1_) {
      if (!this.staticHandler.func_216396_a(p_216408_1_) && !this.streamingHandler.func_216396_a(p_216408_1_)) {
         throw new IllegalStateException("Tried to release unknown channel");
      }
   }

   public String getDebugString() {
      return String.format("Sounds: %d/%d + %d/%d", this.staticHandler.func_216397_d(), this.staticHandler.func_216395_c(), this.streamingHandler.func_216397_d(), this.streamingHandler.func_216395_c());
   }

   @OnlyIn(Dist.CLIENT)
   static class HandlerImpl implements SoundSystem.IHandler {
      private final int field_216400_a;
      private final Set<SoundSource> field_216401_b = Sets.newIdentityHashSet();

      public HandlerImpl(int p_i50804_1_) {
         this.field_216400_a = p_i50804_1_;
      }

      @Nullable
      public SoundSource func_216398_a() {
         if (this.field_216401_b.size() >= this.field_216400_a) {
            return null;
         } else {
            SoundSource lvt_1_1_ = SoundSource.func_216426_a();
            if (lvt_1_1_ != null) {
               this.field_216401_b.add(lvt_1_1_);
            }

            return lvt_1_1_;
         }
      }

      public boolean func_216396_a(SoundSource p_216396_1_) {
         if (!this.field_216401_b.remove(p_216396_1_)) {
            return false;
         } else {
            p_216396_1_.func_216436_b();
            return true;
         }
      }

      public void func_216399_b() {
         this.field_216401_b.forEach(SoundSource::func_216436_b);
         this.field_216401_b.clear();
      }

      public int func_216395_c() {
         return this.field_216400_a;
      }

      public int func_216397_d() {
         return this.field_216401_b.size();
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface IHandler {
      @Nullable
      SoundSource func_216398_a();

      boolean func_216396_a(SoundSource var1);

      void func_216399_b();

      int func_216395_c();

      int func_216397_d();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Mode {
      STATIC,
      STREAMING;
   }
}
