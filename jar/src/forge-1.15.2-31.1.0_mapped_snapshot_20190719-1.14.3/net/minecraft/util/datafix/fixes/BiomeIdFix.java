package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.datafix.TypeReferences;

public class BiomeIdFix extends DataFix {
   public BiomeIdFix(Schema p_i225701_1_, boolean p_i225701_2_) {
      super(p_i225701_1_, p_i225701_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("Level");
      return this.fixTypeEverywhereTyped("Leaves fix", lvt_1_1_, (p_226193_1_) -> {
         return p_226193_1_.updateTyped(lvt_2_1_, (p_226194_0_) -> {
            return p_226194_0_.update(DSL.remainderFinder(), (p_226192_0_) -> {
               Optional<IntStream> lvt_1_1_ = p_226192_0_.get("Biomes").asIntStreamOpt();
               if (!lvt_1_1_.isPresent()) {
                  return p_226192_0_;
               } else {
                  int[] lvt_2_1_ = ((IntStream)lvt_1_1_.get()).toArray();
                  int[] lvt_3_1_ = new int[1024];

                  int lvt_4_2_;
                  for(lvt_4_2_ = 0; lvt_4_2_ < 4; ++lvt_4_2_) {
                     for(int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_) {
                        int lvt_6_1_ = (lvt_5_1_ << 2) + 2;
                        int lvt_7_1_ = (lvt_4_2_ << 2) + 2;
                        int lvt_8_1_ = lvt_7_1_ << 4 | lvt_6_1_;
                        lvt_3_1_[lvt_4_2_ << 2 | lvt_5_1_] = lvt_8_1_ < lvt_2_1_.length ? lvt_2_1_[lvt_8_1_] : -1;
                     }
                  }

                  for(lvt_4_2_ = 1; lvt_4_2_ < 64; ++lvt_4_2_) {
                     System.arraycopy(lvt_3_1_, 0, lvt_3_1_, lvt_4_2_ * 16, 16);
                  }

                  return p_226192_0_.set("Biomes", p_226192_0_.createIntList(Arrays.stream(lvt_3_1_)));
               }
            });
         });
      });
   }
}
