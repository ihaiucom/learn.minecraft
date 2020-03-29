package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;

public class BlockStateFlattenGenOptions extends DataFix {
   private static final Splitter field_199181_a = Splitter.on(';').limit(5);
   private static final Splitter field_199182_b = Splitter.on(',');
   private static final Splitter field_199183_c = Splitter.on('x').limit(2);
   private static final Splitter field_199184_d = Splitter.on('*').limit(2);
   private static final Splitter field_199185_e = Splitter.on(':').limit(3);

   public BlockStateFlattenGenOptions(Schema p_i49627_1_, boolean p_i49627_2_) {
      super(p_i49627_1_, p_i49627_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), (p_207414_1_) -> {
         return p_207414_1_.update(DSL.remainderFinder(), this::fix);
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_209636_1_) {
      return p_209636_1_.get("generatorName").asString("").equalsIgnoreCase("flat") ? p_209636_1_.update("generatorOptions", (p_209634_1_) -> {
         Optional var10000 = p_209634_1_.asString().map(this::fixString);
         p_209634_1_.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(p_209634_1_::createString), p_209634_1_);
      }) : p_209636_1_;
   }

   @VisibleForTesting
   String fixString(String p_199180_1_) {
      if (p_199180_1_.isEmpty()) {
         return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
      } else {
         Iterator<String> lvt_2_1_ = field_199181_a.split(p_199180_1_).iterator();
         String lvt_3_1_ = (String)lvt_2_1_.next();
         int lvt_4_2_;
         String lvt_5_2_;
         if (lvt_2_1_.hasNext()) {
            lvt_4_2_ = NumberUtils.toInt(lvt_3_1_, 0);
            lvt_5_2_ = (String)lvt_2_1_.next();
         } else {
            lvt_4_2_ = 0;
            lvt_5_2_ = lvt_3_1_;
         }

         if (lvt_4_2_ >= 0 && lvt_4_2_ <= 3) {
            StringBuilder lvt_6_1_ = new StringBuilder();
            Splitter lvt_7_1_ = lvt_4_2_ < 3 ? field_199183_c : field_199184_d;
            lvt_6_1_.append((String)StreamSupport.stream(field_199182_b.split(lvt_5_2_).spliterator(), false).map((p_206368_2_) -> {
               List<String> lvt_5_1_ = lvt_7_1_.splitToList(p_206368_2_);
               int lvt_3_2_;
               String lvt_4_2_x;
               if (lvt_5_1_.size() == 2) {
                  lvt_3_2_ = NumberUtils.toInt((String)lvt_5_1_.get(0));
                  lvt_4_2_x = (String)lvt_5_1_.get(1);
               } else {
                  lvt_3_2_ = 1;
                  lvt_4_2_x = (String)lvt_5_1_.get(0);
               }

               List<String> lvt_6_1_ = field_199185_e.splitToList(lvt_4_2_x);
               int lvt_7_1_x = ((String)lvt_6_1_.get(0)).equals("minecraft") ? 1 : 0;
               String lvt_8_1_ = (String)lvt_6_1_.get(lvt_7_1_x);
               int lvt_9_1_ = lvt_4_2_ == 3 ? BlockStateFlatternEntities.getBlockId("minecraft:" + lvt_8_1_) : NumberUtils.toInt(lvt_8_1_, 0);
               int lvt_10_1_ = lvt_7_1_x + 1;
               int lvt_11_1_ = lvt_6_1_.size() > lvt_10_1_ ? NumberUtils.toInt((String)lvt_6_1_.get(lvt_10_1_), 0) : 0;
               return (lvt_3_2_ == 1 ? "" : lvt_3_2_ + "*") + BlockStateFlatteningMap.getFixedNBTForID(lvt_9_1_ << 4 | lvt_11_1_).get("Name").asString("");
            }).collect(Collectors.joining(",")));

            while(lvt_2_1_.hasNext()) {
               lvt_6_1_.append(';').append((String)lvt_2_1_.next());
            }

            return lvt_6_1_.toString();
         } else {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
         }
      }
   }
}
