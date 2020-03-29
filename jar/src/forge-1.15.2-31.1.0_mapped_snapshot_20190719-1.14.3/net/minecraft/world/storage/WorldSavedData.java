package net.minecraft.world.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class WorldSavedData implements INBTSerializable<CompoundNBT> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String name;
   private boolean dirty;

   public WorldSavedData(String p_i2141_1_) {
      this.name = p_i2141_1_;
   }

   public abstract void read(CompoundNBT var1);

   public abstract CompoundNBT write(CompoundNBT var1);

   public void markDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean p_76186_1_) {
      this.dirty = p_76186_1_;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public String getName() {
      return this.name;
   }

   public void save(File p_215158_1_) {
      if (this.isDirty()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.put("data", this.write(new CompoundNBT()));
         compoundnbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());

         try {
            FileOutputStream fileoutputstream = new FileOutputStream(p_215158_1_);
            Throwable var4 = null;

            try {
               CompressedStreamTools.writeCompressed(compoundnbt, fileoutputstream);
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (fileoutputstream != null) {
                  if (var4 != null) {
                     try {
                        fileoutputstream.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     fileoutputstream.close();
                  }
               }

            }
         } catch (IOException var16) {
            LOGGER.error("Could not save data {}", this, var16);
         }

         this.setDirty(false);
      }

   }

   public void deserializeNBT(CompoundNBT p_deserializeNBT_1_) {
      this.read(p_deserializeNBT_1_);
   }

   public CompoundNBT serializeNBT() {
      return this.write(new CompoundNBT());
   }
}
