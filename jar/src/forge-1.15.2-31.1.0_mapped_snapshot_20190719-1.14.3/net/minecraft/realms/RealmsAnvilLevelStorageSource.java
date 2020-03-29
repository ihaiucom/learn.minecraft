package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsAnvilLevelStorageSource {
   private final SaveFormat levelStorageSource;

   public RealmsAnvilLevelStorageSource(SaveFormat p_i1106_1_) {
      this.levelStorageSource = p_i1106_1_;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean levelExists(String p_levelExists_1_) {
      return this.levelStorageSource.canLoadWorld(p_levelExists_1_);
   }

   public boolean convertLevel(String p_convertLevel_1_, IProgressUpdate p_convertLevel_2_) {
      return this.levelStorageSource.convertMapFormat(p_convertLevel_1_, p_convertLevel_2_);
   }

   public boolean requiresConversion(String p_requiresConversion_1_) {
      return this.levelStorageSource.isOldMapFormat(p_requiresConversion_1_);
   }

   public boolean isNewLevelIdAcceptable(String p_isNewLevelIdAcceptable_1_) {
      return this.levelStorageSource.isNewLevelIdAcceptable(p_isNewLevelIdAcceptable_1_);
   }

   public boolean deleteLevel(String p_deleteLevel_1_) {
      return this.levelStorageSource.deleteWorldDirectory(p_deleteLevel_1_);
   }

   public void renameLevel(String p_renameLevel_1_, String p_renameLevel_2_) {
      this.levelStorageSource.renameWorld(p_renameLevel_1_, p_renameLevel_2_);
   }

   public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
      List<RealmsLevelSummary> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.levelStorageSource.getSaveList().iterator();

      while(var2.hasNext()) {
         WorldSummary lvt_3_1_ = (WorldSummary)var2.next();
         lvt_1_1_.add(new RealmsLevelSummary(lvt_3_1_));
      }

      return lvt_1_1_;
   }
}
