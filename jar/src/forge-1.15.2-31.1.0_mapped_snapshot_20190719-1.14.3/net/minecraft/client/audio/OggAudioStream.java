package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class OggAudioStream implements IAudioStream {
   private long field_216461_a;
   private final AudioFormat field_216462_b;
   private final InputStream field_216463_c;
   private ByteBuffer field_216464_d = MemoryUtil.memAlloc(8192);

   public OggAudioStream(InputStream p_i51177_1_) throws IOException {
      this.field_216463_c = p_i51177_1_;
      this.field_216464_d.limit(0);
      MemoryStack lvt_2_1_ = MemoryStack.stackPush();
      Throwable var3 = null;

      try {
         IntBuffer lvt_4_1_ = lvt_2_1_.mallocInt(1);
         IntBuffer lvt_5_1_ = lvt_2_1_.mallocInt(1);

         while(this.field_216461_a == 0L) {
            if (!this.func_216456_c()) {
               throw new IOException("Failed to find Ogg header");
            }

            int lvt_6_1_ = this.field_216464_d.position();
            this.field_216464_d.position(0);
            this.field_216461_a = STBVorbis.stb_vorbis_open_pushdata(this.field_216464_d, lvt_4_1_, lvt_5_1_, (STBVorbisAlloc)null);
            this.field_216464_d.position(lvt_6_1_);
            int lvt_7_1_ = lvt_5_1_.get(0);
            if (lvt_7_1_ == 1) {
               this.func_216459_d();
            } else if (lvt_7_1_ != 0) {
               throw new IOException("Failed to read Ogg file " + lvt_7_1_);
            }
         }

         this.field_216464_d.position(this.field_216464_d.position() + lvt_4_1_.get(0));
         STBVorbisInfo lvt_6_2_ = STBVorbisInfo.mallocStack(lvt_2_1_);
         STBVorbis.stb_vorbis_get_info(this.field_216461_a, lvt_6_2_);
         this.field_216462_b = new AudioFormat((float)lvt_6_2_.sample_rate(), 16, lvt_6_2_.channels(), true, false);
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (lvt_2_1_ != null) {
            if (var3 != null) {
               try {
                  lvt_2_1_.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               lvt_2_1_.close();
            }
         }

      }
   }

   private boolean func_216456_c() throws IOException {
      int lvt_1_1_ = this.field_216464_d.limit();
      int lvt_2_1_ = this.field_216464_d.capacity() - lvt_1_1_;
      if (lvt_2_1_ == 0) {
         return true;
      } else {
         byte[] lvt_3_1_ = new byte[lvt_2_1_];
         int lvt_4_1_ = this.field_216463_c.read(lvt_3_1_);
         if (lvt_4_1_ == -1) {
            return false;
         } else {
            int lvt_5_1_ = this.field_216464_d.position();
            this.field_216464_d.limit(lvt_1_1_ + lvt_4_1_);
            this.field_216464_d.position(lvt_1_1_);
            this.field_216464_d.put(lvt_3_1_, 0, lvt_4_1_);
            this.field_216464_d.position(lvt_5_1_);
            return true;
         }
      }
   }

   private void func_216459_d() {
      boolean lvt_1_1_ = this.field_216464_d.position() == 0;
      boolean lvt_2_1_ = this.field_216464_d.position() == this.field_216464_d.limit();
      if (lvt_2_1_ && !lvt_1_1_) {
         this.field_216464_d.position(0);
         this.field_216464_d.limit(0);
      } else {
         ByteBuffer lvt_3_1_ = MemoryUtil.memAlloc(lvt_1_1_ ? 2 * this.field_216464_d.capacity() : this.field_216464_d.capacity());
         lvt_3_1_.put(this.field_216464_d);
         MemoryUtil.memFree(this.field_216464_d);
         lvt_3_1_.flip();
         this.field_216464_d = lvt_3_1_;
      }

   }

   private boolean func_216460_a(OggAudioStream.Buffer p_216460_1_) throws IOException {
      if (this.field_216461_a == 0L) {
         return false;
      } else {
         MemoryStack lvt_2_1_ = MemoryStack.stackPush();
         Throwable var3 = null;

         try {
            PointerBuffer lvt_4_1_ = lvt_2_1_.mallocPointer(1);
            IntBuffer lvt_5_1_ = lvt_2_1_.mallocInt(1);
            IntBuffer lvt_6_1_ = lvt_2_1_.mallocInt(1);

            while(true) {
               int lvt_7_1_ = STBVorbis.stb_vorbis_decode_frame_pushdata(this.field_216461_a, this.field_216464_d, lvt_5_1_, lvt_4_1_, lvt_6_1_);
               this.field_216464_d.position(this.field_216464_d.position() + lvt_7_1_);
               int lvt_8_1_ = STBVorbis.stb_vorbis_get_error(this.field_216461_a);
               if (lvt_8_1_ == 1) {
                  this.func_216459_d();
                  if (!this.func_216456_c()) {
                     boolean var25 = false;
                     return var25;
                  }
               } else {
                  if (lvt_8_1_ != 0) {
                     throw new IOException("Failed to read Ogg file " + lvt_8_1_);
                  }

                  int lvt_9_1_ = lvt_6_1_.get(0);
                  if (lvt_9_1_ != 0) {
                     int lvt_10_1_ = lvt_5_1_.get(0);
                     PointerBuffer lvt_11_1_ = lvt_4_1_.getPointerBuffer(lvt_10_1_);
                     boolean var12;
                     if (lvt_10_1_ != 1) {
                        if (lvt_10_1_ == 2) {
                           this.func_216458_a(lvt_11_1_.getFloatBuffer(0, lvt_9_1_), lvt_11_1_.getFloatBuffer(1, lvt_9_1_), p_216460_1_);
                           var12 = true;
                           return var12;
                        }

                        throw new IllegalStateException("Invalid number of channels: " + lvt_10_1_);
                     }

                     this.func_216457_a(lvt_11_1_.getFloatBuffer(0, lvt_9_1_), p_216460_1_);
                     var12 = true;
                     return var12;
                  }
               }
            }
         } catch (Throwable var23) {
            var3 = var23;
            throw var23;
         } finally {
            if (lvt_2_1_ != null) {
               if (var3 != null) {
                  try {
                     lvt_2_1_.close();
                  } catch (Throwable var22) {
                     var3.addSuppressed(var22);
                  }
               } else {
                  lvt_2_1_.close();
               }
            }

         }
      }
   }

   private void func_216457_a(FloatBuffer p_216457_1_, OggAudioStream.Buffer p_216457_2_) {
      while(p_216457_1_.hasRemaining()) {
         p_216457_2_.func_216446_a(p_216457_1_.get());
      }

   }

   private void func_216458_a(FloatBuffer p_216458_1_, FloatBuffer p_216458_2_, OggAudioStream.Buffer p_216458_3_) {
      while(p_216458_1_.hasRemaining() && p_216458_2_.hasRemaining()) {
         p_216458_3_.func_216446_a(p_216458_1_.get());
         p_216458_3_.func_216446_a(p_216458_2_.get());
      }

   }

   public void close() throws IOException {
      if (this.field_216461_a != 0L) {
         STBVorbis.stb_vorbis_close(this.field_216461_a);
         this.field_216461_a = 0L;
      }

      MemoryUtil.memFree(this.field_216464_d);
      this.field_216463_c.close();
   }

   public AudioFormat func_216454_a() {
      return this.field_216462_b;
   }

   @Nullable
   public ByteBuffer func_216455_a(int p_216455_1_) throws IOException {
      OggAudioStream.Buffer lvt_2_1_ = new OggAudioStream.Buffer(p_216455_1_ + 8192);

      while(this.func_216460_a(lvt_2_1_) && lvt_2_1_.field_216451_c < p_216455_1_) {
      }

      return lvt_2_1_.func_216445_a();
   }

   public ByteBuffer func_216453_b() throws IOException {
      OggAudioStream.Buffer lvt_1_1_ = new OggAudioStream.Buffer(16384);

      while(this.func_216460_a(lvt_1_1_)) {
      }

      return lvt_1_1_.func_216445_a();
   }

   @OnlyIn(Dist.CLIENT)
   static class Buffer {
      private final List<ByteBuffer> field_216449_a = Lists.newArrayList();
      private final int field_216450_b;
      private int field_216451_c;
      private ByteBuffer field_216452_d;

      public Buffer(int p_i50626_1_) {
         this.field_216450_b = p_i50626_1_ + 1 & -2;
         this.func_216447_b();
      }

      private void func_216447_b() {
         this.field_216452_d = BufferUtils.createByteBuffer(this.field_216450_b);
      }

      public void func_216446_a(float p_216446_1_) {
         if (this.field_216452_d.remaining() == 0) {
            this.field_216452_d.flip();
            this.field_216449_a.add(this.field_216452_d);
            this.func_216447_b();
         }

         int lvt_2_1_ = MathHelper.clamp((int)(p_216446_1_ * 32767.5F - 0.5F), -32768, 32767);
         this.field_216452_d.putShort((short)lvt_2_1_);
         this.field_216451_c += 2;
      }

      public ByteBuffer func_216445_a() {
         this.field_216452_d.flip();
         if (this.field_216449_a.isEmpty()) {
            return this.field_216452_d;
         } else {
            ByteBuffer lvt_1_1_ = BufferUtils.createByteBuffer(this.field_216451_c);
            this.field_216449_a.forEach(lvt_1_1_::put);
            lvt_1_1_.put(this.field_216452_d);
            lvt_1_1_.flip();
            return lvt_1_1_;
         }
      }
   }
}
