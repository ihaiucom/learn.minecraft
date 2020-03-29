package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.ChunkPos;

public final class RegionFileCache implements AutoCloseable {
   private final Long2ObjectLinkedOpenHashMap<RegionFile> cache = new Long2ObjectLinkedOpenHashMap();
   private final File folder;

   RegionFileCache(File p_i49938_1_) {
      this.folder = p_i49938_1_;
   }

   private RegionFile loadFile(ChunkPos p_219098_1_) throws IOException {
      long i = ChunkPos.asLong(p_219098_1_.getRegionCoordX(), p_219098_1_.getRegionCoordZ());
      RegionFile regionfile = (RegionFile)this.cache.getAndMoveToFirst(i);
      if (regionfile != null) {
         return regionfile;
      } else {
         if (this.cache.size() >= 256) {
            ((RegionFile)this.cache.removeLast()).close();
         }

         if (!this.folder.exists()) {
            this.folder.mkdirs();
         }

         File file1 = new File(this.folder, "r." + p_219098_1_.getRegionCoordX() + "." + p_219098_1_.getRegionCoordZ() + ".mca");
         RegionFile regionfile1 = (new RegionFile(file1, this.folder)).extractLargeChunks(p_219098_1_);
         this.cache.putAndMoveToFirst(i, regionfile1);
         return regionfile1;
      }
   }

   @Nullable
   public CompoundNBT readChunk(ChunkPos p_219099_1_) throws IOException {
      RegionFile regionfile = this.loadFile(p_219099_1_);
      DataInputStream datainputstream = regionfile.func_222666_a(p_219099_1_);
      Throwable var5 = null;

      CompoundNBT var6;
      try {
         CompoundNBT compoundnbt;
         if (datainputstream == null) {
            compoundnbt = null;
            return compoundnbt;
         }

         compoundnbt = CompressedStreamTools.read(datainputstream);
         var6 = compoundnbt;
      } catch (Throwable var16) {
         var5 = var16;
         throw var16;
      } finally {
         if (datainputstream != null) {
            if (var5 != null) {
               try {
                  datainputstream.close();
               } catch (Throwable var15) {
                  var5.addSuppressed(var15);
               }
            } else {
               datainputstream.close();
            }
         }

      }

      return var6;
   }

   protected void writeChunk(ChunkPos p_219100_1_, CompoundNBT p_219100_2_) throws IOException {
      RegionFile regionfile = this.loadFile(p_219100_1_);
      DataOutputStream dataoutputstream = regionfile.func_222661_c(p_219100_1_);
      Throwable var5 = null;

      try {
         CompressedStreamTools.write(p_219100_2_, (DataOutput)dataoutputstream);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (dataoutputstream != null) {
            if (var5 != null) {
               try {
                  dataoutputstream.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               dataoutputstream.close();
            }
         }

      }

   }

   public void close() throws IOException {
      ObjectIterator var1 = this.cache.values().iterator();

      while(var1.hasNext()) {
         RegionFile regionfile = (RegionFile)var1.next();
         regionfile.close();
      }

   }
}
