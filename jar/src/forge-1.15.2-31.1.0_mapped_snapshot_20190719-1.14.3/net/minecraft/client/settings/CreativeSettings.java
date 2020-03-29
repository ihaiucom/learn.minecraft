package net.minecraft.client.settings;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreativeSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File dataFile;
   private final DataFixer dataFixer;
   private final HotbarSnapshot[] hotbarSnapshots = new HotbarSnapshot[9];
   private boolean loaded;

   public CreativeSettings(File p_i49702_1_, DataFixer p_i49702_2_) {
      this.dataFile = new File(p_i49702_1_, "hotbar.nbt");
      this.dataFixer = p_i49702_2_;

      for(int lvt_3_1_ = 0; lvt_3_1_ < 9; ++lvt_3_1_) {
         this.hotbarSnapshots[lvt_3_1_] = new HotbarSnapshot();
      }

   }

   private void load() {
      try {
         CompoundNBT lvt_1_1_ = CompressedStreamTools.read(this.dataFile);
         if (lvt_1_1_ == null) {
            return;
         }

         if (!lvt_1_1_.contains("DataVersion", 99)) {
            lvt_1_1_.putInt("DataVersion", 1343);
         }

         lvt_1_1_ = NBTUtil.update(this.dataFixer, DefaultTypeReferences.HOTBAR, lvt_1_1_, lvt_1_1_.getInt("DataVersion"));

         for(int lvt_2_1_ = 0; lvt_2_1_ < 9; ++lvt_2_1_) {
            this.hotbarSnapshots[lvt_2_1_].fromTag(lvt_1_1_.getList(String.valueOf(lvt_2_1_), 10));
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to load creative mode options", var3);
      }

   }

   public void save() {
      try {
         CompoundNBT lvt_1_1_ = new CompoundNBT();
         lvt_1_1_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());

         for(int lvt_2_1_ = 0; lvt_2_1_ < 9; ++lvt_2_1_) {
            lvt_1_1_.put(String.valueOf(lvt_2_1_), this.getHotbarSnapshot(lvt_2_1_).createTag());
         }

         CompressedStreamTools.write(lvt_1_1_, this.dataFile);
      } catch (Exception var3) {
         LOGGER.error("Failed to save creative mode options", var3);
      }

   }

   public HotbarSnapshot getHotbarSnapshot(int p_192563_1_) {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      return this.hotbarSnapshots[p_192563_1_];
   }
}
