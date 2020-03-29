package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;

public class PlayerPredicate {
   public static final PlayerPredicate field_226989_a_ = (new PlayerPredicate.Default()).func_227012_b_();
   private final MinMaxBounds.IntBound field_226990_b_;
   private final GameType field_226991_c_;
   private final Map<Stat<?>, MinMaxBounds.IntBound> field_226992_d_;
   private final Object2BooleanMap<ResourceLocation> field_226993_e_;
   private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> field_226994_f_;

   private static PlayerPredicate.IAdvancementPredicate func_227004_b_(JsonElement p_227004_0_) {
      if (p_227004_0_.isJsonPrimitive()) {
         boolean lvt_1_1_ = p_227004_0_.getAsBoolean();
         return new PlayerPredicate.CompletedAdvancementPredicate(lvt_1_1_);
      } else {
         Object2BooleanMap<String> lvt_1_2_ = new Object2BooleanOpenHashMap();
         JsonObject lvt_2_1_ = JSONUtils.getJsonObject(p_227004_0_, "criterion data");
         lvt_2_1_.entrySet().forEach((p_227003_1_) -> {
            boolean lvt_2_1_ = JSONUtils.getBoolean((JsonElement)p_227003_1_.getValue(), "criterion test");
            lvt_1_2_.put(p_227003_1_.getKey(), lvt_2_1_);
         });
         return new PlayerPredicate.CriteriaPredicate(lvt_1_2_);
      }
   }

   private PlayerPredicate(MinMaxBounds.IntBound p_i225770_1_, GameType p_i225770_2_, Map<Stat<?>, MinMaxBounds.IntBound> p_i225770_3_, Object2BooleanMap<ResourceLocation> p_i225770_4_, Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> p_i225770_5_) {
      this.field_226990_b_ = p_i225770_1_;
      this.field_226991_c_ = p_i225770_2_;
      this.field_226992_d_ = p_i225770_3_;
      this.field_226993_e_ = p_i225770_4_;
      this.field_226994_f_ = p_i225770_5_;
   }

   public boolean func_226998_a_(Entity p_226998_1_) {
      if (this == field_226989_a_) {
         return true;
      } else if (!(p_226998_1_ instanceof ServerPlayerEntity)) {
         return false;
      } else {
         ServerPlayerEntity lvt_2_1_ = (ServerPlayerEntity)p_226998_1_;
         if (!this.field_226990_b_.test(lvt_2_1_.experienceLevel)) {
            return false;
         } else if (this.field_226991_c_ != GameType.NOT_SET && this.field_226991_c_ != lvt_2_1_.interactionManager.getGameType()) {
            return false;
         } else {
            StatisticsManager lvt_3_1_ = lvt_2_1_.getStats();
            Iterator var4 = this.field_226992_d_.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<Stat<?>, MinMaxBounds.IntBound> lvt_5_1_ = (Entry)var4.next();
               int lvt_6_1_ = lvt_3_1_.getValue((Stat)lvt_5_1_.getKey());
               if (!((MinMaxBounds.IntBound)lvt_5_1_.getValue()).test(lvt_6_1_)) {
                  return false;
               }
            }

            RecipeBook lvt_4_1_ = lvt_2_1_.getRecipeBook();
            ObjectIterator var11 = this.field_226993_e_.object2BooleanEntrySet().iterator();

            while(var11.hasNext()) {
               it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<ResourceLocation> lvt_6_2_ = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry)var11.next();
               if (lvt_4_1_.func_226144_b_((ResourceLocation)lvt_6_2_.getKey()) != lvt_6_2_.getBooleanValue()) {
                  return false;
               }
            }

            if (!this.field_226994_f_.isEmpty()) {
               PlayerAdvancements lvt_5_2_ = lvt_2_1_.getAdvancements();
               AdvancementManager lvt_6_3_ = lvt_2_1_.getServer().getAdvancementManager();
               Iterator var7 = this.field_226994_f_.entrySet().iterator();

               while(var7.hasNext()) {
                  Entry<ResourceLocation, PlayerPredicate.IAdvancementPredicate> lvt_8_1_ = (Entry)var7.next();
                  Advancement lvt_9_1_ = lvt_6_3_.getAdvancement((ResourceLocation)lvt_8_1_.getKey());
                  if (lvt_9_1_ == null || !((PlayerPredicate.IAdvancementPredicate)lvt_8_1_.getValue()).test(lvt_5_2_.getProgress(lvt_9_1_))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public static PlayerPredicate func_227000_a_(@Nullable JsonElement p_227000_0_) {
      if (p_227000_0_ != null && !p_227000_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_227000_0_, "player");
         MinMaxBounds.IntBound lvt_2_1_ = MinMaxBounds.IntBound.fromJson(lvt_1_1_.get("level"));
         String lvt_3_1_ = JSONUtils.getString(lvt_1_1_, "gamemode", "");
         GameType lvt_4_1_ = GameType.parseGameTypeWithDefault(lvt_3_1_, GameType.NOT_SET);
         Map<Stat<?>, MinMaxBounds.IntBound> lvt_5_1_ = Maps.newHashMap();
         JsonArray lvt_6_1_ = JSONUtils.getJsonArray(lvt_1_1_, "stats", (JsonArray)null);
         if (lvt_6_1_ != null) {
            Iterator var7 = lvt_6_1_.iterator();

            while(var7.hasNext()) {
               JsonElement lvt_8_1_ = (JsonElement)var7.next();
               JsonObject lvt_9_1_ = JSONUtils.getJsonObject(lvt_8_1_, "stats entry");
               ResourceLocation lvt_10_1_ = new ResourceLocation(JSONUtils.getString(lvt_9_1_, "type"));
               StatType<?> lvt_11_1_ = (StatType)Registry.STATS.getOrDefault(lvt_10_1_);
               if (lvt_11_1_ == null) {
                  throw new JsonParseException("Invalid stat type: " + lvt_10_1_);
               }

               ResourceLocation lvt_12_1_ = new ResourceLocation(JSONUtils.getString(lvt_9_1_, "stat"));
               Stat<?> lvt_13_1_ = func_226997_a_(lvt_11_1_, lvt_12_1_);
               MinMaxBounds.IntBound lvt_14_1_ = MinMaxBounds.IntBound.fromJson(lvt_9_1_.get("value"));
               lvt_5_1_.put(lvt_13_1_, lvt_14_1_);
            }
         }

         Object2BooleanMap<ResourceLocation> lvt_7_1_ = new Object2BooleanOpenHashMap();
         JsonObject lvt_8_2_ = JSONUtils.getJsonObject(lvt_1_1_, "recipes", new JsonObject());
         Iterator var17 = lvt_8_2_.entrySet().iterator();

         while(var17.hasNext()) {
            Entry<String, JsonElement> lvt_10_2_ = (Entry)var17.next();
            ResourceLocation lvt_11_2_ = new ResourceLocation((String)lvt_10_2_.getKey());
            boolean lvt_12_2_ = JSONUtils.getBoolean((JsonElement)lvt_10_2_.getValue(), "recipe present");
            lvt_7_1_.put(lvt_11_2_, lvt_12_2_);
         }

         Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> lvt_9_2_ = Maps.newHashMap();
         JsonObject lvt_10_3_ = JSONUtils.getJsonObject(lvt_1_1_, "advancements", new JsonObject());
         Iterator var22 = lvt_10_3_.entrySet().iterator();

         while(var22.hasNext()) {
            Entry<String, JsonElement> lvt_12_3_ = (Entry)var22.next();
            ResourceLocation lvt_13_2_ = new ResourceLocation((String)lvt_12_3_.getKey());
            PlayerPredicate.IAdvancementPredicate lvt_14_2_ = func_227004_b_((JsonElement)lvt_12_3_.getValue());
            lvt_9_2_.put(lvt_13_2_, lvt_14_2_);
         }

         return new PlayerPredicate(lvt_2_1_, lvt_4_1_, lvt_5_1_, lvt_7_1_, lvt_9_2_);
      } else {
         return field_226989_a_;
      }
   }

   private static <T> Stat<T> func_226997_a_(StatType<T> p_226997_0_, ResourceLocation p_226997_1_) {
      Registry<T> lvt_2_1_ = p_226997_0_.getRegistry();
      T lvt_3_1_ = lvt_2_1_.getOrDefault(p_226997_1_);
      if (lvt_3_1_ == null) {
         throw new JsonParseException("Unknown object " + p_226997_1_ + " for stat type " + Registry.STATS.getKey(p_226997_0_));
      } else {
         return p_226997_0_.get(lvt_3_1_);
      }
   }

   private static <T> ResourceLocation func_226996_a_(Stat<T> p_226996_0_) {
      return p_226996_0_.getType().getRegistry().getKey(p_226996_0_.getValue());
   }

   public JsonElement func_226995_a_() {
      if (this == field_226989_a_) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("level", this.field_226990_b_.serialize());
         if (this.field_226991_c_ != GameType.NOT_SET) {
            lvt_1_1_.addProperty("gamemode", this.field_226991_c_.getName());
         }

         if (!this.field_226992_d_.isEmpty()) {
            JsonArray lvt_2_1_ = new JsonArray();
            this.field_226992_d_.forEach((p_226999_1_, p_226999_2_) -> {
               JsonObject lvt_3_1_ = new JsonObject();
               lvt_3_1_.addProperty("type", Registry.STATS.getKey(p_226999_1_.getType()).toString());
               lvt_3_1_.addProperty("stat", func_226996_a_(p_226999_1_).toString());
               lvt_3_1_.add("value", p_226999_2_.serialize());
               lvt_2_1_.add(lvt_3_1_);
            });
            lvt_1_1_.add("stats", lvt_2_1_);
         }

         JsonObject lvt_2_3_;
         if (!this.field_226993_e_.isEmpty()) {
            lvt_2_3_ = new JsonObject();
            this.field_226993_e_.forEach((p_227002_1_, p_227002_2_) -> {
               lvt_2_3_.addProperty(p_227002_1_.toString(), p_227002_2_);
            });
            lvt_1_1_.add("recipes", lvt_2_3_);
         }

         if (!this.field_226994_f_.isEmpty()) {
            lvt_2_3_ = new JsonObject();
            this.field_226994_f_.forEach((p_227001_1_, p_227001_2_) -> {
               lvt_2_3_.add(p_227001_1_.toString(), p_227001_2_.func_225544_a_());
            });
            lvt_1_1_.add("advancements", lvt_2_3_);
         }

         return lvt_1_1_;
      }
   }

   // $FF: synthetic method
   PlayerPredicate(MinMaxBounds.IntBound p_i225771_1_, GameType p_i225771_2_, Map p_i225771_3_, Object2BooleanMap p_i225771_4_, Map p_i225771_5_, Object p_i225771_6_) {
      this(p_i225771_1_, p_i225771_2_, p_i225771_3_, p_i225771_4_, p_i225771_5_);
   }

   public static class Default {
      private MinMaxBounds.IntBound field_227007_a_;
      private GameType field_227008_b_;
      private final Map<Stat<?>, MinMaxBounds.IntBound> field_227009_c_;
      private final Object2BooleanMap<ResourceLocation> field_227010_d_;
      private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> field_227011_e_;

      public Default() {
         this.field_227007_a_ = MinMaxBounds.IntBound.UNBOUNDED;
         this.field_227008_b_ = GameType.NOT_SET;
         this.field_227009_c_ = Maps.newHashMap();
         this.field_227010_d_ = new Object2BooleanOpenHashMap();
         this.field_227011_e_ = Maps.newHashMap();
      }

      public PlayerPredicate func_227012_b_() {
         return new PlayerPredicate(this.field_227007_a_, this.field_227008_b_, this.field_227009_c_, this.field_227010_d_, this.field_227011_e_);
      }
   }

   static class CriteriaPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final Object2BooleanMap<String> field_227005_a_;

      public CriteriaPredicate(Object2BooleanMap<String> p_i225772_1_) {
         this.field_227005_a_ = p_i225772_1_;
      }

      public JsonElement func_225544_a_() {
         JsonObject lvt_1_1_ = new JsonObject();
         this.field_227005_a_.forEach(lvt_1_1_::addProperty);
         return lvt_1_1_;
      }

      public boolean test(AdvancementProgress p_test_1_) {
         ObjectIterator var2 = this.field_227005_a_.object2BooleanEntrySet().iterator();

         it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry lvt_3_1_;
         CriterionProgress lvt_4_1_;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            lvt_3_1_ = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry)var2.next();
            lvt_4_1_ = p_test_1_.getCriterionProgress((String)lvt_3_1_.getKey());
         } while(lvt_4_1_ != null && lvt_4_1_.isObtained() == lvt_3_1_.getBooleanValue());

         return false;
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((AdvancementProgress)p_test_1_);
      }
   }

   static class CompletedAdvancementPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final boolean field_227006_a_;

      public CompletedAdvancementPredicate(boolean p_i225773_1_) {
         this.field_227006_a_ = p_i225773_1_;
      }

      public JsonElement func_225544_a_() {
         return new JsonPrimitive(this.field_227006_a_);
      }

      public boolean test(AdvancementProgress p_test_1_) {
         return p_test_1_.isDone() == this.field_227006_a_;
      }

      // $FF: synthetic method
      public boolean test(Object p_test_1_) {
         return this.test((AdvancementProgress)p_test_1_);
      }
   }

   interface IAdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement func_225544_a_();
   }
}
