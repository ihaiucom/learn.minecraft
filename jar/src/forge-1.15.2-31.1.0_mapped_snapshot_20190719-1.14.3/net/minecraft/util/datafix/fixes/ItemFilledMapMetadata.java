package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ItemFilledMapMetadata extends DataFix {
   public ItemFilledMapMetadata(Schema p_i49637_1_, boolean p_i49637_2_) {
      super(p_i49637_1_, p_i49637_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", lvt_1_1_, (p_206360_2_) -> {
         Optional<Pair<String, String>> lvt_3_1_x = p_206360_2_.getOptional(lvt_2_1_);
         if (lvt_3_1_x.isPresent() && Objects.equals(((Pair)lvt_3_1_x.get()).getSecond(), "minecraft:filled_map")) {
            Dynamic<?> lvt_4_1_ = (Dynamic)p_206360_2_.get(DSL.remainderFinder());
            Typed<?> lvt_5_1_ = p_206360_2_.getOrCreateTyped(lvt_3_1_);
            Dynamic<?> lvt_6_1_ = (Dynamic)lvt_5_1_.get(DSL.remainderFinder());
            lvt_6_1_ = lvt_6_1_.set("map", lvt_6_1_.createInt(lvt_4_1_.get("Damage").asInt(0)));
            return p_206360_2_.set(lvt_3_1_, lvt_5_1_.set(DSL.remainderFinder(), lvt_6_1_));
         } else {
            return p_206360_2_;
         }
      });
   }
}
