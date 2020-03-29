package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class OpList extends UserList<GameProfile, OpEntry> {
   public OpList(File p_i1152_1_) {
      super(p_i1152_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new OpEntry(p_152682_1_);
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

   public boolean bypassesPlayerLimit(GameProfile p_183026_1_) {
      OpEntry lvt_2_1_ = (OpEntry)this.getEntry(p_183026_1_);
      return lvt_2_1_ != null ? lvt_2_1_.bypassesPlayerLimit() : false;
   }

   protected String getObjectKey(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }

   // $FF: synthetic method
   protected String getObjectKey(Object p_152681_1_) {
      return this.getObjectKey((GameProfile)p_152681_1_);
   }
}
