package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class WhitelistEntry extends UserListEntry<GameProfile> {
   public WhitelistEntry(GameProfile p_i1129_1_) {
      super(p_i1129_1_);
   }

   public WhitelistEntry(JsonObject p_i1130_1_) {
      super(gameProfileFromJsonObject(p_i1130_1_), p_i1130_1_);
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      if (this.getValue() != null) {
         p_152641_1_.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
         p_152641_1_.addProperty("name", ((GameProfile)this.getValue()).getName());
         super.onSerialization(p_152641_1_);
      }
   }

   private static GameProfile gameProfileFromJsonObject(JsonObject p_152646_0_) {
      if (p_152646_0_.has("uuid") && p_152646_0_.has("name")) {
         String lvt_1_1_ = p_152646_0_.get("uuid").getAsString();

         UUID lvt_2_2_;
         try {
            lvt_2_2_ = UUID.fromString(lvt_1_1_);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(lvt_2_2_, p_152646_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
