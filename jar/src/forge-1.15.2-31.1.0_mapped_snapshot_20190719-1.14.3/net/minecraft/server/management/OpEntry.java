package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class OpEntry extends UserListEntry<GameProfile> {
   private final int permissionLevel;
   private final boolean bypassesPlayerLimit;

   public OpEntry(GameProfile p_i46492_1_, int p_i46492_2_, boolean p_i46492_3_) {
      super(p_i46492_1_);
      this.permissionLevel = p_i46492_2_;
      this.bypassesPlayerLimit = p_i46492_3_;
   }

   public OpEntry(JsonObject p_i1150_1_) {
      super(constructProfile(p_i1150_1_), p_i1150_1_);
      this.permissionLevel = p_i1150_1_.has("level") ? p_i1150_1_.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = p_i1150_1_.has("bypassesPlayerLimit") && p_i1150_1_.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getPermissionLevel() {
      return this.permissionLevel;
   }

   public boolean bypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      if (this.getValue() != null) {
         p_152641_1_.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
         p_152641_1_.addProperty("name", ((GameProfile)this.getValue()).getName());
         super.onSerialization(p_152641_1_);
         p_152641_1_.addProperty("level", this.permissionLevel);
         p_152641_1_.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }

   private static GameProfile constructProfile(JsonObject p_152643_0_) {
      if (p_152643_0_.has("uuid") && p_152643_0_.has("name")) {
         String lvt_1_1_ = p_152643_0_.get("uuid").getAsString();

         UUID lvt_2_2_;
         try {
            lvt_2_2_ = UUID.fromString(lvt_1_1_);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(lvt_2_2_, p_152643_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
