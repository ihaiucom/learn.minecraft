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
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class BannerItemColor extends DataFix {
   public BannerItemColor(Schema p_i49645_1_, boolean p_i49645_2_) {
      super(p_i49645_1_, p_i49645_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("tag");
      OpticFinder<?> lvt_4_1_ = lvt_3_1_.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemBannerColorFix", lvt_1_1_, (p_207466_3_) -> {
         Optional<Pair<String, String>> lvt_4_1_x = p_207466_3_.getOptional(lvt_2_1_);
         if (lvt_4_1_x.isPresent() && Objects.equals(((Pair)lvt_4_1_x.get()).getSecond(), "minecraft:banner")) {
            Dynamic<?> lvt_5_1_ = (Dynamic)p_207466_3_.get(DSL.remainderFinder());
            Optional<? extends Typed<?>> lvt_6_1_ = p_207466_3_.getOptionalTyped(lvt_3_1_);
            if (lvt_6_1_.isPresent()) {
               Typed<?> lvt_7_1_ = (Typed)lvt_6_1_.get();
               Optional<? extends Typed<?>> lvt_8_1_ = lvt_7_1_.getOptionalTyped(lvt_4_1_);
               if (lvt_8_1_.isPresent()) {
                  Typed<?> lvt_9_1_ = (Typed)lvt_8_1_.get();
                  Dynamic<?> lvt_10_1_ = (Dynamic)lvt_7_1_.get(DSL.remainderFinder());
                  Dynamic<?> lvt_11_1_ = (Dynamic)lvt_9_1_.getOrCreate(DSL.remainderFinder());
                  if (lvt_11_1_.get("Base").asNumber().isPresent()) {
                     lvt_5_1_ = lvt_5_1_.set("Damage", lvt_5_1_.createShort((short)(lvt_11_1_.get("Base").asInt(0) & 15)));
                     Optional<? extends Dynamic<?>> lvt_12_1_ = lvt_10_1_.get("display").get();
                     if (lvt_12_1_.isPresent()) {
                        Dynamic<?> lvt_13_1_ = (Dynamic)lvt_12_1_.get();
                        if (Objects.equals(lvt_13_1_, lvt_13_1_.emptyMap().merge(lvt_13_1_.createString("Lore"), lvt_13_1_.createList(Stream.of(lvt_13_1_.createString("(+NBT")))))) {
                           return p_207466_3_.set(DSL.remainderFinder(), lvt_5_1_);
                        }
                     }

                     lvt_11_1_.remove("Base");
                     return p_207466_3_.set(DSL.remainderFinder(), lvt_5_1_).set(lvt_3_1_, lvt_7_1_.set(lvt_4_1_, lvt_9_1_.set(DSL.remainderFinder(), lvt_11_1_)));
                  }
               }
            }

            return p_207466_3_.set(DSL.remainderFinder(), lvt_5_1_);
         } else {
            return p_207466_3_;
         }
      });
   }
}
