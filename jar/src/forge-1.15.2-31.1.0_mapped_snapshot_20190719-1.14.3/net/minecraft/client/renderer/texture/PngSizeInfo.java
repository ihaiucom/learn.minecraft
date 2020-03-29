package net.minecraft.client.renderer.texture;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class PngSizeInfo {
   public final int width;
   public final int height;

   public PngSizeInfo(String p_i51172_1_, InputStream p_i51172_2_) throws IOException {
      MemoryStack lvt_3_1_ = MemoryStack.stackPush();
      Throwable var4 = null;

      try {
         PngSizeInfo.Reader lvt_5_1_ = func_195695_a(p_i51172_2_);
         Throwable var6 = null;

         try {
            lvt_5_1_.getClass();
            STBIReadCallback lvt_7_1_ = STBIReadCallback.create(lvt_5_1_::func_195682_a);
            Throwable var8 = null;

            try {
               lvt_5_1_.getClass();
               STBISkipCallback lvt_9_1_ = STBISkipCallback.create(lvt_5_1_::func_195686_a);
               Throwable var10 = null;

               try {
                  lvt_5_1_.getClass();
                  STBIEOFCallback lvt_11_1_ = STBIEOFCallback.create(lvt_5_1_::func_195685_a);
                  Throwable var12 = null;

                  try {
                     STBIIOCallbacks lvt_13_1_ = STBIIOCallbacks.mallocStack(lvt_3_1_);
                     lvt_13_1_.read(lvt_7_1_);
                     lvt_13_1_.skip(lvt_9_1_);
                     lvt_13_1_.eof(lvt_11_1_);
                     IntBuffer lvt_14_1_ = lvt_3_1_.mallocInt(1);
                     IntBuffer lvt_15_1_ = lvt_3_1_.mallocInt(1);
                     IntBuffer lvt_16_1_ = lvt_3_1_.mallocInt(1);
                     if (!STBImage.stbi_info_from_callbacks(lvt_13_1_, 0L, lvt_14_1_, lvt_15_1_, lvt_16_1_)) {
                        throw new IOException("Could not read info from the PNG file " + p_i51172_1_ + " " + STBImage.stbi_failure_reason());
                     }

                     this.width = lvt_14_1_.get(0);
                     this.height = lvt_15_1_.get(0);
                  } catch (Throwable var122) {
                     var12 = var122;
                     throw var122;
                  } finally {
                     if (lvt_11_1_ != null) {
                        if (var12 != null) {
                           try {
                              lvt_11_1_.close();
                           } catch (Throwable var121) {
                              var12.addSuppressed(var121);
                           }
                        } else {
                           lvt_11_1_.close();
                        }
                     }

                  }
               } catch (Throwable var124) {
                  var10 = var124;
                  throw var124;
               } finally {
                  if (lvt_9_1_ != null) {
                     if (var10 != null) {
                        try {
                           lvt_9_1_.close();
                        } catch (Throwable var120) {
                           var10.addSuppressed(var120);
                        }
                     } else {
                        lvt_9_1_.close();
                     }
                  }

               }
            } catch (Throwable var126) {
               var8 = var126;
               throw var126;
            } finally {
               if (lvt_7_1_ != null) {
                  if (var8 != null) {
                     try {
                        lvt_7_1_.close();
                     } catch (Throwable var119) {
                        var8.addSuppressed(var119);
                     }
                  } else {
                     lvt_7_1_.close();
                  }
               }

            }
         } catch (Throwable var128) {
            var6 = var128;
            throw var128;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var118) {
                     var6.addSuppressed(var118);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (Throwable var130) {
         var4 = var130;
         throw var130;
      } finally {
         if (lvt_3_1_ != null) {
            if (var4 != null) {
               try {
                  lvt_3_1_.close();
               } catch (Throwable var117) {
                  var4.addSuppressed(var117);
               }
            } else {
               lvt_3_1_.close();
            }
         }

      }

   }

   private static PngSizeInfo.Reader func_195695_a(InputStream p_195695_0_) {
      return (PngSizeInfo.Reader)(p_195695_0_ instanceof FileInputStream ? new PngSizeInfo.ReaderSeekable(((FileInputStream)p_195695_0_).getChannel()) : new PngSizeInfo.ReaderBuffer(Channels.newChannel(p_195695_0_)));
   }

   @OnlyIn(Dist.CLIENT)
   static class ReaderBuffer extends PngSizeInfo.Reader {
      private final ReadableByteChannel channel;
      private long field_195690_c;
      private int field_195691_d;
      private int field_195692_e;
      private int field_195693_f;

      private ReaderBuffer(ReadableByteChannel p_i48136_1_) {
         super(null);
         this.field_195690_c = MemoryUtil.nmemAlloc(128L);
         this.field_195691_d = 128;
         this.channel = p_i48136_1_;
      }

      private void func_195688_b(int p_195688_1_) throws IOException {
         ByteBuffer lvt_2_1_ = MemoryUtil.memByteBuffer(this.field_195690_c, this.field_195691_d);
         if (p_195688_1_ + this.field_195693_f > this.field_195691_d) {
            this.field_195691_d = p_195688_1_ + this.field_195693_f;
            lvt_2_1_ = MemoryUtil.memRealloc(lvt_2_1_, this.field_195691_d);
            this.field_195690_c = MemoryUtil.memAddress(lvt_2_1_);
         }

         lvt_2_1_.position(this.field_195692_e);

         while(p_195688_1_ + this.field_195693_f > this.field_195692_e) {
            try {
               int lvt_3_1_ = this.channel.read(lvt_2_1_);
               if (lvt_3_1_ == -1) {
                  break;
               }
            } finally {
               this.field_195692_e = lvt_2_1_.position();
            }
         }

      }

      public int func_195683_b(long p_195683_1_, int p_195683_3_) throws IOException {
         this.func_195688_b(p_195683_3_);
         if (p_195683_3_ + this.field_195693_f > this.field_195692_e) {
            p_195683_3_ = this.field_195692_e - this.field_195693_f;
         }

         MemoryUtil.memCopy(this.field_195690_c + (long)this.field_195693_f, p_195683_1_, (long)p_195683_3_);
         this.field_195693_f += p_195683_3_;
         return p_195683_3_;
      }

      public void func_195684_a(int p_195684_1_) throws IOException {
         if (p_195684_1_ > 0) {
            this.func_195688_b(p_195684_1_);
            if (p_195684_1_ + this.field_195693_f > this.field_195692_e) {
               throw new EOFException("Can't skip past the EOF.");
            }
         }

         if (this.field_195693_f + p_195684_1_ < 0) {
            throw new IOException("Can't seek before the beginning: " + (this.field_195693_f + p_195684_1_));
         } else {
            this.field_195693_f += p_195684_1_;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.field_195690_c);
         this.channel.close();
      }

      // $FF: synthetic method
      ReaderBuffer(ReadableByteChannel p_i48137_1_, Object p_i48137_2_) {
         this(p_i48137_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ReaderSeekable extends PngSizeInfo.Reader {
      private final SeekableByteChannel channel;

      private ReaderSeekable(SeekableByteChannel p_i48134_1_) {
         super(null);
         this.channel = p_i48134_1_;
      }

      public int func_195683_b(long p_195683_1_, int p_195683_3_) throws IOException {
         ByteBuffer lvt_4_1_ = MemoryUtil.memByteBuffer(p_195683_1_, p_195683_3_);
         return this.channel.read(lvt_4_1_);
      }

      public void func_195684_a(int p_195684_1_) throws IOException {
         this.channel.position(this.channel.position() + (long)p_195684_1_);
      }

      public int func_195685_a(long p_195685_1_) {
         return super.func_195685_a(p_195685_1_) != 0 && this.channel.isOpen() ? 1 : 0;
      }

      public void close() throws IOException {
         this.channel.close();
      }

      // $FF: synthetic method
      ReaderSeekable(SeekableByteChannel p_i48135_1_, Object p_i48135_2_) {
         this(p_i48135_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Reader implements AutoCloseable {
      protected boolean field_195687_a;

      private Reader() {
      }

      int func_195682_a(long p_195682_1_, long p_195682_3_, int p_195682_5_) {
         try {
            return this.func_195683_b(p_195682_3_, p_195682_5_);
         } catch (IOException var7) {
            this.field_195687_a = true;
            return 0;
         }
      }

      void func_195686_a(long p_195686_1_, int p_195686_3_) {
         try {
            this.func_195684_a(p_195686_3_);
         } catch (IOException var5) {
            this.field_195687_a = true;
         }

      }

      int func_195685_a(long p_195685_1_) {
         return this.field_195687_a ? 1 : 0;
      }

      protected abstract int func_195683_b(long var1, int var3) throws IOException;

      protected abstract void func_195684_a(int var1) throws IOException;

      public abstract void close() throws IOException;

      // $FF: synthetic method
      Reader(Object p_i48138_1_) {
         this();
      }
   }
}
