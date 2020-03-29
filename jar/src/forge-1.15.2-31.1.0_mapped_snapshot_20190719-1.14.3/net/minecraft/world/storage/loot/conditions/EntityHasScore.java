package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.RandomValueRange;

public class EntityHasScore implements ILootCondition {
   private final Map<String, RandomValueRange> scores;
   private final LootContext.EntityTarget target;

   private EntityHasScore(Map<String, RandomValueRange> p_i46618_1_, LootContext.EntityTarget p_i46618_2_) {
      this.scores = ImmutableMap.copyOf(p_i46618_1_);
      this.target = p_i46618_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.target.getParameter());
   }

   public boolean test(LootContext p_test_1_) {
      Entity lvt_2_1_ = (Entity)p_test_1_.get(this.target.getParameter());
      if (lvt_2_1_ == null) {
         return false;
      } else {
         Scoreboard lvt_3_1_ = lvt_2_1_.world.getScoreboard();
         Iterator var4 = this.scores.entrySet().iterator();

         Entry lvt_5_1_;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            lvt_5_1_ = (Entry)var4.next();
         } while(this.entityScoreMatch(lvt_2_1_, lvt_3_1_, (String)lvt_5_1_.getKey(), (RandomValueRange)lvt_5_1_.getValue()));

         return false;
      }
   }

   protected boolean entityScoreMatch(Entity p_186631_1_, Scoreboard p_186631_2_, String p_186631_3_, RandomValueRange p_186631_4_) {
      ScoreObjective lvt_5_1_ = p_186631_2_.getObjective(p_186631_3_);
      if (lvt_5_1_ == null) {
         return false;
      } else {
         String lvt_6_1_ = p_186631_1_.getScoreboardName();
         return !p_186631_2_.entityHasObjective(lvt_6_1_, lvt_5_1_) ? false : p_186631_4_.isInRange(p_186631_2_.getOrCreateScore(lvt_6_1_, lvt_5_1_).getScorePoints());
      }
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   EntityHasScore(Map p_i51204_1_, LootContext.EntityTarget p_i51204_2_, Object p_i51204_3_) {
      this(p_i51204_1_, p_i51204_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<EntityHasScore> {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScore.class);
      }

      public void serialize(JsonObject p_186605_1_, EntityHasScore p_186605_2_, JsonSerializationContext p_186605_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         Iterator var5 = p_186605_2_.scores.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<String, RandomValueRange> lvt_6_1_ = (Entry)var5.next();
            lvt_4_1_.add((String)lvt_6_1_.getKey(), p_186605_3_.serialize(lvt_6_1_.getValue()));
         }

         p_186605_1_.add("scores", lvt_4_1_);
         p_186605_1_.add("entity", p_186605_3_.serialize(p_186605_2_.target));
      }

      public EntityHasScore deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         Set<Entry<String, JsonElement>> lvt_3_1_ = JSONUtils.getJsonObject(p_186603_1_, "scores").entrySet();
         Map<String, RandomValueRange> lvt_4_1_ = Maps.newLinkedHashMap();
         Iterator var5 = lvt_3_1_.iterator();

         while(var5.hasNext()) {
            Entry<String, JsonElement> lvt_6_1_ = (Entry)var5.next();
            lvt_4_1_.put(lvt_6_1_.getKey(), JSONUtils.deserializeClass((JsonElement)lvt_6_1_.getValue(), "score", p_186603_2_, RandomValueRange.class));
         }

         return new EntityHasScore(lvt_4_1_, (LootContext.EntityTarget)JSONUtils.deserializeClass(p_186603_1_, "entity", p_186603_2_, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
