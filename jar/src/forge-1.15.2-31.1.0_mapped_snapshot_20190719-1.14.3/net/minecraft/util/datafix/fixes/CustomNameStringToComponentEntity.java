package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CustomNameStringToComponentEntity extends DataFix {
   public CustomNameStringToComponentEntity(Schema p_i49669_1_, boolean p_i49669_2_) {
      super(p_i49669_1_, p_i49669_2_);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<String> lvt_1_1_ = DSL.fieldFinder("id", DSL.namespacedString());
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_207792_1_) -> {
         return p_207792_1_.update(DSL.remainderFinder(), (p_207791_2_) -> {
            Optional<String> lvt_3_1_ = p_207792_1_.getOptional(lvt_1_1_);
            return lvt_3_1_.isPresent() && Objects.equals(lvt_3_1_.get(), "minecraft:commandblock_minecart") ? p_207791_2_ : fixTagCustomName(p_207791_2_);
         });
      });
   }

   public static Dynamic<?> fixTagCustomName(Dynamic<?> p_209740_0_) {
      String lvt_1_1_ = p_209740_0_.get("CustomName").asString("");
      return lvt_1_1_.isEmpty() ? p_209740_0_.remove("CustomName") : p_209740_0_.set("CustomName", p_209740_0_.createString(ITextComponent.Serializer.toJson(new StringTextComponent(lvt_1_1_))));
   }
}
