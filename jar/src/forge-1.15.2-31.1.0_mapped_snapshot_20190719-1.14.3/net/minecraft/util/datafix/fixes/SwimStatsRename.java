package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class SwimStatsRename extends DataFix {
   public SwimStatsRename(Schema p_i49754_1_, boolean p_i49754_2_) {
      super(p_i49754_1_, p_i49754_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.STATS);
      Type<?> lvt_2_1_ = this.getInputSchema().getType(TypeReferences.STATS);
      OpticFinder<?> lvt_3_1_ = lvt_2_1_.findField("stats");
      OpticFinder<?> lvt_4_1_ = lvt_3_1_.type().findField("minecraft:custom");
      OpticFinder<String> lvt_5_1_ = DSL.namespacedString().finder();
      return this.fixTypeEverywhereTyped("SwimStatsRenameFix", lvt_2_1_, lvt_1_1_, (p_211690_3_) -> {
         return p_211690_3_.updateTyped(lvt_3_1_, (p_211692_2_) -> {
            return p_211692_2_.updateTyped(lvt_4_1_, (p_211691_1_) -> {
               return p_211691_1_.update(lvt_5_1_, (p_211693_0_) -> {
                  if (p_211693_0_.equals("minecraft:swim_one_cm")) {
                     return "minecraft:walk_on_water_one_cm";
                  } else {
                     return p_211693_0_.equals("minecraft:dive_one_cm") ? "minecraft:walk_under_water_one_cm" : p_211693_0_;
                  }
               });
            });
         });
      });
   }
}
