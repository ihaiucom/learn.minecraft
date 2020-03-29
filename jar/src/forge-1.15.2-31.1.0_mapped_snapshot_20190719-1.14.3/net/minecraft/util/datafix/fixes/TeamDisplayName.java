package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TeamDisplayName extends DataFix {
   public TeamDisplayName(Schema p_i49779_1_, boolean p_i49779_2_) {
      super(p_i49779_1_, p_i49779_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> lvt_1_1_ = DSL.named(TypeReferences.TEAM.typeName(), DSL.remainderType());
      if (!Objects.equals(lvt_1_1_, this.getInputSchema().getType(TypeReferences.TEAM))) {
         throw new IllegalStateException("Team type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("TeamDisplayNameFix", lvt_1_1_, (p_211876_0_) -> {
            return (p_211877_0_) -> {
               return p_211877_0_.mapSecond((p_211875_0_) -> {
                  return p_211875_0_.update("DisplayName", (p_211878_1_) -> {
                     Optional var10000 = p_211878_1_.asString().map((p_211879_0_) -> {
                        return ITextComponent.Serializer.toJson(new StringTextComponent(p_211879_0_));
                     });
                     p_211875_0_.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(p_211875_0_::createString), p_211878_1_);
                  });
               });
            };
         });
      }
   }
}
