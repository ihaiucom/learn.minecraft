package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.datafix.TypeReferences;

public class ObjectiveRenderType extends DataFix {
   public ObjectiveRenderType(Schema p_i49781_1_, boolean p_i49781_2_) {
      super(p_i49781_1_, p_i49781_2_);
   }

   private static ScoreCriteria.RenderType getRenderType(String p_211858_0_) {
      return p_211858_0_.equals("health") ? ScoreCriteria.RenderType.HEARTS : ScoreCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> lvt_1_1_ = DSL.named(TypeReferences.OBJECTIVE.typeName(), DSL.remainderType());
      if (!Objects.equals(lvt_1_1_, this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveRenderTypeFix", lvt_1_1_, (p_211859_0_) -> {
            return (p_211860_0_) -> {
               return p_211860_0_.mapSecond((p_211857_0_) -> {
                  Optional<String> lvt_1_1_ = p_211857_0_.get("RenderType").asString();
                  if (!lvt_1_1_.isPresent()) {
                     String lvt_2_1_ = p_211857_0_.get("CriteriaName").asString("");
                     ScoreCriteria.RenderType lvt_3_1_ = getRenderType(lvt_2_1_);
                     return p_211857_0_.set("RenderType", p_211857_0_.createString(lvt_3_1_.getId()));
                  } else {
                     return p_211857_0_;
                  }
               });
            };
         });
      }
   }
}
