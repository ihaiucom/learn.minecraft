package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;

public class LevelDataGeneratorOptionsFix extends DataFix {
   static final Map<String, String> field_210553_a = (Map)Util.make(Maps.newHashMap(), (p_210550_0_) -> {
      p_210550_0_.put("0", "minecraft:ocean");
      p_210550_0_.put("1", "minecraft:plains");
      p_210550_0_.put("2", "minecraft:desert");
      p_210550_0_.put("3", "minecraft:mountains");
      p_210550_0_.put("4", "minecraft:forest");
      p_210550_0_.put("5", "minecraft:taiga");
      p_210550_0_.put("6", "minecraft:swamp");
      p_210550_0_.put("7", "minecraft:river");
      p_210550_0_.put("8", "minecraft:nether");
      p_210550_0_.put("9", "minecraft:the_end");
      p_210550_0_.put("10", "minecraft:frozen_ocean");
      p_210550_0_.put("11", "minecraft:frozen_river");
      p_210550_0_.put("12", "minecraft:snowy_tundra");
      p_210550_0_.put("13", "minecraft:snowy_mountains");
      p_210550_0_.put("14", "minecraft:mushroom_fields");
      p_210550_0_.put("15", "minecraft:mushroom_field_shore");
      p_210550_0_.put("16", "minecraft:beach");
      p_210550_0_.put("17", "minecraft:desert_hills");
      p_210550_0_.put("18", "minecraft:wooded_hills");
      p_210550_0_.put("19", "minecraft:taiga_hills");
      p_210550_0_.put("20", "minecraft:mountain_edge");
      p_210550_0_.put("21", "minecraft:jungle");
      p_210550_0_.put("22", "minecraft:jungle_hills");
      p_210550_0_.put("23", "minecraft:jungle_edge");
      p_210550_0_.put("24", "minecraft:deep_ocean");
      p_210550_0_.put("25", "minecraft:stone_shore");
      p_210550_0_.put("26", "minecraft:snowy_beach");
      p_210550_0_.put("27", "minecraft:birch_forest");
      p_210550_0_.put("28", "minecraft:birch_forest_hills");
      p_210550_0_.put("29", "minecraft:dark_forest");
      p_210550_0_.put("30", "minecraft:snowy_taiga");
      p_210550_0_.put("31", "minecraft:snowy_taiga_hills");
      p_210550_0_.put("32", "minecraft:giant_tree_taiga");
      p_210550_0_.put("33", "minecraft:giant_tree_taiga_hills");
      p_210550_0_.put("34", "minecraft:wooded_mountains");
      p_210550_0_.put("35", "minecraft:savanna");
      p_210550_0_.put("36", "minecraft:savanna_plateau");
      p_210550_0_.put("37", "minecraft:badlands");
      p_210550_0_.put("38", "minecraft:wooded_badlands_plateau");
      p_210550_0_.put("39", "minecraft:badlands_plateau");
      p_210550_0_.put("40", "minecraft:small_end_islands");
      p_210550_0_.put("41", "minecraft:end_midlands");
      p_210550_0_.put("42", "minecraft:end_highlands");
      p_210550_0_.put("43", "minecraft:end_barrens");
      p_210550_0_.put("44", "minecraft:warm_ocean");
      p_210550_0_.put("45", "minecraft:lukewarm_ocean");
      p_210550_0_.put("46", "minecraft:cold_ocean");
      p_210550_0_.put("47", "minecraft:deep_warm_ocean");
      p_210550_0_.put("48", "minecraft:deep_lukewarm_ocean");
      p_210550_0_.put("49", "minecraft:deep_cold_ocean");
      p_210550_0_.put("50", "minecraft:deep_frozen_ocean");
      p_210550_0_.put("127", "minecraft:the_void");
      p_210550_0_.put("129", "minecraft:sunflower_plains");
      p_210550_0_.put("130", "minecraft:desert_lakes");
      p_210550_0_.put("131", "minecraft:gravelly_mountains");
      p_210550_0_.put("132", "minecraft:flower_forest");
      p_210550_0_.put("133", "minecraft:taiga_mountains");
      p_210550_0_.put("134", "minecraft:swamp_hills");
      p_210550_0_.put("140", "minecraft:ice_spikes");
      p_210550_0_.put("149", "minecraft:modified_jungle");
      p_210550_0_.put("151", "minecraft:modified_jungle_edge");
      p_210550_0_.put("155", "minecraft:tall_birch_forest");
      p_210550_0_.put("156", "minecraft:tall_birch_hills");
      p_210550_0_.put("157", "minecraft:dark_forest_hills");
      p_210550_0_.put("158", "minecraft:snowy_taiga_mountains");
      p_210550_0_.put("160", "minecraft:giant_spruce_taiga");
      p_210550_0_.put("161", "minecraft:giant_spruce_taiga_hills");
      p_210550_0_.put("162", "minecraft:modified_gravelly_mountains");
      p_210550_0_.put("163", "minecraft:shattered_savanna");
      p_210550_0_.put("164", "minecraft:shattered_savanna_plateau");
      p_210550_0_.put("165", "minecraft:eroded_badlands");
      p_210550_0_.put("166", "minecraft:modified_wooded_badlands_plateau");
      p_210550_0_.put("167", "minecraft:modified_badlands_plateau");
   });

   public LevelDataGeneratorOptionsFix(Schema p_i49628_1_, boolean p_i49628_2_) {
      super(p_i49628_1_, p_i49628_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.LEVEL);
      return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(TypeReferences.LEVEL), lvt_1_1_, (p_210545_1_) -> {
         Dynamic<?> lvt_2_1_ = p_210545_1_.write();
         Optional<String> lvt_3_1_ = lvt_2_1_.get("generatorOptions").asString();
         Dynamic lvt_4_3_;
         if ("flat".equalsIgnoreCase(lvt_2_1_.get("generatorName").asString(""))) {
            String lvt_5_1_ = (String)lvt_3_1_.orElse("");
            lvt_4_3_ = lvt_2_1_.set("generatorOptions", convert(lvt_5_1_, lvt_2_1_.getOps()));
         } else if ("buffet".equalsIgnoreCase(lvt_2_1_.get("generatorName").asString("")) && lvt_3_1_.isPresent()) {
            Dynamic<JsonElement> lvt_5_2_ = new Dynamic(JsonOps.INSTANCE, JSONUtils.fromJson((String)lvt_3_1_.get(), true));
            lvt_4_3_ = lvt_2_1_.set("generatorOptions", lvt_5_2_.convert(lvt_2_1_.getOps()));
         } else {
            lvt_4_3_ = lvt_2_1_;
         }

         return (Typed)((Optional)lvt_1_1_.readTyped(lvt_4_3_).getSecond()).orElseThrow(() -> {
            return new IllegalStateException("Could not read new level type.");
         });
      });
   }

   private static <T> Dynamic<T> convert(String p_210549_0_, DynamicOps<T> p_210549_1_) {
      Iterator<String> lvt_2_1_ = Splitter.on(';').split(p_210549_0_).iterator();
      String lvt_4_1_ = "minecraft:plains";
      Map<String, Map<String, String>> lvt_5_1_ = Maps.newHashMap();
      Object lvt_3_2_;
      if (!p_210549_0_.isEmpty() && lvt_2_1_.hasNext()) {
         lvt_3_2_ = getLayersInfoFromString((String)lvt_2_1_.next());
         if (!((List)lvt_3_2_).isEmpty()) {
            if (lvt_2_1_.hasNext()) {
               lvt_4_1_ = (String)field_210553_a.getOrDefault(lvt_2_1_.next(), "minecraft:plains");
            }

            if (lvt_2_1_.hasNext()) {
               String[] lvt_6_1_ = ((String)lvt_2_1_.next()).toLowerCase(Locale.ROOT).split(",");
               String[] var7 = lvt_6_1_;
               int var8 = lvt_6_1_.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String lvt_10_1_ = var7[var9];
                  String[] lvt_11_1_ = lvt_10_1_.split("\\(", 2);
                  if (!lvt_11_1_[0].isEmpty()) {
                     lvt_5_1_.put(lvt_11_1_[0], Maps.newHashMap());
                     if (lvt_11_1_.length > 1 && lvt_11_1_[1].endsWith(")") && lvt_11_1_[1].length() > 1) {
                        String[] lvt_12_1_ = lvt_11_1_[1].substring(0, lvt_11_1_[1].length() - 1).split(" ");
                        String[] var13 = lvt_12_1_;
                        int var14 = lvt_12_1_.length;

                        for(int var15 = 0; var15 < var14; ++var15) {
                           String lvt_16_1_ = var13[var15];
                           String[] lvt_17_1_ = lvt_16_1_.split("=", 2);
                           if (lvt_17_1_.length == 2) {
                              ((Map)lvt_5_1_.get(lvt_11_1_[0])).put(lvt_17_1_[0], lvt_17_1_[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               lvt_5_1_.put("village", Maps.newHashMap());
            }
         }
      } else {
         lvt_3_2_ = Lists.newArrayList();
         ((List)lvt_3_2_).add(Pair.of(1, "minecraft:bedrock"));
         ((List)lvt_3_2_).add(Pair.of(2, "minecraft:dirt"));
         ((List)lvt_3_2_).add(Pair.of(1, "minecraft:grass_block"));
         lvt_5_1_.put("village", Maps.newHashMap());
      }

      T lvt_6_2_ = p_210549_1_.createList(((List)lvt_3_2_).stream().map((p_210547_1_) -> {
         return p_210549_1_.createMap(ImmutableMap.of(p_210549_1_.createString("height"), p_210549_1_.createInt((Integer)p_210547_1_.getFirst()), p_210549_1_.createString("block"), p_210549_1_.createString((String)p_210547_1_.getSecond())));
      }));
      T lvt_7_1_ = p_210549_1_.createMap((Map)lvt_5_1_.entrySet().stream().map((p_210544_1_) -> {
         return Pair.of(p_210549_1_.createString(((String)p_210544_1_.getKey()).toLowerCase(Locale.ROOT)), p_210549_1_.createMap((Map)((Map)p_210544_1_.getValue()).entrySet().stream().map((p_210551_1_) -> {
            return Pair.of(p_210549_1_.createString((String)p_210551_1_.getKey()), p_210549_1_.createString((String)p_210551_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(p_210549_1_, p_210549_1_.createMap(ImmutableMap.of(p_210549_1_.createString("layers"), lvt_6_2_, p_210549_1_.createString("biome"), p_210549_1_.createString(lvt_4_1_), p_210549_1_.createString("structures"), lvt_7_1_)));
   }

   @Nullable
   private static Pair<Integer, String> getLayerInfoFromString(String p_210548_0_) {
      String[] lvt_1_1_ = p_210548_0_.split("\\*", 2);
      int lvt_2_2_;
      if (lvt_1_1_.length == 2) {
         try {
            lvt_2_2_ = Integer.parseInt(lvt_1_1_[0]);
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         lvt_2_2_ = 1;
      }

      String lvt_3_2_ = lvt_1_1_[lvt_1_1_.length - 1];
      return Pair.of(lvt_2_2_, lvt_3_2_);
   }

   private static List<Pair<Integer, String>> getLayersInfoFromString(String p_210552_0_) {
      List<Pair<Integer, String>> lvt_1_1_ = Lists.newArrayList();
      String[] lvt_2_1_ = p_210552_0_.split(",");
      String[] var3 = lvt_2_1_;
      int var4 = lvt_2_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String lvt_6_1_ = var3[var5];
         Pair<Integer, String> lvt_7_1_ = getLayerInfoFromString(lvt_6_1_);
         if (lvt_7_1_ == null) {
            return Collections.emptyList();
         }

         lvt_1_1_.add(lvt_7_1_);
      }

      return lvt_1_1_;
   }
}
