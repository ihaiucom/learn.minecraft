package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannelManager {
   private final Set<ChannelManager.Entry> channels = Sets.newIdentityHashSet();
   private final SoundSystem sndSystem;
   private final Executor soundExecutor;

   public ChannelManager(SoundSystem p_i50894_1_, Executor p_i50894_2_) {
      this.sndSystem = p_i50894_1_;
      this.soundExecutor = p_i50894_2_;
   }

   public ChannelManager.Entry createChannel(SoundSystem.Mode p_217895_1_) {
      ChannelManager.Entry lvt_2_1_ = new ChannelManager.Entry();
      this.soundExecutor.execute(() -> {
         SoundSource lvt_3_1_ = this.sndSystem.func_216403_a(p_217895_1_);
         if (lvt_3_1_ != null) {
            lvt_2_1_.source = lvt_3_1_;
            this.channels.add(lvt_2_1_);
         }

      });
      return lvt_2_1_;
   }

   public void func_217897_a(Consumer<Stream<SoundSource>> p_217897_1_) {
      this.soundExecutor.execute(() -> {
         p_217897_1_.accept(this.channels.stream().map((p_217896_0_) -> {
            return p_217896_0_.source;
         }).filter(Objects::nonNull));
      });
   }

   public void tick() {
      this.soundExecutor.execute(() -> {
         Iterator lvt_1_1_ = this.channels.iterator();

         while(lvt_1_1_.hasNext()) {
            ChannelManager.Entry lvt_2_1_ = (ChannelManager.Entry)lvt_1_1_.next();
            lvt_2_1_.source.func_216434_i();
            if (lvt_2_1_.source.func_216435_g()) {
               lvt_2_1_.release();
               lvt_1_1_.remove();
            }
         }

      });
   }

   public void releaseAll() {
      this.channels.forEach(ChannelManager.Entry::release);
      this.channels.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public class Entry {
      private SoundSource source;
      private boolean released;

      public boolean isReleased() {
         return this.released;
      }

      public void runOnSoundExecutor(Consumer<SoundSource> p_217888_1_) {
         ChannelManager.this.soundExecutor.execute(() -> {
            if (this.source != null) {
               p_217888_1_.accept(this.source);
            }

         });
      }

      public void release() {
         this.released = true;
         ChannelManager.this.sndSystem.release(this.source);
         this.source = null;
      }
   }
}
