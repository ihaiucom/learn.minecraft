package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class WhiteList extends UserList<GameProfile, WhitelistEntry> {
   public WhiteList(File p_i1132_1_) {
      super(p_i1132_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new WhitelistEntry(p_152682_1_);
   }

   public boolean isWhitelisted(GameProfile p_152705_1_) {
      return this.hasEntry(p_152705_1_);
   }

   public String[] getKeys() {
      String[] lvt_1_1_ = new String[this.getEntries().size()];
      int lvt_2_1_ = 0;

      UserListEntry lvt_4_1_;
      for(Iterator var3 = this.getEntries().iterator(); var3.hasNext(); lvt_1_1_[lvt_2_1_++] = ((GameProfile)lvt_4_1_.getValue()).getName()) {
         lvt_4_1_ = (UserListEntry)var3.next();
      }

      return lvt_1_1_;
   }

   protected String getObjectKey(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }

   // $FF: synthetic method
   protected String getObjectKey(Object p_152681_1_) {
      return this.getObjectKey((GameProfile)p_152681_1_);
   }
}
