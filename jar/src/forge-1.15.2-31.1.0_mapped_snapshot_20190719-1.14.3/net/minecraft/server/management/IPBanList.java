package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class IPBanList extends UserList<String, IPBanEntry> {
   public IPBanList(File p_i1490_1_) {
      super(p_i1490_1_);
   }

   protected UserListEntry<String> createEntry(JsonObject p_152682_1_) {
      return new IPBanEntry(p_152682_1_);
   }

   public boolean isBanned(SocketAddress p_152708_1_) {
      String lvt_2_1_ = this.addressToString(p_152708_1_);
      return this.hasEntry(lvt_2_1_);
   }

   public boolean isBanned(String p_199044_1_) {
      return this.hasEntry(p_199044_1_);
   }

   public IPBanEntry getBanEntry(SocketAddress p_152709_1_) {
      String lvt_2_1_ = this.addressToString(p_152709_1_);
      return (IPBanEntry)this.getEntry(lvt_2_1_);
   }

   private String addressToString(SocketAddress p_152707_1_) {
      String lvt_2_1_ = p_152707_1_.toString();
      if (lvt_2_1_.contains("/")) {
         lvt_2_1_ = lvt_2_1_.substring(lvt_2_1_.indexOf(47) + 1);
      }

      if (lvt_2_1_.contains(":")) {
         lvt_2_1_ = lvt_2_1_.substring(0, lvt_2_1_.indexOf(58));
      }

      return lvt_2_1_;
   }
}
