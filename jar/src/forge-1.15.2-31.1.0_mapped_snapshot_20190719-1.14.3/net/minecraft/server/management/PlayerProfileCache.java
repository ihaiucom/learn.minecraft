package net.minecraft.server.management;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private static boolean onlineMode;
   private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
   private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
   private final Deque<GameProfile> gameProfiles = Lists.newLinkedList();
   private final GameProfileRepository profileRepo;
   protected final Gson gson;
   private final File usercacheFile;
   private static final ParameterizedType TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerProfileCache.ProfileEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public PlayerProfileCache(GameProfileRepository p_i46836_1_, File p_i46836_2_) {
      this.profileRepo = p_i46836_1_;
      this.usercacheFile = p_i46836_2_;
      GsonBuilder lvt_3_1_ = new GsonBuilder();
      lvt_3_1_.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.gson = lvt_3_1_.create();
      this.load();
   }

   private static GameProfile lookupProfile(GameProfileRepository p_187319_0_, String p_187319_1_) {
      final GameProfile[] lvt_2_1_ = new GameProfile[1];
      ProfileLookupCallback lvt_3_1_ = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
            lvt_2_1_[0] = p_onProfileLookupSucceeded_1_;
         }

         public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
            lvt_2_1_[0] = null;
         }
      };
      p_187319_0_.findProfilesByNames(new String[]{p_187319_1_}, Agent.MINECRAFT, lvt_3_1_);
      if (!isOnlineMode() && lvt_2_1_[0] == null) {
         UUID lvt_4_1_ = PlayerEntity.getUUID(new GameProfile((UUID)null, p_187319_1_));
         GameProfile lvt_5_1_ = new GameProfile(lvt_4_1_, p_187319_1_);
         lvt_3_1_.onProfileLookupSucceeded(lvt_5_1_);
      }

      return lvt_2_1_[0];
   }

   public static void setOnlineMode(boolean p_187320_0_) {
      onlineMode = p_187320_0_;
   }

   private static boolean isOnlineMode() {
      return onlineMode;
   }

   public void addEntry(GameProfile p_152649_1_) {
      this.addEntry(p_152649_1_, (Date)null);
   }

   private void addEntry(GameProfile p_152651_1_, Date p_152651_2_) {
      UUID lvt_3_1_ = p_152651_1_.getId();
      if (p_152651_2_ == null) {
         Calendar lvt_4_1_ = Calendar.getInstance();
         lvt_4_1_.setTime(new Date());
         lvt_4_1_.add(2, 1);
         p_152651_2_ = lvt_4_1_.getTime();
      }

      PlayerProfileCache.ProfileEntry lvt_4_2_ = new PlayerProfileCache.ProfileEntry(p_152651_1_, p_152651_2_);
      if (this.uuidToProfileEntryMap.containsKey(lvt_3_1_)) {
         PlayerProfileCache.ProfileEntry lvt_5_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(lvt_3_1_);
         this.usernameToProfileEntryMap.remove(lvt_5_1_.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(p_152651_1_);
      }

      this.usernameToProfileEntryMap.put(p_152651_1_.getName().toLowerCase(Locale.ROOT), lvt_4_2_);
      this.uuidToProfileEntryMap.put(lvt_3_1_, lvt_4_2_);
      this.gameProfiles.addFirst(p_152651_1_);
      this.save();
   }

   @Nullable
   public GameProfile getGameProfileForUsername(String p_152655_1_) {
      String lvt_2_1_ = p_152655_1_.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry lvt_3_1_ = (PlayerProfileCache.ProfileEntry)this.usernameToProfileEntryMap.get(lvt_2_1_);
      if (lvt_3_1_ != null && (new Date()).getTime() >= lvt_3_1_.expirationDate.getTime()) {
         this.uuidToProfileEntryMap.remove(lvt_3_1_.getGameProfile().getId());
         this.usernameToProfileEntryMap.remove(lvt_3_1_.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(lvt_3_1_.getGameProfile());
         lvt_3_1_ = null;
      }

      GameProfile lvt_4_2_;
      if (lvt_3_1_ != null) {
         lvt_4_2_ = lvt_3_1_.getGameProfile();
         this.gameProfiles.remove(lvt_4_2_);
         this.gameProfiles.addFirst(lvt_4_2_);
      } else {
         lvt_4_2_ = lookupProfile(this.profileRepo, lvt_2_1_);
         if (lvt_4_2_ != null) {
            this.addEntry(lvt_4_2_);
            lvt_3_1_ = (PlayerProfileCache.ProfileEntry)this.usernameToProfileEntryMap.get(lvt_2_1_);
         }
      }

      this.save();
      return lvt_3_1_ == null ? null : lvt_3_1_.getGameProfile();
   }

   @Nullable
   public GameProfile getProfileByUUID(UUID p_152652_1_) {
      PlayerProfileCache.ProfileEntry lvt_2_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(p_152652_1_);
      return lvt_2_1_ == null ? null : lvt_2_1_.getGameProfile();
   }

   private PlayerProfileCache.ProfileEntry getByUUID(UUID p_152653_1_) {
      PlayerProfileCache.ProfileEntry lvt_2_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(p_152653_1_);
      if (lvt_2_1_ != null) {
         GameProfile lvt_3_1_ = lvt_2_1_.getGameProfile();
         this.gameProfiles.remove(lvt_3_1_);
         this.gameProfiles.addFirst(lvt_3_1_);
      }

      return lvt_2_1_;
   }

   public void load() {
      BufferedReader lvt_1_1_ = null;

      try {
         lvt_1_1_ = Files.newReader(this.usercacheFile, StandardCharsets.UTF_8);
         List<PlayerProfileCache.ProfileEntry> lvt_2_1_ = (List)JSONUtils.fromJson(this.gson, (Reader)lvt_1_1_, (Type)TYPE);
         this.usernameToProfileEntryMap.clear();
         this.uuidToProfileEntryMap.clear();
         this.gameProfiles.clear();
         if (lvt_2_1_ != null) {
            Iterator var3 = Lists.reverse(lvt_2_1_).iterator();

            while(var3.hasNext()) {
               PlayerProfileCache.ProfileEntry lvt_4_1_ = (PlayerProfileCache.ProfileEntry)var3.next();
               if (lvt_4_1_ != null) {
                  this.addEntry(lvt_4_1_.getGameProfile(), lvt_4_1_.getExpirationDate());
               }
            }
         }
      } catch (FileNotFoundException var9) {
      } catch (JsonParseException var10) {
      } finally {
         IOUtils.closeQuietly(lvt_1_1_);
      }

   }

   public void save() {
      String lvt_1_1_ = this.gson.toJson(this.getEntriesWithLimit(1000));
      BufferedWriter lvt_2_1_ = null;

      try {
         lvt_2_1_ = Files.newWriter(this.usercacheFile, StandardCharsets.UTF_8);
         lvt_2_1_.write(lvt_1_1_);
         return;
      } catch (FileNotFoundException var8) {
      } catch (IOException var9) {
         return;
      } finally {
         IOUtils.closeQuietly(lvt_2_1_);
      }

   }

   private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int p_152656_1_) {
      List<PlayerProfileCache.ProfileEntry> lvt_2_1_ = Lists.newArrayList();
      List<GameProfile> lvt_3_1_ = Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), p_152656_1_));
      Iterator var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         PlayerProfileCache.ProfileEntry lvt_6_1_ = this.getByUUID(lvt_5_1_.getId());
         if (lvt_6_1_ != null) {
            lvt_2_1_.add(lvt_6_1_);
         }
      }

      return lvt_2_1_;
   }

   class ProfileEntry {
      private final GameProfile gameProfile;
      private final Date expirationDate;

      private ProfileEntry(GameProfile p_i46333_2_, Date p_i46333_3_) {
         this.gameProfile = p_i46333_2_;
         this.expirationDate = p_i46333_3_;
      }

      public GameProfile getGameProfile() {
         return this.gameProfile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }

      // $FF: synthetic method
      ProfileEntry(GameProfile p_i1166_2_, Date p_i1166_3_, Object p_i1166_4_) {
         this(p_i1166_2_, p_i1166_3_);
      }
   }

   class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      private Serializer() {
      }

      public JsonElement serialize(PlayerProfileCache.ProfileEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         lvt_4_1_.addProperty("name", p_serialize_1_.getGameProfile().getName());
         UUID lvt_5_1_ = p_serialize_1_.getGameProfile().getId();
         lvt_4_1_.addProperty("uuid", lvt_5_1_ == null ? "" : lvt_5_1_.toString());
         lvt_4_1_.addProperty("expiresOn", PlayerProfileCache.DATE_FORMAT.format(p_serialize_1_.getExpirationDate()));
         return lvt_4_1_;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            JsonElement lvt_5_1_ = lvt_4_1_.get("name");
            JsonElement lvt_6_1_ = lvt_4_1_.get("uuid");
            JsonElement lvt_7_1_ = lvt_4_1_.get("expiresOn");
            if (lvt_5_1_ != null && lvt_6_1_ != null) {
               String lvt_8_1_ = lvt_6_1_.getAsString();
               String lvt_9_1_ = lvt_5_1_.getAsString();
               Date lvt_10_1_ = null;
               if (lvt_7_1_ != null) {
                  try {
                     lvt_10_1_ = PlayerProfileCache.DATE_FORMAT.parse(lvt_7_1_.getAsString());
                  } catch (ParseException var14) {
                     lvt_10_1_ = null;
                  }
               }

               if (lvt_9_1_ != null && lvt_8_1_ != null) {
                  UUID lvt_11_3_;
                  try {
                     lvt_11_3_ = UUID.fromString(lvt_8_1_);
                  } catch (Throwable var13) {
                     return null;
                  }

                  return PlayerProfileCache.this.new ProfileEntry(new GameProfile(lvt_11_3_, lvt_9_1_), lvt_10_1_);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((PlayerProfileCache.ProfileEntry)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }

      // $FF: synthetic method
      Serializer(Object p_i46332_2_) {
         this();
      }
   }
}
