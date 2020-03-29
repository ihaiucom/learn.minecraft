package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class VillagerTrades extends NamedEntityFix {
   public VillagerTrades(Schema p_i49614_1_, boolean p_i49614_2_) {
      super(p_i49614_1_, p_i49614_2_, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      OpticFinder<?> lvt_2_1_ = p_207419_1_.getType().findField("Offers");
      OpticFinder<?> lvt_3_1_ = lvt_2_1_.type().findField("Recipes");
      Type<?> lvt_4_1_ = lvt_3_1_.type();
      if (!(lvt_4_1_ instanceof ListType)) {
         throw new IllegalStateException("Recipes are expected to be a list.");
      } else {
         ListType<?> lvt_5_1_ = (ListType)lvt_4_1_;
         Type<?> lvt_6_1_ = lvt_5_1_.getElement();
         OpticFinder<?> lvt_7_1_ = DSL.typeFinder(lvt_6_1_);
         OpticFinder<?> lvt_8_1_ = lvt_6_1_.findField("buy");
         OpticFinder<?> lvt_9_1_ = lvt_6_1_.findField("buyB");
         OpticFinder<?> lvt_10_1_ = lvt_6_1_.findField("sell");
         OpticFinder<Pair<String, String>> lvt_11_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
         Function<Typed<?>, Typed<?>> lvt_12_1_ = (p_209284_2_) -> {
            return this.updateItemStack(lvt_11_1_, p_209284_2_);
         };
         return p_207419_1_.updateTyped(lvt_2_1_, (p_209285_6_) -> {
            return p_209285_6_.updateTyped(lvt_3_1_, (p_209287_5_) -> {
               return p_209287_5_.updateTyped(lvt_7_1_, (p_209286_4_) -> {
                  return p_209286_4_.updateTyped(lvt_8_1_, lvt_12_1_).updateTyped(lvt_9_1_, lvt_12_1_).updateTyped(lvt_10_1_, lvt_12_1_);
               });
            });
         });
      }
   }

   private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> p_210482_1_, Typed<?> p_210482_2_) {
      return p_210482_2_.update(p_210482_1_, (p_209288_0_) -> {
         return p_209288_0_.mapSecond((p_209289_0_) -> {
            return Objects.equals(p_209289_0_, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : p_209289_0_;
         });
      });
   }
}
