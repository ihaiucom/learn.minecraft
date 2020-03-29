package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkStatusFix extends DataFix {
   public ChunkStatusFix(Schema p_i50430_1_, boolean p_i50430_2_) {
      super(p_i50430_1_, p_i50430_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = lvt_1_1_.findFieldType("Level");
      OpticFinder<?> lvt_3_1_ = DSL.fieldFinder("Level", lvt_2_1_);
      return this.fixTypeEverywhereTyped("ChunkStatusFix", lvt_1_1_, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219826_1_) -> {
         return p_219826_1_.updateTyped(lvt_3_1_, (p_219827_0_) -> {
            Dynamic<?> lvt_1_1_ = (Dynamic)p_219827_0_.get(DSL.remainderFinder());
            String lvt_2_1_ = lvt_1_1_.get("Status").asString("empty");
            if (Objects.equals(lvt_2_1_, "postprocessed")) {
               lvt_1_1_ = lvt_1_1_.set("Status", lvt_1_1_.createString("fullchunk"));
            }

            return p_219827_0_.set(DSL.remainderFinder(), lvt_1_1_);
         });
      });
   }
}
