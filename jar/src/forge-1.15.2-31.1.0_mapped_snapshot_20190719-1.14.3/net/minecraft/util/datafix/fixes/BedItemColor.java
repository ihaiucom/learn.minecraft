package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class BedItemColor extends DataFix {
   public BedItemColor(Schema p_i49689_1_, boolean p_i49689_2_) {
      super(p_i49689_1_, p_i49689_2_);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> lvt_1_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(TypeReferences.ITEM_STACK), (p_207435_1_) -> {
         Optional<Pair<String, String>> lvt_2_1_ = p_207435_1_.getOptional(lvt_1_1_);
         if (lvt_2_1_.isPresent() && Objects.equals(((Pair)lvt_2_1_.get()).getSecond(), "minecraft:bed")) {
            Dynamic<?> lvt_3_1_ = (Dynamic)p_207435_1_.get(DSL.remainderFinder());
            if (lvt_3_1_.get("Damage").asInt(0) == 0) {
               return p_207435_1_.set(DSL.remainderFinder(), lvt_3_1_.set("Damage", lvt_3_1_.createShort((short)14)));
            }
         }

         return p_207435_1_;
      });
   }
}
