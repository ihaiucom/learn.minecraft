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
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PotionWater extends DataFix {
   public PotionWater(Schema p_i49631_1_, boolean p_i49631_2_) {
      super(p_i49631_1_, p_i49631_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWaterPotionFix", lvt_1_1_, (p_206363_2_) -> {
         Optional<Pair<String, String>> lvt_3_1_x = p_206363_2_.getOptional(lvt_2_1_);
         if (lvt_3_1_x.isPresent()) {
            String lvt_4_1_ = (String)((Pair)lvt_3_1_x.get()).getSecond();
            if ("minecraft:potion".equals(lvt_4_1_) || "minecraft:splash_potion".equals(lvt_4_1_) || "minecraft:lingering_potion".equals(lvt_4_1_) || "minecraft:tipped_arrow".equals(lvt_4_1_)) {
               Typed<?> lvt_5_1_ = p_206363_2_.getOrCreateTyped(lvt_3_1_);
               Dynamic<?> lvt_6_1_ = (Dynamic)lvt_5_1_.get(DSL.remainderFinder());
               if (!lvt_6_1_.get("Potion").asString().isPresent()) {
                  lvt_6_1_ = lvt_6_1_.set("Potion", lvt_6_1_.createString("minecraft:water"));
               }

               return p_206363_2_.set(lvt_3_1_, lvt_5_1_.set(DSL.remainderFinder(), lvt_6_1_));
            }
         }

         return p_206363_2_;
      });
   }
}
