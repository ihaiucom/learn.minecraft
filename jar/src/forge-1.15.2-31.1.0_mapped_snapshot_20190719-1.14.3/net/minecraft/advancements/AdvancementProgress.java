package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   private final Map<String, CriterionProgress> criteria = Maps.newHashMap();
   private String[][] requirements = new String[0][];

   public void update(Map<String, Criterion> p_192099_1_, String[][] p_192099_2_) {
      Set<String> lvt_3_1_ = p_192099_1_.keySet();
      this.criteria.entrySet().removeIf((p_209539_1_) -> {
         return !lvt_3_1_.contains(p_209539_1_.getKey());
      });
      Iterator var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         String lvt_5_1_ = (String)var4.next();
         if (!this.criteria.containsKey(lvt_5_1_)) {
            this.criteria.put(lvt_5_1_, new CriterionProgress());
         }
      }

      this.requirements = p_192099_2_;
   }

   public boolean isDone() {
      if (this.requirements.length == 0) {
         return false;
      } else {
         String[][] var1 = this.requirements;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String[] lvt_4_1_ = var1[var3];
            boolean lvt_5_1_ = false;
            String[] var6 = lvt_4_1_;
            int var7 = lvt_4_1_.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String lvt_9_1_ = var6[var8];
               CriterionProgress lvt_10_1_ = this.getCriterionProgress(lvt_9_1_);
               if (lvt_10_1_ != null && lvt_10_1_.isObtained()) {
                  lvt_5_1_ = true;
                  break;
               }
            }

            if (!lvt_5_1_) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasProgress() {
      Iterator var1 = this.criteria.values().iterator();

      CriterionProgress lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         lvt_2_1_ = (CriterionProgress)var1.next();
      } while(!lvt_2_1_.isObtained());

      return true;
   }

   public boolean grantCriterion(String p_192109_1_) {
      CriterionProgress lvt_2_1_ = (CriterionProgress)this.criteria.get(p_192109_1_);
      if (lvt_2_1_ != null && !lvt_2_1_.isObtained()) {
         lvt_2_1_.obtain();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeCriterion(String p_192101_1_) {
      CriterionProgress lvt_2_1_ = (CriterionProgress)this.criteria.get(p_192101_1_);
      if (lvt_2_1_ != null && lvt_2_1_.isObtained()) {
         lvt_2_1_.reset();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public void serializeToNetwork(PacketBuffer p_192104_1_) {
      p_192104_1_.writeVarInt(this.criteria.size());
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, CriterionProgress> lvt_3_1_ = (Entry)var2.next();
         p_192104_1_.writeString((String)lvt_3_1_.getKey());
         ((CriterionProgress)lvt_3_1_.getValue()).write(p_192104_1_);
      }

   }

   public static AdvancementProgress fromNetwork(PacketBuffer p_192100_0_) {
      AdvancementProgress lvt_1_1_ = new AdvancementProgress();
      int lvt_2_1_ = p_192100_0_.readVarInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         lvt_1_1_.criteria.put(p_192100_0_.readString(32767), CriterionProgress.read(p_192100_0_));
      }

      return lvt_1_1_;
   }

   @Nullable
   public CriterionProgress getCriterionProgress(String p_192106_1_) {
      return (CriterionProgress)this.criteria.get(p_192106_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float lvt_1_1_ = (float)this.requirements.length;
         float lvt_2_1_ = (float)this.countCompletedRequirements();
         return lvt_2_1_ / lvt_1_1_;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int lvt_1_1_ = this.requirements.length;
         if (lvt_1_1_ <= 1) {
            return null;
         } else {
            int lvt_2_1_ = this.countCompletedRequirements();
            return lvt_2_1_ + "/" + lvt_1_1_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private int countCompletedRequirements() {
      int lvt_1_1_ = 0;
      String[][] var2 = this.requirements;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String[] lvt_5_1_ = var2[var4];
         boolean lvt_6_1_ = false;
         String[] var7 = lvt_5_1_;
         int var8 = lvt_5_1_.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String lvt_10_1_ = var7[var9];
            CriterionProgress lvt_11_1_ = this.getCriterionProgress(lvt_10_1_);
            if (lvt_11_1_ != null && lvt_11_1_.isObtained()) {
               lvt_6_1_ = true;
               break;
            }
         }

         if (lvt_6_1_) {
            ++lvt_1_1_;
         }
      }

      return lvt_1_1_;
   }

   public Iterable<String> getRemaningCriteria() {
      List<String> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, CriterionProgress> lvt_3_1_ = (Entry)var2.next();
         if (!((CriterionProgress)lvt_3_1_.getValue()).isObtained()) {
            lvt_1_1_.add(lvt_3_1_.getKey());
         }
      }

      return lvt_1_1_;
   }

   public Iterable<String> getCompletedCriteria() {
      List<String> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, CriterionProgress> lvt_3_1_ = (Entry)var2.next();
         if (((CriterionProgress)lvt_3_1_.getValue()).isObtained()) {
            lvt_1_1_.add(lvt_3_1_.getKey());
         }
      }

      return lvt_1_1_;
   }

   @Nullable
   public Date getFirstProgressDate() {
      Date lvt_1_1_ = null;
      Iterator var2 = this.criteria.values().iterator();

      while(true) {
         CriterionProgress lvt_3_1_;
         do {
            do {
               if (!var2.hasNext()) {
                  return lvt_1_1_;
               }

               lvt_3_1_ = (CriterionProgress)var2.next();
            } while(!lvt_3_1_.isObtained());
         } while(lvt_1_1_ != null && !lvt_3_1_.getObtained().before(lvt_1_1_));

         lvt_1_1_ = lvt_3_1_.getObtained();
      }
   }

   public int compareTo(AdvancementProgress p_compareTo_1_) {
      Date lvt_2_1_ = this.getFirstProgressDate();
      Date lvt_3_1_ = p_compareTo_1_.getFirstProgressDate();
      if (lvt_2_1_ == null && lvt_3_1_ != null) {
         return 1;
      } else if (lvt_2_1_ != null && lvt_3_1_ == null) {
         return -1;
      } else {
         return lvt_2_1_ == null && lvt_3_1_ == null ? 0 : lvt_2_1_.compareTo(lvt_3_1_);
      }
   }

   // $FF: synthetic method
   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((AdvancementProgress)p_compareTo_1_);
   }

   public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
      public JsonElement serialize(AdvancementProgress p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         JsonObject lvt_5_1_ = new JsonObject();
         Iterator var6 = p_serialize_1_.criteria.entrySet().iterator();

         while(var6.hasNext()) {
            Entry<String, CriterionProgress> lvt_7_1_ = (Entry)var6.next();
            CriterionProgress lvt_8_1_ = (CriterionProgress)lvt_7_1_.getValue();
            if (lvt_8_1_.isObtained()) {
               lvt_5_1_.add((String)lvt_7_1_.getKey(), lvt_8_1_.serialize());
            }
         }

         if (!lvt_5_1_.entrySet().isEmpty()) {
            lvt_4_1_.add("criteria", lvt_5_1_);
         }

         lvt_4_1_.addProperty("done", p_serialize_1_.isDone());
         return lvt_4_1_;
      }

      public AdvancementProgress deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "advancement");
         JsonObject lvt_5_1_ = JSONUtils.getJsonObject(lvt_4_1_, "criteria", new JsonObject());
         AdvancementProgress lvt_6_1_ = new AdvancementProgress();
         Iterator var7 = lvt_5_1_.entrySet().iterator();

         while(var7.hasNext()) {
            Entry<String, JsonElement> lvt_8_1_ = (Entry)var7.next();
            String lvt_9_1_ = (String)lvt_8_1_.getKey();
            lvt_6_1_.criteria.put(lvt_9_1_, CriterionProgress.fromJson(JSONUtils.getString((JsonElement)lvt_8_1_.getValue(), lvt_9_1_)));
         }

         return lvt_6_1_;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((AdvancementProgress)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }
   }
}
