package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ProfileBanEntry extends BanEntry<GameProfile> {
   public ProfileBanEntry(GameProfile p_i1134_1_) {
      this(p_i1134_1_, (Date)null, (String)null, (Date)null, (String)null);
   }

   public ProfileBanEntry(GameProfile p_i1135_1_, @Nullable Date p_i1135_2_, @Nullable String p_i1135_3_, @Nullable Date p_i1135_4_, @Nullable String p_i1135_5_) {
      super(p_i1135_1_, p_i1135_2_, p_i1135_3_, p_i1135_4_, p_i1135_5_);
   }

   public ProfileBanEntry(JsonObject p_i1136_1_) {
      super(toGameProfile(p_i1136_1_), p_i1136_1_);
   }

   protected void onSerialization(JsonObject p_152641_1_) {
      if (this.getValue() != null) {
         p_152641_1_.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
         p_152641_1_.addProperty("name", ((GameProfile)this.getValue()).getName());
         super.onSerialization(p_152641_1_);
      }
   }

   public ITextComponent getDisplayName() {
      GameProfile lvt_1_1_ = (GameProfile)this.getValue();
      return new StringTextComponent(lvt_1_1_.getName() != null ? lvt_1_1_.getName() : Objects.toString(lvt_1_1_.getId(), "(Unknown)"));
   }

   private static GameProfile toGameProfile(JsonObject p_152648_0_) {
      if (p_152648_0_.has("uuid") && p_152648_0_.has("name")) {
         String lvt_1_1_ = p_152648_0_.get("uuid").getAsString();

         UUID lvt_2_2_;
         try {
            lvt_2_2_ = UUID.fromString(lvt_1_1_);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(lvt_2_2_, p_152648_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
