package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkLightRemoveFix extends DataFix {
   public ChunkLightRemoveFix(Schema p_i50431_1_, boolean p_i50431_2_) {
      super(p_i50431_1_, p_i50431_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = lvt_1_1_.findFieldType("Level");
      OpticFinder<?> lvt_3_1_ = DSL.fieldFinder("Level", lvt_2_1_);
      return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", lvt_1_1_, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219821_1_) -> {
         return p_219821_1_.updateTyped(lvt_3_1_, (p_219822_0_) -> {
            return p_219822_0_.update(DSL.remainderFinder(), (p_219820_0_) -> {
               return p_219820_0_.remove("isLightOn");
            });
         });
      });
   }
}
