package net.minecraft.server.management;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final Gson gson;
   private final File saveFile;
   private final Map<String, V> values = Maps.newHashMap();
   private boolean lanServer = true;
   private static final ParameterizedType USER_LIST_ENTRY_TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{UserListEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public UserList(File p_i1144_1_) {
      this.saveFile = p_i1144_1_;
      GsonBuilder lvt_2_1_ = (new GsonBuilder()).setPrettyPrinting();
      lvt_2_1_.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
      this.gson = lvt_2_1_.create();
   }

   public boolean isLanServer() {
      return this.lanServer;
   }

   public void setLanServer(boolean p_152686_1_) {
      this.lanServer = p_152686_1_;
   }

   public File getSaveFile() {
      return this.saveFile;
   }

   public void addEntry(V p_152687_1_) {
      this.values.put(this.getObjectKey(p_152687_1_.getValue()), p_152687_1_);

      try {
         this.writeChanges();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after adding a user.", var3);
      }

   }

   @Nullable
   public V getEntry(K p_152683_1_) {
      this.removeExpired();
      return (UserListEntry)this.values.get(this.getObjectKey(p_152683_1_));
   }

   public void removeEntry(K p_152684_1_) {
      this.values.remove(this.getObjectKey(p_152684_1_));

      try {
         this.writeChanges();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after removing a user.", var3);
      }

   }

   public void removeEntry(UserListEntry<K> p_199042_1_) {
      this.removeEntry(p_199042_1_.getValue());
   }

   public String[] getKeys() {
      return (String[])this.values.keySet().toArray(new String[this.values.size()]);
   }

   public boolean isEmpty() {
      return this.values.size() < 1;
   }

   protected String getObjectKey(K p_152681_1_) {
      return p_152681_1_.toString();
   }

   protected boolean hasEntry(K p_152692_1_) {
      return this.values.containsKey(this.getObjectKey(p_152692_1_));
   }

   private void removeExpired() {
      List<K> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.values.values().iterator();

      while(var2.hasNext()) {
         V lvt_3_1_ = (UserListEntry)var2.next();
         if (lvt_3_1_.hasBanExpired()) {
            lvt_1_1_.add(lvt_3_1_.getValue());
         }
      }

      var2 = lvt_1_1_.iterator();

      while(var2.hasNext()) {
         K lvt_3_2_ = var2.next();
         this.values.remove(this.getObjectKey(lvt_3_2_));
      }

   }

   protected UserListEntry<K> createEntry(JsonObject p_152682_1_) {
      return new UserListEntry((Object)null, p_152682_1_);
   }

   public Collection<V> getEntries() {
      return this.values.values();
   }

   public void writeChanges() throws IOException {
      Collection<V> lvt_1_1_ = this.values.values();
      String lvt_2_1_ = this.gson.toJson(lvt_1_1_);
      BufferedWriter lvt_3_1_ = null;

      try {
         lvt_3_1_ = Files.newWriter(this.saveFile, StandardCharsets.UTF_8);
         lvt_3_1_.write(lvt_2_1_);
      } finally {
         IOUtils.closeQuietly(lvt_3_1_);
      }

   }

   public void readSavedFile() throws FileNotFoundException {
      if (this.saveFile.exists()) {
         BufferedReader lvt_1_1_ = null;

         try {
            lvt_1_1_ = Files.newReader(this.saveFile, StandardCharsets.UTF_8);
            Collection<UserListEntry<K>> lvt_2_1_ = (Collection)JSONUtils.fromJson(this.gson, (Reader)lvt_1_1_, (Type)USER_LIST_ENTRY_TYPE);
            if (lvt_2_1_ != null) {
               this.values.clear();
               Iterator var3 = lvt_2_1_.iterator();

               while(var3.hasNext()) {
                  UserListEntry<K> lvt_4_1_ = (UserListEntry)var3.next();
                  if (lvt_4_1_.getValue() != null) {
                     this.values.put(this.getObjectKey(lvt_4_1_.getValue()), lvt_4_1_);
                  }
               }
            }
         } finally {
            IOUtils.closeQuietly(lvt_1_1_);
         }

      }
   }

   class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
      private Serializer() {
      }

      public JsonElement serialize(UserListEntry<K> p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         p_serialize_1_.onSerialization(lvt_4_1_);
         return lvt_4_1_;
      }

      public UserListEntry<K> deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            return UserList.this.createEntry(lvt_4_1_);
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((UserListEntry)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }

      // $FF: synthetic method
      Serializer(Object p_i1141_2_) {
         this();
      }
   }
}
