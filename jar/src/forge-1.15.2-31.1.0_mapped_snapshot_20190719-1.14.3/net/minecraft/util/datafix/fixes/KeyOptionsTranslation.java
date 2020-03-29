package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.TypeReferences;

public class KeyOptionsTranslation extends DataFix {
   public KeyOptionsTranslation(Schema p_i49620_1_, boolean p_i49620_2_) {
      super(p_i49620_1_, p_i49620_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (p_209667_0_) -> {
         return p_209667_0_.update(DSL.remainderFinder(), (p_209668_0_) -> {
            return (Dynamic)p_209668_0_.getMapValues().map((p_209669_1_) -> {
               return p_209668_0_.createMap((Map)p_209669_1_.entrySet().stream().map((p_209666_1_) -> {
                  if (((Dynamic)p_209666_1_.getKey()).asString("").startsWith("key_")) {
                     String lvt_2_1_ = ((Dynamic)p_209666_1_.getValue()).asString("");
                     if (!lvt_2_1_.startsWith("key.mouse") && !lvt_2_1_.startsWith("scancode.")) {
                        return Pair.of(p_209666_1_.getKey(), p_209668_0_.createString("key.keyboard." + lvt_2_1_.substring("key.".length())));
                     }
                  }

                  return Pair.of(p_209666_1_.getKey(), p_209666_1_.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).orElse(p_209668_0_);
         });
      });
   }
}
