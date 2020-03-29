package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public abstract class TypedEntityRenameHelper extends DataFix {
   private final String name;

   public TypedEntityRenameHelper(String p_i49713_1_, Schema p_i49713_2_, boolean p_i49713_3_) {
      super(p_i49713_2_, p_i49713_3_);
      this.name = p_i49713_1_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> lvt_1_1_ = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
      TaggedChoiceType<String> lvt_2_1_ = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
      Type<Pair<String, String>> lvt_3_1_ = DSL.named(TypeReferences.ENTITY_NAME.typeName(), DSL.namespacedString());
      if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), lvt_3_1_)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, lvt_1_1_, lvt_2_1_, (p_211306_3_) -> {
            return (p_211307_3_) -> {
               return p_211307_3_.mapFirst((p_211309_3_) -> {
                  String lvt_4_1_ = this.rename(p_211309_3_);
                  Type<?> lvt_5_1_ = (Type)lvt_1_1_.types().get(p_211309_3_);
                  Type<?> lvt_6_1_ = (Type)lvt_2_1_.types().get(lvt_4_1_);
                  if (!lvt_6_1_.equals(lvt_5_1_, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", lvt_6_1_, lvt_5_1_));
                  } else {
                     return lvt_4_1_;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.name + " for entity name", lvt_3_1_, (p_211308_1_) -> {
            return (p_211310_1_) -> {
               return p_211310_1_.mapSecond(this::rename);
            };
         }));
      }
   }

   protected abstract String rename(String var1);
}
