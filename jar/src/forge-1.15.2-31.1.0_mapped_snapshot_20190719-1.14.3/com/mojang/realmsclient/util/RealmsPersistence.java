package com.mojang.realmsclient.util;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class RealmsPersistence {
   public static RealmsPersistence.RealmsPersistenceData func_225188_a() {
      File lvt_0_1_ = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson lvt_1_1_ = new Gson();

      try {
         return (RealmsPersistence.RealmsPersistenceData)lvt_1_1_.fromJson(FileUtils.readFileToString(lvt_0_1_), RealmsPersistence.RealmsPersistenceData.class);
      } catch (IOException var3) {
         return new RealmsPersistence.RealmsPersistenceData();
      }
   }

   public static void func_225187_a(RealmsPersistence.RealmsPersistenceData p_225187_0_) {
      File lvt_1_1_ = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson lvt_2_1_ = new Gson();
      String lvt_3_1_ = lvt_2_1_.toJson(p_225187_0_);

      try {
         FileUtils.writeStringToFile(lvt_1_1_, lvt_3_1_);
      } catch (IOException var5) {
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsPersistenceData {
      public String field_225185_a;
      public boolean field_225186_b;

      private RealmsPersistenceData() {
      }

      // $FF: synthetic method
      RealmsPersistenceData(Object p_i51723_1_) {
         this();
      }
   }
}
