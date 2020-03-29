package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;
   private PropertyMap properties;

   public Session(String p_i1098_1_, String p_i1098_2_, String p_i1098_3_, String p_i1098_4_) {
      if (p_i1098_1_ == null || p_i1098_1_.isEmpty()) {
         p_i1098_1_ = "MissingName";
         p_i1098_3_ = "NotValid";
         p_i1098_2_ = "NotValid";
         Logger logger = LogManager.getLogger(this.getClass().getName());
         logger.log(Level.WARN, "=========================================================");
         logger.log(Level.WARN, "WARNING!! the username was not set for this session, typically");
         logger.log(Level.WARN, "this means you installed Forge incorrectly. We have set your");
         logger.log(Level.WARN, "name to \"MissingName\" and your session to nothing. Please");
         logger.log(Level.WARN, "check your installation and post a console log from the launcher");
         logger.log(Level.WARN, "when asking for help!");
         logger.log(Level.WARN, "=========================================================");
      }

      this.username = p_i1098_1_;
      this.playerID = p_i1098_2_;
      this.token = p_i1098_3_;
      this.sessionType = Session.Type.setSessionType(p_i1098_4_);
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public String getUsername() {
      return this.username;
   }

   public String getToken() {
      return this.token;
   }

   public GameProfile getProfile() {
      try {
         UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
         GameProfile ret = new GameProfile(uuid, this.getUsername());
         if (this.properties != null) {
            ret.getProperties().putAll(this.properties);
         }

         return ret;
      } catch (IllegalArgumentException var3) {
         return new GameProfile((UUID)null, this.getUsername());
      }
   }

   public void setProperties(PropertyMap p_setProperties_1_) {
      if (this.properties == null) {
         this.properties = p_setProperties_1_;
      }

   }

   public boolean hasCachedProperties() {
      return this.properties != null;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> SESSION_TYPES = (Map)Arrays.stream(values()).collect(Collectors.toMap((p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_.sessionType;
      }, Function.identity()));
      private final String sessionType;

      private Type(String p_i1096_3_) {
         this.sessionType = p_i1096_3_;
      }

      @Nullable
      public static Session.Type setSessionType(String p_152421_0_) {
         return (Session.Type)SESSION_TYPES.get(p_152421_0_.toLowerCase(Locale.ROOT));
      }
   }
}
