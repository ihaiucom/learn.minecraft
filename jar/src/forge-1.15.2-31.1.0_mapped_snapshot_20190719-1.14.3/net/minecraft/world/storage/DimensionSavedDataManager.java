package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraftforge.common.util.DummyWorldSaveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, WorldSavedData> savedDatum = Maps.newHashMap();
   private final DataFixer dataFixer;
   private final File folder;

   public DimensionSavedDataManager(File p_i51279_1_, DataFixer p_i51279_2_) {
      this.dataFixer = p_i51279_2_;
      this.folder = p_i51279_1_;
   }

   private File getDataFile(String p_215754_1_) {
      return new File(this.folder, p_215754_1_ + ".dat");
   }

   public <T extends WorldSavedData> T getOrCreate(Supplier<T> p_215752_1_, String p_215752_2_) {
      T t = this.get(p_215752_1_, p_215752_2_);
      if (t != null) {
         return t;
      } else {
         T t1 = (WorldSavedData)p_215752_1_.get();
         this.set(t1);
         return t1;
      }
   }

   @Nullable
   public <T extends WorldSavedData> T get(Supplier<T> p_215753_1_, String p_215753_2_) {
      WorldSavedData worldsaveddata = (WorldSavedData)this.savedDatum.get(p_215753_2_);
      if (worldsaveddata == DummyWorldSaveData.DUMMY) {
         return null;
      } else {
         if (worldsaveddata == null && !this.savedDatum.containsKey(p_215753_2_)) {
            worldsaveddata = this.func_223409_c(p_215753_1_, p_215753_2_);
            this.savedDatum.put(p_215753_2_, worldsaveddata);
         } else if (worldsaveddata == null) {
            this.savedDatum.put(p_215753_2_, DummyWorldSaveData.DUMMY);
            return null;
         }

         return worldsaveddata;
      }
   }

   @Nullable
   private <T extends WorldSavedData> T func_223409_c(Supplier<T> p_223409_1_, String p_223409_2_) {
      try {
         File file1 = this.getDataFile(p_223409_2_);
         if (file1.exists()) {
            T t = (WorldSavedData)p_223409_1_.get();
            CompoundNBT compoundnbt = this.load(p_223409_2_, SharedConstants.getVersion().getWorldVersion());
            t.read(compoundnbt.getCompound("data"));
            return t;
         }
      } catch (Exception var6) {
         LOGGER.error("Error loading saved data: {}", p_223409_2_, var6);
      }

      return (WorldSavedData)null;
   }

   public void set(WorldSavedData p_215757_1_) {
      this.savedDatum.put(p_215757_1_.getName(), p_215757_1_);
   }

   public CompoundNBT load(String p_215755_1_, int p_215755_2_) throws IOException {
      File file1 = this.getDataFile(p_215755_1_);
      PushbackInputStream pushbackinputstream = new PushbackInputStream(new FileInputStream(file1), 2);
      Throwable var6 = null;

      CompoundNBT compoundnbt1;
      try {
         CompoundNBT compoundnbt;
         if (this.isCompressed(pushbackinputstream)) {
            compoundnbt = CompressedStreamTools.readCompressed(pushbackinputstream);
         } else {
            DataInputStream datainputstream = new DataInputStream(pushbackinputstream);
            Throwable var9 = null;

            try {
               compoundnbt = CompressedStreamTools.read(datainputstream);
            } catch (Throwable var32) {
               var9 = var32;
               throw var32;
            } finally {
               if (datainputstream != null) {
                  if (var9 != null) {
                     try {
                        datainputstream.close();
                     } catch (Throwable var31) {
                        var9.addSuppressed(var31);
                     }
                  } else {
                     datainputstream.close();
                  }
               }

            }
         }

         int i = compoundnbt.contains("DataVersion", 99) ? compoundnbt.getInt("DataVersion") : 1343;
         compoundnbt1 = NBTUtil.update(this.dataFixer, DefaultTypeReferences.SAVED_DATA, compoundnbt, i, p_215755_2_);
      } catch (Throwable var34) {
         var6 = var34;
         throw var34;
      } finally {
         if (pushbackinputstream != null) {
            if (var6 != null) {
               try {
                  pushbackinputstream.close();
               } catch (Throwable var30) {
                  var6.addSuppressed(var30);
               }
            } else {
               pushbackinputstream.close();
            }
         }

      }

      return compoundnbt1;
   }

   private boolean isCompressed(PushbackInputStream p_215756_1_) throws IOException {
      byte[] abyte = new byte[2];
      boolean flag = false;
      int i = p_215756_1_.read(abyte, 0, 2);
      if (i == 2) {
         int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
         if (j == 35615) {
            flag = true;
         }
      }

      if (i != 0) {
         p_215756_1_.unread(abyte, 0, i);
      }

      return flag;
   }

   public void save() {
      Iterator var1 = this.savedDatum.values().iterator();

      while(var1.hasNext()) {
         WorldSavedData worldsaveddata = (WorldSavedData)var1.next();
         if (worldsaveddata != null) {
            worldsaveddata.save(this.getDataFile(worldsaveddata.getName()));
         }
      }

   }
}
