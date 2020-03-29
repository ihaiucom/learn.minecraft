package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AudioStreamManager {
   private final IResourceManager resourceManager;
   private final Map<ResourceLocation, CompletableFuture<AudioStreamBuffer>> field_217919_b = Maps.newHashMap();

   public AudioStreamManager(IResourceManager p_i50893_1_) {
      this.resourceManager = p_i50893_1_;
   }

   public CompletableFuture<AudioStreamBuffer> func_217909_a(ResourceLocation p_217909_1_) {
      return (CompletableFuture)this.field_217919_b.computeIfAbsent(p_217909_1_, (p_217913_1_) -> {
         return CompletableFuture.supplyAsync(() -> {
            try {
               IResource lvt_2_1_ = this.resourceManager.getResource(p_217913_1_);
               Throwable var3 = null;

               AudioStreamBuffer var9;
               try {
                  InputStream lvt_4_1_ = lvt_2_1_.getInputStream();
                  Throwable var5 = null;

                  try {
                     IAudioStream lvt_6_1_ = new OggAudioStream(lvt_4_1_);
                     Throwable var7 = null;

                     try {
                        ByteBuffer lvt_8_1_ = lvt_6_1_.func_216453_b();
                        var9 = new AudioStreamBuffer(lvt_8_1_, lvt_6_1_.func_216454_a());
                     } catch (Throwable var56) {
                        var7 = var56;
                        throw var56;
                     } finally {
                        if (lvt_6_1_ != null) {
                           if (var7 != null) {
                              try {
                                 lvt_6_1_.close();
                              } catch (Throwable var55) {
                                 var7.addSuppressed(var55);
                              }
                           } else {
                              lvt_6_1_.close();
                           }
                        }

                     }
                  } catch (Throwable var58) {
                     var5 = var58;
                     throw var58;
                  } finally {
                     if (lvt_4_1_ != null) {
                        if (var5 != null) {
                           try {
                              lvt_4_1_.close();
                           } catch (Throwable var54) {
                              var5.addSuppressed(var54);
                           }
                        } else {
                           lvt_4_1_.close();
                        }
                     }

                  }
               } catch (Throwable var60) {
                  var3 = var60;
                  throw var60;
               } finally {
                  if (lvt_2_1_ != null) {
                     if (var3 != null) {
                        try {
                           lvt_2_1_.close();
                        } catch (Throwable var53) {
                           var3.addSuppressed(var53);
                        }
                     } else {
                        lvt_2_1_.close();
                     }
                  }

               }

               return var9;
            } catch (IOException var62) {
               throw new CompletionException(var62);
            }
         }, Util.getServerExecutor());
      });
   }

   public CompletableFuture<IAudioStream> func_217917_b(ResourceLocation p_217917_1_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            IResource lvt_2_1_ = this.resourceManager.getResource(p_217917_1_);
            InputStream lvt_3_1_ = lvt_2_1_.getInputStream();
            return new OggAudioStream(lvt_3_1_);
         } catch (IOException var4) {
            throw new CompletionException(var4);
         }
      }, Util.getServerExecutor());
   }

   public void func_217912_a() {
      this.field_217919_b.values().forEach((p_217910_0_) -> {
         p_217910_0_.thenAccept(AudioStreamBuffer::func_216474_b);
      });
      this.field_217919_b.clear();
   }

   public CompletableFuture<?> func_217908_a(Collection<Sound> p_217908_1_) {
      return CompletableFuture.allOf((CompletableFuture[])p_217908_1_.stream().map((p_217911_1_) -> {
         return this.func_217909_a(p_217911_1_.getSoundAsOggLocation());
      }).toArray((p_217916_0_) -> {
         return new CompletableFuture[p_217916_0_];
      }));
   }
}
