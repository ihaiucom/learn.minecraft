package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion {
   private final ICriterionInstance criterionInstance;

   public Criterion(ICriterionInstance p_i47470_1_) {
      this.criterionInstance = p_i47470_1_;
   }

   public Criterion() {
      this.criterionInstance = null;
   }

   public void serializeToNetwork(PacketBuffer p_192140_1_) {
   }

   public static Criterion criterionFromJson(JsonObject p_192145_0_, JsonDeserializationContext p_192145_1_) {
      ResourceLocation lvt_2_1_ = new ResourceLocation(JSONUtils.getString(p_192145_0_, "trigger"));
      ICriterionTrigger<?> lvt_3_1_ = CriteriaTriggers.get(lvt_2_1_);
      if (lvt_3_1_ == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + lvt_2_1_);
      } else {
         ICriterionInstance lvt_4_1_ = lvt_3_1_.deserializeInstance(JSONUtils.getJsonObject(p_192145_0_, "conditions", new JsonObject()), p_192145_1_);
         return new Criterion(lvt_4_1_);
      }
   }

   public static Criterion criterionFromNetwork(PacketBuffer p_192146_0_) {
      return new Criterion();
   }

   public static Map<String, Criterion> criteriaFromJson(JsonObject p_192144_0_, JsonDeserializationContext p_192144_1_) {
      Map<String, Criterion> lvt_2_1_ = Maps.newHashMap();
      Iterator var3 = p_192144_0_.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<String, JsonElement> lvt_4_1_ = (Entry)var3.next();
         lvt_2_1_.put(lvt_4_1_.getKey(), criterionFromJson(JSONUtils.getJsonObject((JsonElement)lvt_4_1_.getValue(), "criterion"), p_192144_1_));
      }

      return lvt_2_1_;
   }

   public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer p_192142_0_) {
      Map<String, Criterion> lvt_1_1_ = Maps.newHashMap();
      int lvt_2_1_ = p_192142_0_.readVarInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         lvt_1_1_.put(p_192142_0_.readString(32767), criterionFromNetwork(p_192142_0_));
      }

      return lvt_1_1_;
   }

   public static void serializeToNetwork(Map<String, Criterion> p_192141_0_, PacketBuffer p_192141_1_) {
      p_192141_1_.writeVarInt(p_192141_0_.size());
      Iterator var2 = p_192141_0_.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, Criterion> lvt_3_1_ = (Entry)var2.next();
         p_192141_1_.writeString((String)lvt_3_1_.getKey());
         ((Criterion)lvt_3_1_.getValue()).serializeToNetwork(p_192141_1_);
      }

   }

   @Nullable
   public ICriterionInstance getCriterionInstance() {
      return this.criterionInstance;
   }

   public JsonElement serialize() {
      JsonObject lvt_1_1_ = new JsonObject();
      lvt_1_1_.addProperty("trigger", this.criterionInstance.getId().toString());
      lvt_1_1_.add("conditions", this.criterionInstance.serialize());
      return lvt_1_1_;
   }
}
