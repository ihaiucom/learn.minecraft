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

public class OminousBannerRenameFix extends DataFix {
   public OminousBannerRenameFix(Schema p_i50433_1_, boolean p_i50433_2_) {
      super(p_i50433_1_, p_i50433_2_);
   }

   private Dynamic<?> func_219818_a(Dynamic<?> p_219818_1_) {
      Optional<? extends Dynamic<?>> lvt_2_1_ = p_219818_1_.get("display").get();
      if (lvt_2_1_.isPresent()) {
         Dynamic<?> lvt_3_1_ = (Dynamic)lvt_2_1_.get();
         Optional<String> lvt_4_1_ = lvt_3_1_.get("Name").asString();
         if (lvt_4_1_.isPresent()) {
            String lvt_5_1_ = (String)lvt_4_1_.get();
            lvt_5_1_ = lvt_5_1_.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            lvt_3_1_ = lvt_3_1_.set("Name", lvt_3_1_.createString(lvt_5_1_));
         }

         return p_219818_1_.set("display", lvt_3_1_);
      } else {
         return p_219818_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("tag");
      return this.fixTypeEverywhereTyped("OminousBannerRenameFix", lvt_1_1_, (p_219819_3_) -> {
         Optional<Pair<String, String>> lvt_4_1_ = p_219819_3_.getOptional(lvt_2_1_);
         if (lvt_4_1_.isPresent() && Objects.equals(((Pair)lvt_4_1_.get()).getSecond(), "minecraft:white_banner")) {
            Optional<? extends Typed<?>> lvt_5_1_ = p_219819_3_.getOptionalTyped(lvt_3_1_);
            if (lvt_5_1_.isPresent()) {
               Typed<?> lvt_6_1_ = (Typed)lvt_5_1_.get();
               Dynamic<?> lvt_7_1_ = (Dynamic)lvt_6_1_.get(DSL.remainderFinder());
               return p_219819_3_.set(lvt_3_1_, lvt_6_1_.set(DSL.remainderFinder(), this.func_219818_a(lvt_7_1_)));
            }
         }

         return p_219819_3_;
      });
   }
}
